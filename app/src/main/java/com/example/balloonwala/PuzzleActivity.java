package com.example.balloonwala;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.balloonwala.helpers.ButtonManager;
import com.example.balloonwala.helpers.CelebrationHelper;
import com.example.balloonwala.helpers.GameState;
import com.example.balloonwala.helpers.GameTimer;
import com.example.balloonwala.helpers.TileStyleHelper;
import com.example.balloonwala.helpers.UIHelper;
import com.example.balloonwala.model.Move;
import com.example.balloonwala.utils.GenericUtils;
import org.apache.commons.lang3.StringUtils;

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
 */

public class PuzzleActivity extends AppCompatActivity
        implements ButtonManager.OnTileClickListener {

    private int columns = 16;

    // ── Helpers ───────────────────────────────────────────
    private ButtonManager     buttonManager;
    private GameTimer         gameTimer;
    private GameState         gameState;
    private UIHelper          uiHelper;
    private CelebrationHelper celebrationHelper;

    // ── Lifecycle ─────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle);

        setSupportActionBar(findViewById(R.id.toolbar));

        columns = getIntent().getIntExtra(AppConstants.COLUMNS, 16);
        setTitle(columns == AppConstants.EIGHT_PUZZLE? R.string.eight_puzzle : R.string.fifteen_puzzle);

        initialiseHelpers();
        startNewGame();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameTimer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameTimer.resume();
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
                 findViewById(R.id.undo));

        celebrationHelper = new CelebrationHelper(this);
    }

    // ── New Game ──────────────────────────────────────────

    private void startNewGame() {
        // Hide celebration if showing
        if (celebrationHelper.isShowing()) {
            celebrationHelper.hideCelebration();
        }

        gameState.reset();
        gameTimer.start();

        GenericUtils.distributeData(columns + 1, buttonManager.getButtonList());

        // Apply fixed colors to all tiles after distribution
        TileStyleHelper.applyStyleToAll(buttonManager.getButtonList());

        uiHelper.updateMovesDisplay(0);
        uiHelper.setUndoEnabled(false);

        for (Button b : buttonManager.getButtonList()) {
            b.setEnabled(true);
        }
    }

    // ── OnTileClickListener ───────────────────────────────

    /**
     * Called by ButtonManager when the player taps a tile.
     * Finds the empty neighbour and delegates swap to GenericUtils.
     */
    @Override
    public void onTileClicked(Button buttonPressed) {
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

        // Perform the swap
        GenericUtils.swapData(buttonPressed, emptyNeighbour);
        gameState.addMove(buttonPressed, emptyNeighbour);

        // Reapply colors after swap so tiles keep their fixed colors
        TileStyleHelper.applyStyle(buttonPressed);
        TileStyleHelper.applyStyle(emptyNeighbour);

        uiHelper.updateMovesDisplay(gameState.getStepsCount());
        uiHelper.setUndoEnabled(gameState.canUndo());

        // Check if puzzle is solved
        if (gameState.isSolved(columns, buttonManager.getButtonList())) {
            onPuzzleSolved();
        }
    }

    // ── Undo ──────────────────────────────────────────────

    public void undo(View view) {
        Move lastMove = gameState.undoLastMove();
        if (lastMove == null) return;

        GenericUtils.swapData(lastMove.getToButton(), lastMove.getFromButton());

        // Reapply colors after undo swap
        TileStyleHelper.applyStyle(lastMove.getToButton());
        TileStyleHelper.applyStyle(lastMove.getFromButton());

        uiHelper.updateMovesDisplay(gameState.getStepsCount());
        uiHelper.setUndoEnabled(gameState.canUndo());
    }

    // ── Solved ────────────────────────────────────────────

    private void onPuzzleSolved() {
        gameTimer.stop();
        GenericUtils.disableButtons(buttonManager.getButtonList());

        boolean newBestTime  = gameTimer.checkAndSaveBestTime(columns);
        boolean newBestMoves = gameState.checkAndSaveBestMoves(columns);

        StringBuilder message = new StringBuilder();
        message.append(getString(R.string.puzzle_solved)).append("\n");
        message.append(getString(R.string.in))
                .append(" ").append(gameState.getStepsCount())
                .append(" ").append(getString(R.string.steps)).append("\n");
        message.append("Time: ").append(gameTimer.getFormattedTime());

        if (newBestTime || newBestMoves) {
            message.append("\n🏆 New Best!");
        }

        String stats = gameState.getStepsCount() + " moves  ·  "
                + gameTimer.getFormattedTime();

        celebrationHelper.showCelebration(
                stats,
                newBestTime || newBestMoves,
                this::startNewGame);
    }

    // ── Play Again ────────────────────────────────────────

    public void playAgain(View view) {
        uiHelper.showConfirmDialog(
                getString(R.string.play_again_confirmation),
                R.string.quit,
                this::startNewGame);
    }

    // ── Back ──────────────────────────────────────────────

    public void backToMainActivity(View view) {
        uiHelper.showConfirmDialog(
                getString(R.string.play_again_confirmation),
                R.string.quit,
                () -> {
                    gameTimer.stop();
                    startActivity(new Intent(this, MainActivity.class));
                });
    }
}
