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

public class PuzzleActivity extends AppCompatActivity
        implements ButtonManager.OnTileClickListener {

    private int columns = AppConstants.FIFTEEN_PUZZLE;

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

    private Button btnHelp;
    private Button btnHint;
    private Button btnSolution;
    private Button btnStickers;

    private final ExecutorService solverExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler    = new Handler(Looper.getMainLooper());

    private int solverTaskId = 0;
    private boolean assistedSolve = false;
    private boolean puzzleAlreadySolved = false;
    private boolean isPickingSticker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle);
        setSupportActionBar(findViewById(R.id.toolbar));
        columns = getIntent().getIntExtra(NavigationConstants.COLUMNS, AppConstants.FIFTEEN_PUZZLE);
        setTitle(columns == AppConstants.EIGHT_PUZZLE? R.string.eight_puzzle : R.string.fifteen_puzzle);

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
            TileStyleHelper.applyStyleToAll(buttons);
        }
        
        assistedSolve = savedInstanceState.getBoolean("assisted", false);
        puzzleAlreadySolved = savedInstanceState.getBoolean("is_solved", false);
        boolean wasPickingSticker = savedInstanceState.getBoolean("is_picking", false);
        int steps = savedInstanceState.getInt("steps", 0);
        long elapsed = savedInstanceState.getLong("elapsed_time", 0);
        
        gameState.reset();
        gameState.setStepsCount(steps);
        uiHelper.updateMovesDisplay(steps);

        // Fix: Restore timer from saved elapsed time
        if (elapsed > 0 && !puzzleAlreadySolved) {
            gameTimer.startFromElapsed(elapsed);
        }

        // Fix: If sticker dialog was showing — just start fresh
        // Restoring dialog state is complex and error-prone
        if (wasPickingSticker) {
            isPickingSticker = false;
            startNewGame();
            return;
        }

        if (puzzleAlreadySolved) {
            GenericUtils.disableButtons(buttonManager.getButtonList());
            setHintSolutionEnabled(false);
            uiHelper.setUndoEnabled(false);
        } else {
            for (Button b : buttonManager.getButtonList()) b.setEnabled(true);
            setHintSolutionEnabled(true);
            uiHelper.setUndoEnabled(gameState.canUndo());
        }
        
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
        gameTimer.pause();
        soundHelper.pauseMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameTimer.resume();
        soundHelper.pauseMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        celebrationHelper.hideCelebration();
        hintHelper.cancel();
        solutionHelper.stop();
        solverExecutor.shutdownNow();
    }

    private void initialiseHelpers() {
        buttonManager = new ButtonManager(this, columns, this);
        gameTimer = new GameTimer(findViewById(R.id.timerChronometer), this);
        gameState = new GameState(this);
        uiHelper = new UIHelper(this, findViewById(R.id.movesCountTextView), findViewById(R.id.btnUndo));

        btnHelp     = findViewById(R.id.btnHelp);
        btnHint     = findViewById(R.id.btnHint);
        btnSolution = findViewById(R.id.btnSolution);
        btnStickers = findViewById(R.id.btnStickers);

        celebrationHelper = new CelebrationHelper(this);
        hintHelper        = new HintHelper(findViewById(R.id.hintArrowView));
        solutionHelper    = new SolutionHelper();
        styleButtons();
    }

    private void startNewGame() {
        solverTaskId++;
        puzzleAlreadySolved = false;
        if (celebrationHelper.isShowing()) {
            celebrationHelper.hideCelebration();
        }
        hintHelper.cancel();
        solutionHelper.stop();
        assistedSolve = false;
        GenericUtils.distributeData(0, buttonManager.getButtonList());
        TileStyleHelper.applyStyleToAll(buttonManager.getButtonList());
        uiHelper.updateMovesDisplay(0);
        uiHelper.setUndoEnabled(false);
        setHintSolutionEnabled(true);
        for (Button b : buttonManager.getButtonList()) {
            b.setEnabled(true);
        }
        gameState.reset();
        gameTimer.stop();
        gameTimer.start();
    }

    @Override
    public void onTileClicked(Button buttonPressed) {
        if (AnimationHelper.isAnimating()) return;
        if (StringUtils.isBlank(buttonPressed.getText())) return;

        // FIXED: Using Grid Indexing instead of fragile XML constraints
        List<Button> buttons = buttonManager.getButtonList();
        int index = buttons.indexOf(buttonPressed);
        if (index == -1) return;

        int size = (int) Math.round(Math.sqrt(buttons.size()));
        int row = index / size;
        int col = index % size;

        Button emptyNeighbour = null;

        // Check neighbors by grid index
        // Above
        if (row > 0) {
            Button neighbor = buttons.get((row - 1) * size + col);
            if (GenericUtils.checkButton(neighbor)) emptyNeighbour = neighbor;
        }
        // Below
        if (emptyNeighbour == null && row < size - 1) {
            Button neighbor = buttons.get((row + 1) * size + col);
            if (GenericUtils.checkButton(neighbor)) emptyNeighbour = neighbor;
        }
        // Left
        if (emptyNeighbour == null && col > 0) {
            Button neighbor = buttons.get(row * size + (col - 1));
            if (GenericUtils.checkButton(neighbor)) emptyNeighbour = neighbor;
        }
        // Right
        if (emptyNeighbour == null && col < size - 1) {
            Button neighbor = buttons.get(row * size + (col + 1));
            if (GenericUtils.checkButton(neighbor)) emptyNeighbour = neighbor;
        }

        if (emptyNeighbour == null) return;

        soundHelper.playTileTap();
        final Button finalEmpty = emptyNeighbour;

        AnimationHelper.animateTileSlide(buttonPressed, finalEmpty, () -> {
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

    public void undo(View view) {
        if (AnimationHelper.isAnimating()) return;
        Move lastMove = gameState.undoLastMove();
        if (lastMove == null) return;
        soundHelper.playTileTap();
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        GenericUtils.swapData(lastMove.getToButton(), lastMove.getFromButton());
        TileStyleHelper.applyStyle(lastMove.getToButton());
        TileStyleHelper.applyStyle(lastMove.getFromButton());
        uiHelper.updateMovesDisplay(gameState.getStepsCount());
        uiHelper.setUndoEnabled(gameState.canUndo());
    }

    public void showHint(View view) {
        if (AnimationHelper.isAnimating()) return;
        if (solutionHelper.isPlaying())    return;
        if (gameState.isSolved(columns, buttonManager.getButtonList())) return;
        final int currentTaskId = solverTaskId;
        setHintSolutionEnabled(false);
        int[] board = GenericUtils.extractBoard(buttonManager.getButtonList());
        int gridSize = (int) Math.round(Math.sqrt(columns));
        solverExecutor.execute(() -> {
            List<Integer> moves = new PuzzleSolver().solve(board, gridSize);
            mainHandler.post(() -> {
                if (isDestroyed() || isFinishing() || currentTaskId != solverTaskId) return;
                setHintSolutionEnabled(true);
                if (moves.isEmpty()) {
                    Toast.makeText(PuzzleActivity.this, getString(R.string.solver_timeout_hint), Toast.LENGTH_SHORT).show();
                    return;
                }
                int nextTile = moves.get(0);
                Button hintButton = GenericUtils.findButtonForTile(nextTile, buttonManager.getButtonList());
                Button emptyButton = GenericUtils.findEmptyButton(buttonManager.getButtonList());
                if (hintButton != null && emptyButton != null) {
                    assistedSolve = true;
                    hintHelper.showHint(hintButton, emptyButton);
                }
            });
        });
    }

    public void showSolution(View view) {
        if (AnimationHelper.isAnimating()) return;
        if (solutionHelper.isPlaying())    return;
        uiHelper.showConfirmDialog(getString(R.string.solution_confirm_title), R.string.solution_confirm_message, this::startSolvingInBackground);
    }

    private void startSolvingInBackground() {
        assistedSolve = true;
        setHintSolutionEnabled(false);
        uiHelper.setUndoEnabled(false);
        hintHelper.cancel();
        for (Button b : buttonManager.getButtonList()) b.setEnabled(false);
        int[] board = GenericUtils.extractBoard(buttonManager.getButtonList());
        int gridSize = (int) Math.round(Math.sqrt(columns));
        solverExecutor.execute(() -> {
            List<Integer> moves = new PuzzleSolver().solve(board, gridSize);
            mainHandler.post(() -> {
                if (isDestroyed() || isFinishing()) return;
                if (moves.isEmpty()) {
                    Toast.makeText(PuzzleActivity.this, getString(R.string.solver_timeout_solution), Toast.LENGTH_SHORT).show();
                    for (Button b : buttonManager.getButtonList()) b.setEnabled(true);
                    setHintSolutionEnabled(true);
                    uiHelper.setUndoEnabled(gameState.canUndo());
                    return;
                }
                for (Button b : buttonManager.getButtonList()) b.setEnabled(false);
                solutionHelper.playSolution(moves, buttonManager.getButtonList(), this::onPuzzleSolved);
            });
        });
    }

    private void onPuzzleSolved() {
        if (puzzleAlreadySolved || gameState.getStepsCount() == 0) return;
        puzzleAlreadySolved = true;
        gameTimer.stop();
        GenericUtils.disableButtons(buttonManager.getButtonList());
        uiHelper.setUndoEnabled(false);
        setHintSolutionEnabled(false);
        soundHelper.playWinFanfare();
        hintHelper.cancel();
        boolean newBestTime  = gameTimer.checkAndSaveBestTime(columns);
        boolean newBestMoves = gameState.checkAndSaveBestMoves(columns);
        String result = assistedSolve ? getString(R.string.solved_with_help) : getString(R.string.puzzle_solved);
        String stats = gameState.getStepsCount() + " " + getString(R.string.steps) + "  ·  " + gameTimer.getFormattedTime();
        speechHelper.speak(result + "! " + stats);
        if (!assistedSolve) {
            showPickStickerDialog(result, stats, newBestTime || newBestMoves);
        } else {
            celebrationHelper.showCelebration(result + "\n" + stats, false, null, this::startNewGame);
        }
    }

    private void showPickStickerDialog(String result, String stats, boolean isNewBest) {
        isPickingSticker = true;
        List<String> options = stickerHelper.getRewardOptions(3);
        if (options.isEmpty()) {
            isPickingSticker = false;
            celebrationHelper.showCelebration(result + "\n" + stats, isNewBest, "FULL", this::startNewGame);
            return;
        }
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_pick_sticker, null);
        ViewGroup container = dialogView.findViewById(R.id.stickerOptionsContainer);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create();
        int index = 0;
        for (String sticker : options) {
            Button btn = new Button(this);
            btn.setText(sticker);
            btn.setTextSize(40);
            int heightPx = (int) (100 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, heightPx, 1.0f);
            params.setMargins(8, 8, 8, 8);
            btn.setLayoutParams(params);
            if (index == 0)      ButtonStyleHelper.stylePrimary(btn);
            else if (index == 1) ButtonStyleHelper.stylePlayAgain(btn);
            else                 ButtonStyleHelper.styleMenu(btn);
            btn.setOnClickListener(v -> {
                isPickingSticker = false;
                stickerHelper.unlockSticker(sticker);
                dialog.dismiss();
                celebrationHelper.showCelebration(result + "\n" + stats, isNewBest, sticker, this::startNewGame);
            });
            container.addView(btn);
            index++;
        }
        dialog.show();
    }

    public void playAgain(View view) {
        uiHelper.showConfirmDialog(getString(R.string.play_again_confirm_title), R.string.play_again_confirm_message, this::startNewGame);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("board_state", GenericUtils.extractBoard(buttonManager.getButtonList()));
        outState.putBoolean("assisted", assistedSolve);
        outState.putBoolean("is_solved", puzzleAlreadySolved);
        outState.putBoolean("is_picking", isPickingSticker);
        outState.putInt("steps", gameState.getStepsCount());
        outState.putLong("elapsed_time", gameTimer.getElapsedMillis());
        outState.putBoolean("celebration_showing", celebrationHelper.isShowing());
    }

    public void backToMainActivity(View view) {
        uiHelper.showConfirmDialog(getString(R.string.back_to_main_menu_confirm_title), R.string.back_to_main_menu_confirm_message, () -> {
            gameTimer.stop();
            finish();
        });
    }

    private void setHintSolutionEnabled(boolean enabled) {
        if (btnHelp != null) { btnHelp.setEnabled(enabled); btnHelp.setAlpha(enabled ? 1.0f : 0.4f); }
        if (btnHint != null) { btnHint.setEnabled(enabled); btnHint.setAlpha(enabled ? 1.0f : 0.4f); }
        if (btnSolution != null) { btnSolution.setEnabled(enabled); btnSolution.setAlpha(enabled ? 1.0f : 0.4f); }
        if (btnStickers != null) { btnStickers.setEnabled(enabled); btnStickers.setAlpha(enabled ? 1.0f : 0.4f); }
    }

    public void showStickerBook(View view) {
        uiHelper.showStickerBook(stickerHelper, () -> speechHelper.stop());
    }

    public void showHowToPlay(View view) {
        String rules = getString(R.string.how_to_play_rules);
        speechHelper.speak(rules);
        uiHelper.showInfoDialog(getString(R.string.how_to_play_title), getString(R.string.how_to_play_rules), () -> speechHelper.stop());
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
