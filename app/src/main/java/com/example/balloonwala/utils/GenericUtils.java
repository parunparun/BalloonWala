package com.example.balloonwala.utils;

import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility methods for puzzle tile operations.
 *
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

        // Generate a solvable shuffled list of numbers
        List<Integer> numbers = generateSolvableNumbers(size, maximum - 1);

        // BUG FIX 3: Assign numbers to buttons using a loop instead of a switch
        for (int i = 0; i < size; i++) {
            setData(numbers.get(i), buttonList.get(i), maximum - 1);
        }
    }

    private static void setData(int number, Button currentButton, int limit) {
        currentButton.setText(number == limit ? "" : String.valueOf(number));
        currentButton.setEnabled(true);
    }

    // ── Solvability ───────────────────────────────────────

    /**
     * Generates a shuffled number arrangement guaranteed to be solvable.
     * Keeps reshuffling until the solvability check passes.
     */
    private static List<Integer> generateSolvableNumbers(int size, int limit) {
        List<Integer> numbers = new ArrayList<>();

        // Build list: 1 to (size-1), then limit as the blank tile marker
        for (int i = 1; i < size; i++) {
            numbers.add(i);
        }
        numbers.add(limit); // blank tile

        // Keep shuffling until we get a solvable arrangement
        do {
            Collections.shuffle(numbers);
        } while (!isSolvable(numbers, size));

        return numbers;
    }

    /**
     * Checks if a given puzzle arrangement is solvable.
     *
     * Rules:
     * - For odd grid width (3x3 = 8-puzzle): solvable if inversion count is even
     * - For even grid width (4x4 = 15-puzzle): solvable if
     *   (inversion count + row of blank from bottom) is odd
     */
    private static boolean isSolvable(List<Integer> numbers, int size) {
        int gridWidth = (int) Math.sqrt(size);
        int inversions = countInversions(numbers, size);

        if (gridWidth % 2 != 0) {
            // Odd grid (3x3): solvable if inversions is even
            return inversions % 2 == 0;
        } else {
            // Even grid (4x4): find row of blank tile from bottom
            int blankIndex = numbers.indexOf(size - 1);
            int blankRowFromBottom = gridWidth - (blankIndex / gridWidth);
            return (inversions + blankRowFromBottom) % 2 != 0;
        }
    }

    private static int countInversions(List<Integer> numbers, int size) {
        int inversions = 0;
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                // Blank tile (represented by limit value) doesn't count
                if (numbers.get(i) != size - 1
                        && numbers.get(j) != size - 1
                        && numbers.get(i) > numbers.get(j)) {
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
        String startValue = "0";
        for (Button button : buttonList) {
            int startValueInt = Integer.parseInt(startValue) + 1;
            if (startValueInt == limit) {
                startValue = "";
            } else {
                startValue = String.valueOf(startValueInt);
            }
            if (!button.getText().toString().equalsIgnoreCase(startValue)) {
                return false;
            }
        }
        return true;
    }

    // ── Button State ──────────────────────────────────────

    /** Disables all tile buttons e.g. when the puzzle is solved. */
    public static void disableButtons(List<Button> buttonList) {
        for (Button button : buttonList) {
            button.setEnabled(false);
        }
    }
}
