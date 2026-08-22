package com.example.balloonwala.helpers;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;
import java.util.List;

/**
 * Applies fixed colors to each puzzle tile based on its number.
 *
 * Each number always maps to the same color so kids can
 * recognise pieces by color as well as by number.
 *
 * Creates a fresh GradientDrawable per tile — avoids shared
 * drawable mutation issues that cause all tiles to appear
 * the same color.
 *
 * Call applyStyle(button) after every swap or undo.
 * Call applyStyleToAll(buttonList) after distributeData().
 */
public class TileStyleHelper {

    // Fixed tile colors — index 0 = tile "1", index 1 = tile "2" etc.
    private static final String[] TILE_COLORS = {
            "#FF6B6B",  //  1 — Red
            "#FF9F43",  //  2 — Orange
            "#F9CA24",  //  3 — Yellow  (dark text)
            "#6AB04C",  //  4 — Green
            "#FF9FF3",  //  5 — Pink    (dark text)
            "#54A0FF",  //  6 — Blue
            "#5F27CD",  //  7 — Purple
            "#00D2D3",  //  8 — Teal
            "#1DD1A1",  //  9 — Mint
            "#FD79A8",  // 10 — Rose
            "#6C5CE7",  // 11 — Indigo
            "#FAB1A0",  // 12 — Peach  (dark text)
            "#74B9FF",  // 13 — Sky
            "#55EFC4",  // 14 — Aqua   (dark text)
            "#FDCB6E",  // 15 — Amber  (dark text)
    };

    // Pre-parsed int values — avoids Color.parseColor() on every tap
    private static final int[] TILE_COLOR_INTS;
    static {
        TILE_COLOR_INTS = new int[TILE_COLORS.length];
        for (int i = 0; i < TILE_COLORS.length; i++) {
            TILE_COLOR_INTS[i] = Color.parseColor(TILE_COLORS[i]);
        }
    }

    // Tile numbers that need dark text (light colored tiles)
    private static final int[] DARK_TEXT_TILES = { 3, 5, 12, 14, 15 };

    // Pre-parsed dark text color
    private static final int DARK_TEXT_COLOR  = Color.parseColor("#333333");

    // Corner radius in dp
    private static final int CORNER_RADIUS_DP = 16;

    // ── Public API ────────────────────────────────────────

    /**
     * Applies the correct background color and text color
     * to a tile button based on its current text value.
     * Empty tiles get a subtle hollow grey appearance.
     */
    public static void applyStyle(Button button) {
        String text = button.getText().toString().trim();

        if (text.isEmpty()) {
            applyEmptyStyle(button);
            return;
        }

        try {
            int number = Integer.parseInt(text);
            if (number >= 1 && number <= TILE_COLOR_INTS.length) {
                applyFilledStyle(button, number);
            }
        } catch (NumberFormatException e) {
            applyEmptyStyle(button);
        }
    }

    /**
     * Applies styles to all buttons in the list.
     * Call this after distributeData() on a new game.
     */
    public static void applyStyleToAll(List<Button> buttons) {
        for (Button button : buttons) {
            applyStyle(button);
        }
    }

    // ── Private Helpers ───────────────────────────────────

    private static void applyFilledStyle(Button button, int number) {
        android.util.Log.d("TileStyle", "Applying color " +
                TILE_COLORS[number - 1] + " to tile " + number);
        // Create a fresh GradientDrawable each time — avoids shared
        // drawable mutation issues that cause all tiles to show same color
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(dpToPx(CORNER_RADIUS_DP, button));
        drawable.setColor(TILE_COLOR_INTS[number - 1]);
        // Reset state list animator — prevents Material from overriding background
        button.setStateListAnimator(null);
        // FIX: Forces MaterialButtons to accept the custom GradientDrawable background
        androidx.core.view.ViewCompat.setBackground(button, drawable);
        // FIX Alternative: Clear material tint properties if they are still overriding colors
        if (button instanceof com.google.android.material.button.MaterialButton) {
            ((com.google.android.material.button.MaterialButton) button).setBackgroundTintList(null);
        }
        button.setTextColor(needsDarkText(number) ? DARK_TEXT_COLOR : Color.WHITE);
    }

    private static void applyEmptyStyle(Button button) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(dpToPx(CORNER_RADIUS_DP, button));
        drawable.setColor(Color.parseColor("#18000000"));
        // Reset state list animator — prevents Material from overriding background
        button.setStateListAnimator(null);
        // FIX: Forces MaterialButtons to accept the empty background
        androidx.core.view.ViewCompat.setBackground(button, drawable);
        if (button instanceof com.google.android.material.button.MaterialButton) {
            ((com.google.android.material.button.MaterialButton) button).setBackgroundTintList(null);
        }
        button.setTextColor(Color.TRANSPARENT);
    }

    private static boolean needsDarkText(int number) {
        for (int darkTile : DARK_TEXT_TILES) {
            if (darkTile == number) return true;
        }
        return false;
    }

    private static float dpToPx(int dp, Button button) {
        return dp * button.getContext().getResources()
                .getDisplayMetrics().density;
    }
}