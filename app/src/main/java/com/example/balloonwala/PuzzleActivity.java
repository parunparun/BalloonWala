package com.example.balloonwala;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.balloonwala.helpers.AnimationHelper;
import com.example.balloonwala.helpers.ButtonManager;
import com.example.balloonwala.helpers.ButtonStyleHelper;
import com.example.balloonwala.helpers.CelebrationHelper;
import com.example.balloonwala.helpers.GameState;
import com.example.balloonwala.helpers.GameTimer;
import com.example.balloonwala.helpers.HintHelper;
import com.example.balloonwala.helpers.SolutionHelper;
import com.example.balloonwala.helpers.SoundHelper;
import com.example.balloonwala.helpers.SpeechHelper;
import com.example.balloonwala.helpers.StickerHelper;
import com.example.balloonwala.helpers.TileStyleHelper;
import com.example.balloonwala.helpers.UIHelper;
import com.example.balloonwala.model.Move;
import com.example.balloonwala.solver.PuzzleSolver;
import com.example.balloonwala.utils.GenericUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * PuzzleActivity — pure orchestrator.
 * <p>
 * Wires together:
 *  - ButtonManager      →  tile button setup and lookup
 *  - GameTimer          →  timer start/stop/pause/resume
 *  - GameState          →  move count, undo stack, solved check
 *  - UIHelper           →  UI updates and dialogs
 *  - TileStyleHelper    →  fixed colors per tile number
 *  - CelebrationHelper  →  balloon shower and win overlay
 *  - AnimationHelper    →  smooth tile slide animation
 *  - SoundHelper        →  tile tap, win fanfare, background music
 *  - HintHelper         →  pulse animation on next correct tile
 *  - SolutionHelper     →  autoplay step-by-step solution
 *  - PuzzleSolver       →  IDA* solver running on background thread
 */

public class PuzzleActivity extends AppCompatActivity
        implements ButtonManager.OnTileClickListener {

    private int columns = AppConstants.FIFTEEN_PUZZLE;

    // ── Game helpers ──────────────────────────────────────
    private ButtonManager     buttonManager;
    private GameTimer         gameTimer;
    private GameState         gameState;
    private UIHelper          uiHelper;
    private CelebrationHelper celebrationHelper;
    private SoundHelper       soundHelper;
    private SpeechHelper      speechHelper;
    private StickerHelper     stickerHelper;
    private HintHelper hintHelper;
    private SolutionHelper solutionHelper;

    // ── Cached views ──────────────────────────────────────
    private Button btnHelp;
    private Button btnHint;
    private Button btnSolution;
    private Button btnStickers;

    // ── Solver infrastructure ─────────────────────────────
    private final ExecutorService solverExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler    = new Handler(Looper.getMainLooper());

    // ── State flags ───────────────────────────────────────

    /**
     * Incremented every time a new game starts. Used to invalidate
     * background solver tasks from previous game sessions.
     */
    private int solverTaskId = 0;

    /**
     * True if the player used Hint or Solution during this game.
     * Changes the win message to "Solved with help!" to encourage
     * the child to try without assistance next time.
     */
    private boolean assistedSolve = false;

    /**
     * Prevents multiple calls to onPuzzleSolved during state transitions.
     */
    private boolean puzzleAlreadySolved = false;

    /**
     * True if the "Pick Sticker" dialog is currently visible.
     */
    private boolean isPickingSticker = false;


    // ── Lifecycle ─────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PuzzleDebug", "onCreate called (savedInstanceState is " + (savedInstanceState != null) + ")");
        setContentView(R.layout.puzzle);

        setSupportActionBar(findViewById(R.id.toolbar));

        columns = getIntent().getIntExtra(NavigationConstants.COLUMNS, AppConstants.FIFTEEN_PUZZLE);
        setTitle(columns == AppConstants.EIGHT_PUZZLE? R.string.eight_puzzle : R.string.fifteen_puzzle);

        // SoundHelper lives in Application — shared with MainActivity
        BalloonWalaApp app = (BalloonWalaApp) getApplication();
        soundHelper = app.getSoundHelper();
        speechHelper = app.getSpeechHelper();
        stickerHelper = app.getStickerHelper();
        soundHelper.pauseMusic();

        initialiseHelpers();
        
        if (savedInstanceState == null) {
            startNewGame();
        } else {
            restoreGame(savedInstanceState);
        }
    }

    private void restoreGame(Bundle savedInstanceState) {
        int[] board = savedInstanceState.getIntArray("board_state");
        if (board != null) {
            List<Button> buttons = buttonManager.getButtonList();
            for (int i = 0; i < buttons.size() && i < board.length; i++) {
                int number = board[i];
                buttons.get(i).setText(number == 0 ? "" : String.valueOf(number));
            }
            // Sync UI state immediately
            TileStyleHelper.applyStyleToAll(buttons);
        }
        
        assistedSolve = savedInstanceState.getBoolean("assisted", false);
        puzzleAlreadySolved = savedInstanceState.getBoolean("is_solved", false);
        isPickingSticker = savedInstanceState.getBoolean("is_picking", false);
        int steps = savedInstanceState.getInt("steps", 0);
        long elapsed = savedInstanceState.getLong("elapsed_time", 0);
        
        gameState.reset();
        gameState.setStepsCount(steps);
        uiHelper.updateMovesDisplay(steps);
        
        // Restore timer state
        if (elapsed > 0 && !puzzleAlreadySolved) {
            // We can't perfectly restore the Chronometer's internal state
            // without more complex logic, but we can at least show the count.
            gameTimer.start(); 
        }

        // Locked state preservation
        if (puzzleAlreadySolved) {
            GenericUtils.disableButtons(buttonManager.getButtonList());
            setHintSolutionEnabled(false);
            uiHelper.setUndoEnabled(false);
        } else {
            for (Button b : buttonManager.getButtonList()) b.setEnabled(true);
            setHintSolutionEnabled(true);
            uiHelper.setUndoEnabled(gameState.canUndo());
        }
        
        // Restore reward or celebration
        String stats = steps + " " + getString(R.string.steps) + "  ·  "
                + gameTimer.getFormattedTime();
        String result = assistedSolve
                ? getString(R.string.solved_with_help)
                : getString(R.string.puzzle_solved);

        if (isPickingSticker) {
            showPickStickerDialog(result, stats, false);
        } else if (savedInstanceState.getBoolean("celebration_showing", false)) {
            celebrationHelper.showCelebration(result + "\n" + stats, false, null, this::startNewGame);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("PuzzleDebug", "onPause called");
        gameTimer.pause();
        
        // BUG FIX: Always pause music when leaving the game screen.
        // Even though music "should" be off, this prevents edge cases
        // where it might stay on (e.g. backgrounding via Home button).
        soundHelper.pauseMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("PuzzleDebug", "onResume called");
        gameTimer.resume();
        
        // Ensure music stays OFF when in the puzzle game
        soundHelper.pauseMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up Handler callbacks to prevent leaks
        // if Activity is destroyed while celebration is running
        celebrationHelper.hideCelebration();
        hintHelper.cancel();
        solutionHelper.stop();
        
        // BUG FIX: Immediate shutdown of solver tasks
        solverExecutor.shutdownNow();
        
        // BUG FIX: Removed resumeMusic() from here. 
        // PuzzleActivity should not be responsible for turning music back on.
        // MainActivity will handle resuming music in its own onResume()
        // when the player returns to the home screen.
    }

    // ── Initialise ────────────────────────────────────────

    private void initialiseHelpers() {
        // ButtonManager — passes 'this' as the OnTileClickListener
        buttonManager = new ButtonManager(this, columns, this);

        // GameTimer
        gameTimer = new GameTimer(
                findViewById(R.id.timerChronometer), this);

        // GameState
        gameState = new GameState(this);

        // UIHelper
        uiHelper = new UIHelper(
                this,
                 findViewById(R.id.movesCountTextView),
                 findViewById(R.id.btnUndo));

        btnHelp     = findViewById(R.id.btnHelp);
        btnHint     = findViewById(R.id.btnHint);
        btnSolution = findViewById(R.id.btnSolution);
        btnStickers = findViewById(R.id.btnStickers);

        celebrationHelper = new CelebrationHelper(this);
        hintHelper        = new HintHelper(findViewById(R.id.hintArrowView));
        solutionHelper    = new SolutionHelper();
        styleButtons();
    }

    // ── New Game ──────────────────────────────────────────

    private void startNewGame() {
        solverTaskId++; // Invalidate any pending solver results
        puzzleAlreadySolved = false;
        Log.d("PuzzleDebug", "startNewGame called");
        
        // Hide celebration if showing
        if (celebrationHelper.isShowing()) {
            celebrationHelper.hideCelebration();
        }
        
        // BUG FIX: Clear any active hints or solutions when starting a new game
        hintHelper.cancel();
        solutionHelper.stop();
        
        assistedSolve = false;
        // Simplified call — maximum is no longer needed
        GenericUtils.distributeData(0, buttonManager.getButtonList());
        
        // Apply styling immediately
        TileStyleHelper.applyStyleToAll(buttonManager.getButtonList());
        
        // Post-styling guard for Material Components initialization
        findViewById(R.id.statsBar).post(() ->
                TileStyleHelper.applyStyleToAll(buttonManager.getButtonList()));

        uiHelper.updateMovesDisplay(0);
        
        // Ensure UI buttons are in the correct state for a new game
        uiHelper.setUndoEnabled(false);
        setHintSolutionEnabled(true);
        
        for (Button b : buttonManager.getButtonList()) {
            b.setEnabled(true);
        }
        gameState.reset();
        gameTimer.stop();
        gameTimer.start();
    }

    // ── OnTileClickListener ───────────────────────────────

    /**
     * Called by ButtonManager when the player taps a tile.
     * Finds the empty neighbor and delegates swap to GenericUtils.
     */
    @Override
    public void onTileClicked(Button buttonPressed) {
        // Block taps during animation or on empty tile
        if (AnimationHelper.isAnimating()) return;
        if (StringUtils.isBlank(buttonPressed.getText())) return;

        ConstraintLayout.LayoutParams params =
                (ConstraintLayout.LayoutParams) buttonPressed.getLayoutParams();

        View above = findViewById(params.topToBottom);
        View below = findViewById(params.bottomToTop);
        View left  = findViewById(params.endToStart);
        View right = findViewById(params.startToEnd);

        Button emptyNeighbour = null;
        if      (GenericUtils.checkButton(above)) emptyNeighbour = (Button) above;
        else if (GenericUtils.checkButton(below)) emptyNeighbour = (Button) below;
        else if (GenericUtils.checkButton(left))  emptyNeighbour = (Button) left;
        else if (GenericUtils.checkButton(right)) emptyNeighbour = (Button) right;

        if (emptyNeighbour == null) return;

        // Play tap sound immediately — before animation starts
        soundHelper.playTileTap();

        final Button finalEmpty = emptyNeighbour;

        // Animate slide — swap text and update state after animation completes
        AnimationHelper.animateTileSlide(buttonPressed, finalEmpty, () -> {
            // Haptic feedback when animation completes
            buttonPressed.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            GenericUtils.swapData(buttonPressed, finalEmpty);
            gameState.addMove(buttonPressed, finalEmpty);

            TileStyleHelper.applyStyle(buttonPressed);
            TileStyleHelper.applyStyle(finalEmpty);

            uiHelper.updateMovesDisplay(gameState.getStepsCount());
            uiHelper.setUndoEnabled(gameState.canUndo());

            if (gameState.isSolved(columns, buttonManager.getButtonList())) {
                onPuzzleSolved();
            }
        });
        hintHelper.cancel();
    }

    // ── Undo ──────────────────────────────────────────────

    public void undo(View view) {

        // Block undo during animation
        if (AnimationHelper.isAnimating()) return;

        Move lastMove = gameState.undoLastMove();
        if (lastMove == null) return;

        soundHelper.playTileTap();
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        GenericUtils.swapData(lastMove.getToButton(), lastMove.getFromButton());

        // Reapply colors after undo swap
        TileStyleHelper.applyStyle(lastMove.getToButton());
        TileStyleHelper.applyStyle(lastMove.getFromButton());

        uiHelper.updateMovesDisplay(gameState.getStepsCount());
        uiHelper.setUndoEnabled(gameState.canUndo());
    }

    // ── Hint ──────────────────────────────────────────────

    public void showHint(View view) {
        if (AnimationHelper.isAnimating()) return;
        if (solutionHelper.isPlaying())    return;

        if (gameState.isSolved(columns, buttonManager.getButtonList())) return;

        final int currentTaskId = solverTaskId;
        setHintSolutionEnabled(false);

        int[]     board    = GenericUtils.extractBoard(buttonManager.getButtonList());
        int       gridSize = (int) Math.round(Math.sqrt(columns));

        solverExecutor.execute(() -> {
            List<Integer> moves = new PuzzleSolver().solve(board, gridSize);

            mainHandler.post(() -> {
                // Guards: Activity alive AND we are still in the same game session
                if (isDestroyed() || isFinishing() || currentTaskId != solverTaskId) return;

                setHintSolutionEnabled(true);

                if (moves.isEmpty()) {
                    Toast.makeText(PuzzleActivity.this,
                            "That's a tough one! Try moving a tile first.", 
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                int    nextTile   = moves.get(0);
                Button hintButton = GenericUtils.findButtonForTile(
                        nextTile, buttonManager.getButtonList());
                Button emptyButton = GenericUtils.findEmptyButton(
                        buttonManager.getButtonList());

                if (hintButton != null && emptyButton != null) {
                    // Only mark assisted when hint is actually shown
                    assistedSolve = true;
                    hintHelper.showHint(hintButton, emptyButton);
                }
            });
        });
    }

    // ── Solution ──────────────────────────────────────────

    public void showSolution(View view) {
        if (AnimationHelper.isAnimating()) return;
        if (solutionHelper.isPlaying())    return;

        uiHelper.showConfirmDialog(
                getString(R.string.solution_confirm_title),
                R.string.solution_confirm_message,
                this::startSolvingInBackground);
    }

    private void startSolvingInBackground() {
        assistedSolve = true;
        setHintSolutionEnabled(false);
        uiHelper.setUndoEnabled(false);
        
        // BUG FIX: Clear any active hints before starting the automated solution
        hintHelper.cancel();

        // Fix 2: Disable tiles immediately so player cannot corrupt board
        // while solver is running (can take up to 5 seconds for 15-puzzle)
        for (Button b : buttonManager.getButtonList()) b.setEnabled(false);

        int[]     board    = GenericUtils.extractBoard(buttonManager.getButtonList());
        int       gridSize = (int) Math.round(Math.sqrt(columns));

        solverExecutor.execute(() -> {
            List<Integer> moves = new PuzzleSolver().solve(board, gridSize);

            mainHandler.post(() -> {
                // Fix 3: Guard against Activity being destroyed while solver ran
                if (isDestroyed() || isFinishing()) return;

                if (moves.isEmpty()) {
                    // Timed out or already solved — restore UI
                    Toast.makeText(PuzzleActivity.this,
                            "Almost there! Try solving the last few tiles yourself.", 
                            Toast.LENGTH_SHORT).show();
                    for (Button b : buttonManager.getButtonList()) b.setEnabled(true);
                    setHintSolutionEnabled(true);
                    uiHelper.setUndoEnabled(gameState.canUndo());
                    return;
                }

                // Disable tiles — player watches, not plays
                for (Button b : buttonManager.getButtonList()) b.setEnabled(false);

                solutionHelper.playSolution(
                        moves,
                        buttonManager.getButtonList(),
                        this::onPuzzleSolved);
            });
        });
    }

    // ── Solved ────────────────────────────────────────────

    private void onPuzzleSolved() {
        // Guard: Don't solve twice or if no moves made
        if (puzzleAlreadySolved || gameState.getStepsCount() == 0) return;
        puzzleAlreadySolved = true;
        
        Log.d("PuzzleDebug", "Puzzle solved! Moves: " + gameState.getStepsCount());
        
        gameTimer.stop();
        
        // LOCK THE GAME: Disable tiles and gameplay controls
        GenericUtils.disableButtons(buttonManager.getButtonList());
        uiHelper.setUndoEnabled(false);
        setHintSolutionEnabled(false);

        soundHelper.playWinFanfare();
        hintHelper.cancel(); // clear hint arrow and glow before celebration

        boolean newBestTime  = gameTimer.checkAndSaveBestTime(columns);
        boolean newBestMoves = gameState.checkAndSaveBestMoves(columns);

        String result = assistedSolve
                ? getString(R.string.solved_with_help)
                : getString(R.string.puzzle_solved);

        String stats = gameState.getStepsCount() + " " + getString(R.string.steps) + "  ·  "
                + gameTimer.getFormattedTime();

        // Voice guidance on win
        speechHelper.speak(result + "! " + stats);

        // Unlock sticker for unassisted solve
        if (!assistedSolve) {
            showPickStickerDialog(result, stats, newBestTime || newBestMoves);
        } else {
            celebrationHelper.showCelebration(
                    result + "\n" + stats,
                    false,
                    null,
                    this::startNewGame);
        }
    }

    private void showPickStickerDialog(String result, String stats, boolean isNewBest) {
        isPickingSticker = true;
        List<String> options = stickerHelper.getRewardOptions(3);
        
        if (options.isEmpty()) {
            isPickingSticker = false;
            // Already have all stickers
            celebrationHelper.showCelebration(result + "\n" + stats, isNewBest, "FULL", this::startNewGame);
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_pick_sticker, null);
        ViewGroup container = dialogView.findViewById(R.id.stickerOptionsContainer);
        
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        for (String sticker : options) {
            Button btn = new Button(this);
            btn.setText(sticker);
            btn.setTextSize(40);
            
            // Fixed: Use DP for button height instead of raw pixels
            int heightPx = (int) (100 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, heightPx, 1.0f);
            params.setMargins(8, 8, 8, 8);
            btn.setLayoutParams(params);
            
            // Use existing styling for buttons
            ButtonStyleHelper.stylePrimary(btn);
            
            btn.setOnClickListener(v -> {
                isPickingSticker = false;
                stickerHelper.unlockSticker(sticker);
                dialog.dismiss();
                // Now show the final celebration with the chosen sticker
                celebrationHelper.showCelebration(result + "\n" + stats, isNewBest, sticker, this::startNewGame);
            });
            container.addView(btn);
        }

        dialog.show();
    }

    // ── Play Again ────────────────────────────────────────

    public void playAgain(View view) {
        Log.d("play again", "Play Again Called");
        uiHelper.showConfirmDialog(
                getString(R.string.play_again_confirm_title),
                R.string.play_again_confirm_message,
                this::startNewGame);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("board_state", 
                GenericUtils.extractBoard(buttonManager.getButtonList()));
        outState.putBoolean("assisted", assistedSolve);
        outState.putBoolean("is_solved", puzzleAlreadySolved);
        outState.putBoolean("is_picking", isPickingSticker);
        outState.putInt("steps", gameState.getStepsCount());
        outState.putLong("elapsed_time", gameTimer.getElapsedMillis());
        outState.putBoolean("celebration_showing", celebrationHelper.isShowing());
    }

    // ── Back ──────────────────────────────────────────────

    public void backToMainActivity(View view) {
        uiHelper.showConfirmDialog(
                getString(R.string.back_to_main_menu_confirm_title),
                R.string.back_to_main_menu_confirm_message,
                () -> {
                    gameTimer.stop();
                    // BUG FIX: Just finish this activity to return to the existing MainActivity.
                    // This prevents creating a loop of multiple Activity instances in the backstack.
                    finish();
                });
    }

    // ── UI Helpers ────────────────────────────────────────

    private void setHintSolutionEnabled(boolean enabled) {
        if (btnHelp != null) {
            btnHelp.setEnabled(enabled);
            btnHelp.setAlpha(enabled ? 1.0f : 0.4f);
        }
        if (btnHint     != null) {
            btnHint.setEnabled(enabled);
            btnHint.setAlpha(enabled ? 1.0f : 0.4f);
        }
        if (btnSolution != null) {
            btnSolution.setEnabled(enabled);
            btnSolution.setAlpha(enabled ? 1.0f : 0.4f);
        }
        if (btnStickers != null) {
            btnStickers.setEnabled(enabled);
            btnStickers.setAlpha(enabled ? 1.0f : 0.4f);
        }
    }

    public void showStickerBook(View view) {
        uiHelper.showStickerBook(stickerHelper, () -> speechHelper.stop());
    }

    public void showHowToPlay(View view) {
        String rules = getString(R.string.how_to_play_rules);
        speechHelper.speak(rules);

        uiHelper.showInfoDialog(
                getString(R.string.how_to_play_title),
                R.string.how_to_play_rules,
                () -> speechHelper.stop());
    }

    private void styleButtons() {
        ButtonStyleHelper.styleHowToPlay(btnHelp);
        ButtonStyleHelper.stylePlayAgain(findViewById(R.id.btnPlayAgain));
        ButtonStyleHelper.styleUndo(findViewById(R.id.btnUndo));
        ButtonStyleHelper.styleMenu(findViewById(R.id.btnMenu));
        ButtonStyleHelper.styleHint(btnHint);
        ButtonStyleHelper.styleSolution(btnSolution);
        ButtonStyleHelper.styleStickerBook(btnStickers);
    }
}
