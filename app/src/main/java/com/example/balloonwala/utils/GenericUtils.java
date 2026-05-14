package com.example.balloonwala.utils;

import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/* JADX INFO: loaded from: classes2.dex */
public class GenericUtils {
    public static void swapData(Button buttonPressed, Button buttonEmpty) {
        CharSequence text = buttonPressed.getText();
        buttonPressed.setText(buttonEmpty.getText());
        buttonEmpty.setText(text);
    }

    public static void undo(Button buttonPressed, Button buttonEmpty) {
        CharSequence text = buttonPressed.getText();
        buttonPressed.setText(buttonEmpty.getText());
        buttonEmpty.setText(text);
    }

    public static boolean checkButton(View buttonEmpty) {
        return (buttonEmpty instanceof Button) &&
                buttonEmpty.getVisibility() == View.VISIBLE &&
                StringUtils.isBlank(((Button) buttonEmpty).getText());
    }

    private static int getRandomInteger(int maximum, int minimum) {
        return ((int) (Math.random() * ((double) (maximum - minimum)))) + minimum;
    }

    private static void setData(int number, Button currentButton, int limit) {
        if (number == limit) {
            currentButton.setText("");
        } else {
            currentButton.setText(String.valueOf(number));
        }
        currentButton.setEnabled(true);
    }

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

    public static void disableButtons(List<Button> buttonList) {
        for (Button button : buttonList) {
            button.setEnabled(false);
        }
    }

    // BUG FIX 2: Replaced giant 16-case switch statement with a clean loop
    public static void distributeData(int maximum, int minimum, List<Button> buttonList) {
        int size = buttonList.size();

        // Generate a solvable shuffled list of numbers
        List<Integer> numbers = generateSolvableNumbers(size, maximum - 1);

        // BUG FIX 3: Assign numbers to buttons using a loop instead of a switch
        for (int i = 0; i < size; i++) {
            setData(numbers.get(i), buttonList.get(i), maximum - 1);
        }
    }

    // BUG FIX 4: Added solvability check so puzzle is always solvable
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
}
