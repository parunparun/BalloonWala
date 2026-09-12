package com.example.balloonwala.helpers;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.balloonwala.AppConstants;
import com.example.balloonwala.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages all puzzle tile buttons.
 */
public class ButtonManager {
    public interface OnTileClickListener {
        void onTileClicked(Button button);
    }

    private final Activity           activity;
    private final OnTileClickListener tileClickListener;
    private final List<Button>        buttonList = new ArrayList<>();

    private static final int[] BUTTON_IDS = {
            R.id.buttonCoordinates1_1, R.id.buttonCoordinates1_2,
            R.id.buttonCoordinates1_3, R.id.buttonCoordinates1_4,
            R.id.buttonCoordinates2_1, R.id.buttonCoordinates2_2,
            R.id.buttonCoordinates2_3, R.id.buttonCoordinates2_4,
            R.id.buttonCoordinates3_1, R.id.buttonCoordinates3_2,
            R.id.buttonCoordinates3_3, R.id.buttonCoordinates3_4,
            R.id.buttonCoordinates4_1, R.id.buttonCoordinates4_2,
            R.id.buttonCoordinates4_3, R.id.buttonCoordinates4_4
    };

    private static final int[] FIFTEEN_PUZZLE_ONLY_IDS = {
            R.id.buttonCoordinates1_4,
            R.id.buttonCoordinates2_4,
            R.id.buttonCoordinates3_4,
            R.id.buttonCoordinates4_1,
            R.id.buttonCoordinates4_2,
            R.id.buttonCoordinates4_3,
            R.id.buttonCoordinates4_4
    };

    public ButtonManager(Activity activity, int columns, OnTileClickListener listener) {
        this.activity          = activity;
        this.tileClickListener = listener;
        initialiseButtons(columns);
    }

    private void initialiseButtons(int columns) {
        List<Integer> fifteenOnly = new ArrayList<>();
        for (int id : FIFTEEN_PUZZLE_ONLY_IDS) {
            fifteenOnly.add(id);
        }

        View.OnTouchListener touchListener = (view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) return true;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Button tapped = findButtonById(view.getId());
                if (tapped != null && tapped.getVisibility() == View.VISIBLE) {
                    tileClickListener.onTileClicked(tapped);
                }
                return true;
            }
            return false;
        };

        for (int id : BUTTON_IDS) {
            boolean is15PuzzleOnly = fifteenOnly.contains(id);
            boolean visible = (columns == AppConstants.FIFTEEN_PUZZLE) || !is15PuzzleOnly;
            
            Button button = activity.findViewById(id);
            if (visible) {
                button.setOnTouchListener(touchListener);
                buttonList.add(button);
                button.setVisibility(View.VISIBLE);
                button.setEnabled(true);
            } else {
                button.setOnTouchListener(null);
                // USE GONE to allow ConstraintLayout chains to re-center the 3x3 board
                button.setVisibility(View.GONE);
                button.setEnabled(false);
            }
        }
    }

    public Button findButtonById(int id) {
        for (Button button : buttonList) {
            if (button.getId() == id) return button;
        }
        return null;
    }

    public List<Button> getButtonList() {
        return Collections.unmodifiableList(buttonList);
    }
}
