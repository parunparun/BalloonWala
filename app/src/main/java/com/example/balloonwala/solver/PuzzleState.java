package com.example.balloonwala.solver;

import java.util.Arrays;

/**
 * Represents a single puzzle board state used by PuzzleSolver.
 * <p>
 * Stores the tile arrangement as a flat int array:
 *   - Values 1…N-1 represent numbered tiles
 *   - Value 0 represents the empty slot
 * <p>
 * Example 8-puzzle goal state: [1, 2, 3, 4, 5, 6, 7, 8, 0]
 */
public class PuzzleState {

    final int[] board;
    final int   gridSize;
    final int   emptyIndex;

    /**
     * The tile number that was moved to reach this state.
     * 0 for the initial state (no move made yet).
     */
    final int movedTile;

    PuzzleState(int[] board, int gridSize, int emptyIndex, int movedTile) {
        this.board      = board;
        this.gridSize   = gridSize;
        this.emptyIndex = emptyIndex;
        this.movedTile  = movedTile;
    }

    // ── Goal Check ────────────────────────────────────────

    /**
     * Returns true if the board is in the solved state.
     * Solved = tiles 1…N-1 in order, empty slot last.
     */
    boolean isGoal() {
        for (int i = 0; i < board.length - 1; i++) {
            if (board[i] != i + 1) return false;
        }
        return board[board.length - 1] == 0;
    }

    // ── Heuristic ─────────────────────────────────────────

    /**
     * Manhattan Distance heuristic — sum of distances each tile
     * is from its goal position. Admissible: never overestimates.
     * Guarantees IDA* finds the optimal solution.
     */
    int manhattanDistance() {
        int distance = 0;
        for (int i = 0; i < board.length; i++) {
            int tile = board[i];
            if (tile != 0) {
                int goalRow = (tile - 1) / gridSize;
                int goalCol = (tile - 1) % gridSize;
                int currRow = i / gridSize;
                int currCol = i % gridSize;
                distance += Math.abs(goalRow - currRow)
                        + Math.abs(goalCol - currCol);
            }
        }
        return distance;
    }

    // ── Equality ──────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PuzzleState)) return false;
        return Arrays.equals(board, ((PuzzleState) o).board);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(board);
    }
}
