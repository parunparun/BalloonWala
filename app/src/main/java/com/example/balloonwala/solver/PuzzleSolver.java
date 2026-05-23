package com.example.balloonwala.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Solves the 8-puzzle and 15-puzzle using IDA* (Iterative Deepening A*)
 * with Manhattan Distance as the admissible heuristic.
 * <p>
 * Guarantees the OPTIMAL (fewest moves) solution.
 * Memory efficient — O(depth) space unlike standard A*.
 * <p>
 * Has a 5-second timeout to prevent ANR on hard 15-puzzle positions.
 */
public class PuzzleSolver {

    private static final int  FOUND      = -1;
    private static final int  MAX_BOUND  = 100; // 15-puzzle optimal ≤ 80 moves
    private static final long TIMEOUT_MS = 5000;

    private long deadline;

    // ── Public API ────────────────────────────────────────

    /**
     * Solves the puzzle and returns the ordered list of tile numbers to move.
     * Returns empty list if already solved, timed out, or no solution found.
     *
     * @param board    flat int array — 0 = empty tile
     * @param gridSize 3 for 8-puzzle, 4 for 15-puzzle
     */
    public List<Integer> solve(int[] board, int gridSize) {
        int emptyIdx = findEmpty(board);
        if (emptyIdx == -1) return Collections.emptyList(); // invalid board

        PuzzleState initial = new PuzzleState(
                board.clone(), gridSize, emptyIdx, 0);

        if (initial.isGoal()) return Collections.emptyList();

        deadline = System.currentTimeMillis() + TIMEOUT_MS;

        int bound = initial.manhattanDistance();
        List<PuzzleState> path = new ArrayList<>();
        path.add(initial);

        while (bound <= MAX_BOUND) {
            if (timedOut()) return Collections.emptyList();

            int result = search(path, 0, bound);

            if (result == FOUND) return extractMoves(path);
            if (result == Integer.MAX_VALUE) return Collections.emptyList();

            bound = result;
        }
        return Collections.emptyList();
    }

    // ── IDA* Search ───────────────────────────────────────

    private int search(List<PuzzleState> path, int g, int bound) {
        if (timedOut()) return Integer.MAX_VALUE;

        PuzzleState current = path.get(path.size() - 1);
        int h = current.manhattanDistance();
        int f = g + h;

        if (f > bound) return f;
        if (h == 0)    return FOUND; // goal reached — Manhattan 0 = solved

        int min = Integer.MAX_VALUE;

        for (int neighborIdx : getValidMoves(current.emptyIndex,
                current.gridSize, current.board.length)) {

            // Build neighbor board
            int[] newBoard    = current.board.clone();
            int   movedTile   = newBoard[neighborIdx];
            newBoard[current.emptyIndex] = movedTile;
            newBoard[neighborIdx]        = 0;

            // Skip parent state — prevents immediate backtracking
            if (path.size() > 1) {
                PuzzleState parent = path.get(path.size() - 2);
                if (Arrays.equals(newBoard, parent.board)) continue;
            }

            PuzzleState neighbor = new PuzzleState(
                    newBoard, current.gridSize, neighborIdx, movedTile);

            path.add(neighbor);
            int result = search(path, g + 1, bound);
            if (result == FOUND) return FOUND;
            if (result < min)    min = result;
            path.remove(path.size() - 1);
        }
        return min;
    }

    // ── Move Generation ───────────────────────────────────

    /**
     * Returns indices adjacent to the empty tile that can legally move.
     * Handles grid boundaries — no wraparound on left/right edges.
     */
    private int[] getValidMoves(int emptyIndex, int gridSize, int totalSize) {
        List<Integer> moves = new ArrayList<>(4);

        // Up — tile above slides down
        if (emptyIndex >= gridSize) {
            moves.add(emptyIndex - gridSize);
        }
        // Down — tile below slides up
        if (emptyIndex < totalSize - gridSize) {
            moves.add(emptyIndex + gridSize);
        }
        // Left — tile to left slides right (guard: not on left edge)
        if (emptyIndex % gridSize != 0) {
            moves.add(emptyIndex - 1);
        }
        // Right — tile to right slides left (guard: not on right edge)
        if ((emptyIndex + 1) % gridSize != 0) {
            moves.add(emptyIndex + 1);
        }

        int[] result = new int[moves.size()];
        for (int i = 0; i < moves.size(); i++) result[i] = moves.get(i);
        return result;
    }

    // ── Helpers ───────────────────────────────────────────

    private boolean timedOut() {
        return System.currentTimeMillis() > deadline;
    }

    private int findEmpty(int[] board) {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) return i;
        }
        return -1;
    }

    /**
     * Extracts the ordered list of moved tile numbers from the solution path.
     * Index 0 of path = initial state (movedTile = 0), skipped.
     */
    private List<Integer> extractMoves(List<PuzzleState> path) {
        List<Integer> moves = new ArrayList<>(path.size() - 1);
        for (int i = 1; i < path.size(); i++) {
            moves.add(path.get(i).movedTile);
        }
        return moves;
    }
}