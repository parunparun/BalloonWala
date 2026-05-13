package com.example.balloonwala.utils;

import android.view.View;
import android.widget.Button;
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
        return (buttonEmpty instanceof Button) && ((Button) buttonEmpty).getVisibility() == 0 && StringUtils.isBlank(((Button) buttonEmpty).getText());
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
        boolean solved = true;
        String startValue = "0";
        for (Button button : buttonList) {
            int startValueInt = Integer.valueOf(startValue).intValue() + 1;
            if (startValueInt == limit) {
                startValue = "";
            } else {
                startValue = String.valueOf(startValueInt);
            }
            if (!button.getText().toString().equalsIgnoreCase(startValue)) {
                System.out.println("cool cool id: " + button.toString() + " value: " + ((Object) button.getText()) + " startValue: " + startValue);
                solved = false;
            }
            if (!solved) {
                return solved;
            }
        }
        return solved;
    }

    public static void disableButtons(List<Button> buttonList) {
        for (Button button : buttonList) {
            button.setEnabled(false);
        }
    }

    public static void distributeData(int maximum, int minimum, List<Button> buttonList) {
        Set<Integer> numbersGenerated = new HashSet<>();
        int index = 1;
        int limit = maximum - 1;
        while (numbersGenerated.size() != limit) {
            int number = getRandomInteger(maximum, minimum);
            if (!numbersGenerated.contains(Integer.valueOf(number))) {
                switch (index) {
                    case 1:
                        setData(number, buttonList.get(0), limit);
                        break;
                    case 2:
                        setData(number, buttonList.get(1), limit);
                        break;
                    case 3:
                        setData(number, buttonList.get(2), limit);
                        break;
                    case 4:
                        setData(number, buttonList.get(3), limit);
                        break;
                    case 5:
                        setData(number, buttonList.get(4), limit);
                        break;
                    case 6:
                        setData(number, buttonList.get(5), limit);
                        break;
                    case 7:
                        setData(number, buttonList.get(6), limit);
                        break;
                    case 8:
                        setData(number, buttonList.get(7), limit);
                        break;
                    case 9:
                        setData(number, buttonList.get(8), limit);
                        break;
                    case 10:
                        setData(number, buttonList.get(9), limit);
                        break;
                    case 11:
                        setData(number, buttonList.get(10), limit);
                        break;
                    case 12:
                        setData(number, buttonList.get(11), limit);
                        break;
                    case 13:
                        setData(number, buttonList.get(12), limit);
                        break;
                    case 14:
                        setData(number, buttonList.get(13), limit);
                        break;
                    case 15:
                        setData(number, buttonList.get(14), limit);
                        break;
                    case 16:
                        setData(number, buttonList.get(15), limit);
                        break;
                }
                index++;
                numbersGenerated.add(Integer.valueOf(number));
            }
        }
    }
}
