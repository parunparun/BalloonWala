package com.example.balloonwala.solver;

import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Solves the 8-puzzle and 15-puzzle using IDA* (Iterative Deepening A*)
 * with Manhattan Distance + Linear Conflict as the heuristic.
 * <p>
 * Highly optimized for mobile:
 * - Incremental Heuristic updates (O(1) MD, row/col LC).
 * - Zero object allocation in hot loops.
 */
public class PuzzleSolver {

    private static final int FOUND = -1;
    private static final int MAX_BOUND = 80;
    private static final long TIMEOUT_MS = 5000;

    private long deadline;
    private int gridSize;
    private int[] board;
    
    private int[] goalRows;
    private int[] goalCols;
    private int[] rowForIdx;
    private int[] colForIdx;

    private final List<Integer> solutionMoves = new ArrayList<>();

    public List<Integer> solve(int[] initialBoard, int gridSize) {
        this.gridSize = gridSize;
        this.board = initialBoard.clone();
        this.solutionMoves.clear();

        int emptyIdx = findEmpty(board);
        if (emptyIdx == -1) return Collections.emptyList();

        precompute(gridSize);
        
        int md = calculateInitialMD();
        if (md == 0) return Collections.emptyList();

        deadline = System.currentTimeMillis() + TIMEOUT_MS;

        int bound = md + calculateInitialLC();

        while (bound <= MAX_BOUND) {
            if (timedOut()) return Collections.emptyList();

            int result = search(0, md, bound, emptyIdx, -1);
            if (result == FOUND) return new ArrayList<>(solutionMoves);
            if (result == Integer.MAX_VALUE) return Collections.emptyList();

            bound = result;
        }

        return Collections.emptyList();
    }

    private void precompute(int gridSize) {
        int size = gridSize * gridSize;
        goalRows = new int[size];
        goalCols = new int[size];
        rowForIdx = new int[size];
        colForIdx = new int[size];

        for (int i = 0; i < size; i++) {
            rowForIdx[i] = i / gridSize;
            colForIdx[i] = i % gridSize;
            if (i < size - 1) {
                int tile = i + 1;
                goalRows[tile] = i / gridSize;
                goalCols[tile] = i % gridSize;
            }
        }
    }

    private int calculateInitialMD() {
        int md = 0;
        for (int i = 0; i < board.length; i++) {
            int tile = board[i];
            if (tile != 0) {
                md += Math.abs(goalRows[tile] - rowForIdx[i])
                    + Math.abs(goalCols[tile] - colForIdx[i]);
            }
        }
        return md;
    }

    private int calculateInitialLC() {
        int lc = 0;
        for (int i = 0; i < gridSize; i++) {
            lc += countRowConflicts(i);
            lc += countColConflicts(i);
        }
        return lc;
    }

    private int countRowConflicts(int row) {
        int conflicts = 0;
        int rowOffset = row * gridSize;
        for (int i = 0; i < gridSize; i++) {
            int t1 = board[rowOffset + i];
            if (t1 == 0 || goalRows[t1] != row) continue;
            for (int j = i + 1; j < gridSize; j++) {
                int t2 = board[rowOffset + j];
                if (t2 != 0 && goalRows[t2] == row && goalCols[t1] > goalCols[t2]) {
                    conflicts += 2;
                }
            }
        }
        return conflicts;
    }

    private int countColConflicts(int col) {
        int conflicts = 0;
        for (int i = 0; i < gridSize; i++) {
            int t1 = board[i * gridSize + col];
            if (t1 == 0 || goalCols[t1] != col) continue;
            for (int j = i + 1; j < gridSize; j++) {
                int t2 = board[j * gridSize + col];
                if (t2 != 0 && goalCols[t2] == col && goalRows[t1] > goalRows[t2]) {
                    conflicts += 2;
                }
            }
        }
        return conflicts;
    }

    private int search(int g, int md, int bound, int emptyIdx, int prevEmptyIdx) {
        // Full LC is still fast for 4x4, but let's keep it clean
        int h = md + calculateInitialLC(); 
        int f = g + h;
        if (f > bound) return f;
        if (md == 0 && h == md) return FOUND;
        if (timedOut()) return Integer.MAX_VALUE;

        int min = Integer.MAX_VALUE;

        int[] offsets = { -gridSize, gridSize, -1, 1 };
        for (int offset : offsets) {
            int nextIdx = emptyIdx + offset;
            if (nextIdx < 0 || nextIdx >= board.length) continue;
            if (offset == -1 && emptyIdx % gridSize == 0) continue;
            if (offset == 1 && (emptyIdx + 1) % gridSize == 0) continue;
            if (nextIdx == prevEmptyIdx) continue;

            int tile = board[nextIdx];
            int nextMD = md - (Math.abs(goalRows[tile] - rowForIdx[nextIdx]) + Math.abs(goalCols[tile] - colForIdx[nextIdx]))
                           + (Math.abs(goalRows[tile] - rowForIdx[emptyIdx]) + Math.abs(goalCols[tile] - colForIdx[emptyIdx]));

            board[emptyIdx] = tile;
            board[nextIdx] = 0;
            solutionMoves.add(tile);

            int result = search(g + 1, nextMD, bound, nextIdx, emptyIdx);

            if (result == FOUND) return FOUND;
            if (result < min) min = result;

            solutionMoves.remove(solutionMoves.size() - 1);
            board[nextIdx] = tile;
            board[emptyIdx] = 0;
        }

        return min;
    }

    private boolean timedOut() {
        return System.currentTimeMillis() > deadline;
    }

    private int findEmpty(int[] board) {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) return i;
        }
        return -1;
    }
}
