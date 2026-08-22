package com.example.balloonwala;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
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
    private HintHelper hintHelper;
    private SolutionHelper solutionHelper;

    // ── Cached views ──────────────────────────────────────
    private Button btnHint;
    private Button btnSolution;

    // ── Solver infrastructure ─────────────────────────────
    private final ExecutorService solverExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler    = new Handler(Looper.getMainLooper());

    // ── State flags ───────────────────────────────────────

    /**
     * True if the player used Hint or Solution during this game.
     * Changes the win message to "Solved with help!" to encourage
     * the child to try without assistance next time.
     */
    private boolean assistedSolve = false;


    // ── Lifecycle ─────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle);

        setSupportActionBar(findViewById(R.id.toolbar));

        columns = getIntent().getIntExtra(NavigationConstants.COLUMNS, AppConstants.FIFTEEN_PUZZLE);
        setTitle(columns == AppConstants.EIGHT_PUZZLE? R.string.eight_puzzle : R.string.fifteen_puzzle);

        // SoundHelper lives in Application — shared with MainActivity
        soundHelper = ((BalloonWalaApp) getApplication()).getSoundHelper();

        initialiseHelpers();
        startNewGame();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameTimer.pause();
        soundHelper.pauseMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameTimer.resume();
        soundHelper.resumeMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up Handler callbacks to prevent leaks
        // if Activity is destroyed while celebration is running
        celebrationHelper.hideCelebration();
        hintHelper.cancel();
        solutionHelper.stop();
        solverExecutor.shutdown();
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

        btnHint     = findViewById(R.id.btnHint);
        btnSolution = findViewById(R.id.btnSolution);

        celebrationHelper = new CelebrationHelper(this);
        hintHelper        = new HintHelper(findViewById(R.id.hintArrowView));
        solutionHelper    = new SolutionHelper();
        styleButtons();
    }

    // ── New Game ──────────────────────────────────────────

    private void startNewGame() {
        android.util.Log.d("chronometer", "chronometer called");
        // Hide celebration if showing
        if (celebrationHelper.isShowing()) {
            celebrationHelper.hideCelebration();
        }
        assistedSolve = false;
        GenericUtils.distributeData(columns + 1, buttonManager.getButtonList());
        // Apply fixed colors to all tiles after distribution
//        TileStyleHelper.applyStyleToAll(buttonManager.getButtonList());
        // Post ensures background is set AFTER Material Components
        // applies its default button styling
        findViewById(R.id.statsBar).post(() ->
                TileStyleHelper.applyStyleToAll(buttonManager.getButtonList()));
        uiHelper.updateMovesDisplay(0);
        uiHelper.setUndoEnabled(false);
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

        assistedSolve = true;
        setHintSolutionEnabled(false);

        int[]     board    = GenericUtils.extractBoard(buttonManager.getButtonList());
        int       gridSize = (int) Math.round(Math.sqrt(columns));

        solverExecutor.execute(() -> {
            List<Integer> moves = new PuzzleSolver().solve(board, gridSize);

            mainHandler.post(() -> {
                // Fix 3: Guard against Activity being destroyed while solver ran
                if (isDestroyed() || isFinishing()) return;

                setHintSolutionEnabled(true);

                if (moves.isEmpty()) return; // already solved or timed out

                // Only mark assisted when hint is actually shown
                assistedSolve = true;

                int    nextTile   = moves.get(0);
                Button hintButton = GenericUtils.findButtonForTile(
                        nextTile, buttonManager.getButtonList());
                Button emptyButton = GenericUtils.findEmptyButton(
                        buttonManager.getButtonList());

                if (hintButton != null && emptyButton != null) {
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
        gameTimer.stop();
        GenericUtils.disableButtons(buttonManager.getButtonList());
        soundHelper.playWinFanfare();
        hintHelper.cancel(); // clear hint arrow and glow before celebration

        boolean newBestTime  = gameTimer.checkAndSaveBestTime(columns);
        boolean newBestMoves = gameState.checkAndSaveBestMoves(columns);

        String result = assistedSolve
                ? getString(R.string.solved_with_help)
                : getString(R.string.puzzle_solved);

        String stats = gameState.getStepsCount() + " moves  ·  "
                + gameTimer.getFormattedTime();

        celebrationHelper.showCelebration(
                result + "\n" + stats,
                !assistedSolve && (newBestTime || newBestMoves),
                this::startNewGame);
    }

    // ── Play Again ────────────────────────────────────────

    public void playAgain(View view) {
        uiHelper.showConfirmDialog(
                getString(R.string.play_again_confirm_title),
                R.string.play_again_confirm_message,
                this::startNewGame);
    }

    // ── Back ──────────────────────────────────────────────

    public void backToMainActivity(View view) {
        uiHelper.showConfirmDialog(
                getString(R.string.back_to_main_menu_confirm_title),
                R.string.back_to_main_menu_confirm_message,
                () -> {
                    gameTimer.stop();
                    startActivity(new Intent(this, MainActivity.class));
                });
    }

    // ── UI Helpers ────────────────────────────────────────

    private void setHintSolutionEnabled(boolean enabled) {
        if (btnHint     != null) {
            btnHint.setEnabled(enabled);
            btnHint.setAlpha(enabled ? 1.0f : 0.4f);
        }
        if (btnSolution != null) {
            btnSolution.setEnabled(enabled);
            btnSolution.setAlpha(enabled ? 1.0f : 0.4f);
        }
    }

    private void styleButtons() {
        ButtonStyleHelper.stylePlayAgain(findViewById(R.id.btnPlayAgain));
        ButtonStyleHelper.styleUndo(findViewById(R.id.btnUndo));
        ButtonStyleHelper.styleMenu(findViewById(R.id.btnMenu));
        ButtonStyleHelper.styleHint(btnHint);
        ButtonStyleHelper.styleSolution(btnSolution);
    }
}
