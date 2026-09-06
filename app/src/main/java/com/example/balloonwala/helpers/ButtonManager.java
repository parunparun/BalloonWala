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
 * <p>
 * Responsibilities:
 * - Register all 16 buttons from the layout
 * - Show / hide buttons based on puzzle mode (8 or 15 puzzle)
 * - Attach touch listeners to each button
 * - Look up a button by its view ID
 * <p>
 * Uses the OnTileClickListener interface to notify
 * PuzzleActivity when a tile is tapped — keeping
 * ButtonManager independent of PuzzleActivity.
 */
public class ButtonManager {
    // ── Interface ─────────────────────────────────────────

    /**
     * Callback interface — implemented by PuzzleActivity.
     * Called whenever the player taps a puzzle tile.
     */
    public interface OnTileClickListener {
        void onTileClicked(Button button);
    }

    // ── Fields ────────────────────────────────────────────

    private final Activity           activity;
    private final OnTileClickListener tileClickListener;
    private final List<Button>        buttonList = new ArrayList<>();

    // All 16 button IDs in grid order
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

    // Buttons only used in 15-puzzle (4x4) — hidden in 8-puzzle (3x3)
    private static final int[] FIFTEEN_PUZZLE_ONLY_IDS = {
            R.id.buttonCoordinates1_4,
            R.id.buttonCoordinates2_4,
            R.id.buttonCoordinates3_4,
            R.id.buttonCoordinates4_1,
            R.id.buttonCoordinates4_2,
            R.id.buttonCoordinates4_3,
            R.id.buttonCoordinates4_4
    };

    // ── Constructor ───────────────────────────────────────

    public ButtonManager(Activity activity, int columns, OnTileClickListener listener) {
        this.activity          = activity;
        this.tileClickListener = listener;
        initialiseButtons(columns);
    }

    // ── Setup ─────────────────────────────────────────────

    /**
     * Finds all 16 buttons, attaches touch listeners,
     * and hides buttons not needed for the current puzzle mode.
     */
    private void initialiseButtons(int columns) {
        // Build set of IDs that are 15-puzzle only for quick lookup
        List<Integer> fifteenOnly = new ArrayList<>();
        for (int id : FIFTEEN_PUZZLE_ONLY_IDS) {
            fifteenOnly.add(id);
        }

        View.OnTouchListener touchListener = buildTouchListener();

        for (int id : BUTTON_IDS) {
            boolean is15PuzzleOnly = fifteenOnly.contains(id);
            boolean visible = (columns == AppConstants.FIFTEEN_PUZZLE) || !is15PuzzleOnly;
            registerButton(id, touchListener, visible);
        }
    }

    /**
     * Builds the shared OnTouchListener used by all buttons.
     * Fires onTileClicked on ACTION_UP (finger lifted).
     */
    private View.OnTouchListener buildTouchListener() {
        return (view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Return true for ACTION_DOWN to ensure we receive ACTION_UP
                // and to prevent touches from falling through to the background.
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Button tapped = findButtonById(view.getId());
                if (tapped != null && tapped.getVisibility() == View.VISIBLE) {
                    tileClickListener.onTileClicked(tapped);
                }
                return true;
            }
            return false;
        };
    }

    /**
     * Finds a button by ID, attaches the touch listener,
     * and either adds it to the active list or hides it.
     */
    private void registerButton(int id, View.OnTouchListener listener, boolean visible) {
        Button button = activity.findViewById(id);
        if (visible) {
            button.setOnTouchListener(listener);
            buttonList.add(button);
            button.setVisibility(View.VISIBLE);
            button.setEnabled(true);
        } else {
            button.setOnTouchListener(null); // Explicitly remove listener for hidden buttons
            button.setVisibility(View.INVISIBLE);
            button.setEnabled(false);
        }
    }

    // ── Lookup ────────────────────────────────────────────

    /**
     * Finds and returns the Button matching the given view ID.
     * Returns null if the ID doesn't match any registered button.
     */
    public Button findButtonById(int id) {
        for (Button button : buttonList) {
            if (button.getId() == id) {
                return button;
            }
        }
        return null;
    }

    // ── Accessors ─────────────────────────────────────────

    /** Returns an unmodifiable view of the active button list. */
    public List<Button> getButtonList() {
        return Collections.unmodifiableList(buttonList);
    }
}
