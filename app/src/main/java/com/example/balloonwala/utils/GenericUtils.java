package com.example.balloonwala.utils;

import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility methods for puzzle tile operations.
 * <p>
 * Responsibilities:
 * - Swapping tile text between buttons
 * - Checking if a tile is the empty slot
 * - Distributing numbers to tiles (with solvability guarantee)
 * - Checking if the puzzle is solved
 * - Enabling / disabling all tiles
 */
public class GenericUtils {

    // ── Tile Swap ─────────────────────────────────────────

    /** Swaps the text content between two tile buttons. */
    public static void swapData(Button buttonPressed, Button buttonEmpty) {
        CharSequence text = buttonPressed.getText();
        buttonPressed.setText(buttonEmpty.getText());
        buttonEmpty.setText(text);
    }

    // ── Empty Tile Check ──────────────────────────────────

    /**
     * Returns true if the given view is a visible Button
     * with blank (empty) text — i.e. the empty tile slot.
     */
    public static boolean checkButton(View buttonEmpty) {
        return (buttonEmpty instanceof Button) &&
                buttonEmpty.getVisibility() == View.VISIBLE &&
                StringUtils.isBlank(((Button) buttonEmpty).getText());
    }

    // ── Tile Distribution ─────────────────────────────────

    /**
     * Assigns shuffled numbers to all tile buttons.
     * Guarantees the resulting arrangement is solvable.
     */

    public static void distributeData(int maximum, List<Button> buttonList) {
        int size = buttonList.size();

        // Generate a solvable shuffled list of numbers (0 is blank)
        List<Integer> numbers = generateSolvableNumbers(size);

        // Assign numbers to buttons
        for (int i = 0; i < size; i++) {
            setData(numbers.get(i), buttonList.get(i));
        }
    }

    private static void setData(int number, Button currentButton) {
        currentButton.setText(number == 0 ? "" : String.valueOf(number));
        currentButton.setEnabled(true);
    }

    // ── Solvability ───────────────────────────────────────

    /**
     * Generates a shuffled number arrangement guaranteed to be solvable.
     * 0 represents the blank tile.
     */
    private static List<Integer> generateSolvableNumbers(int size) {
        List<Integer> numbers = new ArrayList<>();

        // Build list: 1 to (size-1), then 0 as the blank tile marker
        for (int i = 1; i < size; i++) {
            numbers.add(i);
        }
        numbers.add(0); // blank tile

        // Keep shuffling until we get a solvable arrangement
        do {
            Collections.shuffle(numbers);
        } while (!isSolvable(numbers, size));

        return numbers;
    }

    /**
     * Checks if a given puzzle arrangement is solvable.
     * <p>
     * Rules:
     * - For odd grid width (3x3 = 8-puzzle): solvable if inversion count is even
     * - For even grid width (4x4 = 15-puzzle): solvable if
     *   (inversion count + row of blank from bottom) is odd
     */
    private static boolean isSolvable(List<Integer> numbers, int size) {
        int gridWidth = (int) Math.round(Math.sqrt(size));
        int inversions = countInversions(numbers, size);

        if (gridWidth % 2 != 0) {
            // Odd grid (3x3): solvable if inversions is even
            return inversions % 2 == 0;
        } else {
            // Even grid (4x4): find row of blank tile from bottom
            int blankIndex = numbers.indexOf(0);
            int blankRowFromBottom = gridWidth - (blankIndex / gridWidth);
            return (inversions + blankRowFromBottom) % 2 != 0;
        }
    }

    private static int countInversions(List<Integer> numbers, int size) {
        int inversions = 0;
        for (int i = 0; i < size - 1; i++) {
            int num1 = numbers.get(i);
            if (num1 == 0) continue;
            for (int j = i + 1; j < size; j++) {
                int num2 = numbers.get(j);
                if (num2 != 0 && num1 > num2) {
                    inversions++;
                }
            }
        }
        return inversions;
    }

    // ── Solved Check ──────────────────────────────────────

    /**
     * Returns true if all tiles are in the correct solved order.
     *
     * @param limit      the column count (9 for 8-puzzle, 16 for 15-puzzle)
     * @param buttonList the full ordered list of tile buttons
     */
    public static boolean solved(int limit, List<Button> buttonList) {
        for (int i = 0; i < limit - 1; i++) {
            String text = buttonList.get(i).getText().toString();
            if (text.isEmpty() || Integer.parseInt(text) != i + 1) {
                return false;
            }
        }
        // Last button must be empty
        return buttonList.get(limit - 1).getText().toString().isEmpty();
    }

    // ── Button State ──────────────────────────────────────

    /** Disables all tile buttons e.g. when the puzzle is solved. */
    public static void disableButtons(List<Button> buttonList) {
        for (Button button : buttonList) {
            button.setEnabled(false);
        }
    }

    // ── Solver Support ────────────────────────────────────

    /**
     * Converts the current button arrangement into a flat int array
     * for use by PuzzleSolver. 0 represents the empty tile.
     */
    public static int[] extractBoard(List<Button> buttonList) {
        int[] board = new int[buttonList.size()];
        for (int i = 0; i < buttonList.size(); i++) {
            String text = buttonList.get(i).getText().toString().trim();
            if (text.isEmpty()) {
                board[i] = 0;
            } else {
                try {
                    board[i] = Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    // Fallback for safety - treats malformed text as empty slot
                    board[i] = 0;
                }
            }
        }
        return board;
    }

    /**
     * Finds the button currently showing the given tile number.
     * Returns null if not found.
     */
    public static Button findButtonForTile(int tileNumber, List<Button> buttonList) {
        for (Button button : buttonList) {
            String text = button.getText().toString().trim();
            if (!text.isEmpty() && Integer.parseInt(text) == tileNumber) {
                return button;
            }
        }
        return null;
    }

    /**
     * Finds the button currently showing the empty tile (blank text).
     * Returns null if not found.
     */
    public static Button findEmptyButton(List<Button> buttonList) {
        for (Button button : buttonList) {
            if (StringUtils.isBlank(button.getText())) return button;
        }
        return null;
    }
}
