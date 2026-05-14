package com.example.balloonwala.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import com.example.balloonwala.model.Move;
import com.example.balloonwala.utils.GenericUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the game state for a puzzle session.
 *
 * Responsibilities:
 * - Track current move count (stepsCount)
 * - Manage the undo stack (list of moves)
 * - Check if the puzzle is solved
 * - Track and persist the best move count per puzzle mode
 *   using SharedPreferences
 */
public class GameState {

    // SharedPreferences keys for best move counts
    private static final String PREFS_NAME          = "BalloonWalaPrefs";
    private static final String KEY_BEST_MOVES_8    = "best_moves_8_puzzle";
    private static final String KEY_BEST_MOVES_15   = "best_moves_15_puzzle";
    private static final int    NO_BEST_MOVES       = -1;

    private final SharedPreferences prefs;

    private int stepsCount = 0;
    private final List<Move> moveHistory = new ArrayList<>();

    public GameState(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ── Reset ─────────────────────────────────────────────

    /** Resets all state for a new game. */
    public void reset() {
        stepsCount = 0;
        moveHistory.clear();
    }

    // ── Moves ─────────────────────────────────────────────

    /** Records a new move and increments the step counter. */
    public void addMove(Button fromButton, Button toButton) {
        moveHistory.add(0, new Move(fromButton, toButton));
        stepsCount++;
    }

    /** Returns the current step count. */
    public int getStepsCount() {
        return stepsCount;
    }

    /** Returns true if there are moves available to undo. */
    public boolean canUndo() {
        return !moveHistory.isEmpty();
    }

    // ── Undo ─────────────────────────────────────────────

    /**
     * Pops and returns the last move from the history stack.
     * Also decrements the step counter.
     * Returns null if there is nothing to undo.
     */
    public Move undoLastMove() {
        if (moveHistory.isEmpty()) {
            return null;
        }
        Move lastMove = moveHistory.get(0);
        moveHistory.remove(0);
        if (stepsCount > 0) {
            stepsCount--;
        }
        return lastMove;
    }

    // ── Solved Check ──────────────────────────────────────

    /**
     * Checks whether the current button arrangement matches
     * the solved state for the given puzzle size.
     *
     * @param columns  9 for 8-puzzle, 16 for 15-puzzle
     * @param buttonList the full ordered list of puzzle buttons
     */
    public boolean isSolved(int columns, List<Button> buttonList) {
        return GenericUtils.solved(columns, buttonList);
    }

    // ── Best Moves ────────────────────────────────────────

    /**
     * Checks if the current step count is a new best (fewest moves).
     * If so, saves it and returns true.
     *
     * @param columns 9 for 8-puzzle, 16 for 15-puzzle
     */
    public boolean checkAndSaveBestMoves(int columns) {
        String key = columns == 9 ? KEY_BEST_MOVES_8 : KEY_BEST_MOVES_15;
        int bestMoves = prefs.getInt(key, NO_BEST_MOVES);

        if (bestMoves == NO_BEST_MOVES || stepsCount < bestMoves) {
            prefs.edit().putInt(key, stepsCount).apply();
            return true; // new best move count!
        }
        return false;
    }

    /**
     * Returns the stored best move count, or -1 if none exists yet.
     *
     * @param columns 9 for 8-puzzle, 16 for 15-puzzle
     */
    public int getBestMoves(int columns) {
        String key = columns == 9 ? KEY_BEST_MOVES_8 : KEY_BEST_MOVES_15;
        return prefs.getInt(key, NO_BEST_MOVES);
    }

    /** Clears the saved best move count for a given puzzle mode. */
    public void clearBestMoves(int columns) {
        String key = columns == 9 ? KEY_BEST_MOVES_8 : KEY_BEST_MOVES_15;
        prefs.edit().remove(key).apply();
    }
}
