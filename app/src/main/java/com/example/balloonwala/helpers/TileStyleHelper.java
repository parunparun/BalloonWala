package com.example.balloonwala.helpers;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;
import androidx.core.content.ContextCompat;
import com.example.balloonwala.R;
import java.util.List;

/**
 * Applies fixed colors to each puzzle tile based on its number.
 * <p>
 * Each number always maps to the same color so kids can
 * recognise pieces by color as well as by number.
 * <p>
 * Call applyStyle(button) after every:
 *  - Initial tile distribution
 *  - Tile swap
 *  - Undo
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

    // Pre-parsed int values — avoids Color.parseColor() on every tile tap
    private static final int[] TILE_COLOR_INTS;
    static {
        TILE_COLOR_INTS = new int[TILE_COLORS.length];
        for (int i = 0; i < TILE_COLORS.length; i++) {
            TILE_COLOR_INTS[i] = Color.parseColor(TILE_COLORS[i]);
        }
    }

    // Dark text color pre-parsed once
    private static final int DARK_TEXT_COLOR  = Color.parseColor("#333333");

    // Tile numbers that need dark text (light colored tiles)
    private static final int[] DARK_TEXT_TILES = { 3, 5, 12, 14, 15 };

    /**
     * Applies the correct background color and text color
     * to a tile button based on its current text value.
     * <p>
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
            if (number >= 1 && number <= TILE_COLORS.length) {
                applyFilledStyle(button, number);
            }
        } catch (NumberFormatException e) {
            applyEmptyStyle(button);
        }
    }

    private static void applyFilledStyle(Button button, int number) {
        // Get the tile shape drawable and color it
        Drawable drawable = ContextCompat.getDrawable(
                button.getContext(), R.drawable.tile_shape);

        if (drawable != null) {
            // getConstantState() can return null — guard before calling newDrawable()
            Drawable.ConstantState constantState = drawable.getConstantState();
            if (constantState != null) {
                GradientDrawable tileDrawable =
                        (GradientDrawable) constantState.newDrawable().mutate();
                // Use pre-parsed int — no Color.parseColor() on every tap
                tileDrawable.setColor(TILE_COLOR_INTS[number - 1]);
                button.setBackground(tileDrawable);
            }
        }

        // Apply text color — dark for light tiles, white for dark tiles
        button.setTextColor(needsDarkText(number)
            ? DARK_TEXT_COLOR
            : Color.WHITE);
    }

    private static void applyEmptyStyle(Button button) {
        Drawable drawable = ContextCompat.getDrawable(
                button.getContext(), R.drawable.tile_empty_shape);
        if (drawable != null) {
            button.setBackground(drawable);
        }
        button.setTextColor(Color.TRANSPARENT);
    }

    private static boolean needsDarkText(int number) {
        for (int darkTile : DARK_TEXT_TILES) {
            if (darkTile == number) return true;
        }
        return false;
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

}
