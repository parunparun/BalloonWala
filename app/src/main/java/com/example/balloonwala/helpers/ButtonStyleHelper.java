package com.example.balloonwala.helpers;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;

import androidx.core.view.ViewCompat;

import com.google.android.material.button.MaterialButton;

/**
 * Sets action button colors programmatically.
 *
 * android:backgroundTint is unreliable with custom drawable
 * backgrounds in Material Components — this class sets colors
 * directly via GradientDrawable to guarantee correct rendering.
 */
public class ButtonStyleHelper {

    // Button colors
    private static final int COLOR_SOUND_TOGGLE = Color.parseColor("#DFE6E9"); // Green
    private static final int COLOR_SOUND_TOGGLE_TEXT = Color.parseColor("#636E72"); // Green

    private static final int COLOR_QUIT = Color.parseColor("#FF6B6B"); // Green
    private static final int COLOR_QUIT_TEXT = Color.parseColor("#636E72"); // Green

    private static final int COLOR_PLAY_AGAIN = Color.parseColor("#1DD1A1"); // Green
    private static final int COLOR_UNDO        = Color.parseColor("#DFE6E9"); // Grey
    private static final int COLOR_UNDO_TEXT   = Color.parseColor("#636E72"); // Grey text
    private static final int COLOR_MENU        = Color.parseColor("#54A0FF"); // Blue
    private static final int COLOR_HINT        = Color.parseColor("#FDCB6E"); // Amber
    private static final int COLOR_SOLUTION    = Color.parseColor("#6C5CE7"); // Indigo
    private static final int COLOR_PRIMARY     = Color.parseColor("#FF6B6B"); // Red

    private static final int CORNER_RADIUS_DP  = 14;

    // ── Public API ────────────────────────────────────────

    public static void styleSoundToggle(Button button) {
        applyStyle(button, COLOR_SOUND_TOGGLE, COLOR_SOUND_TOGGLE_TEXT);
    }

    public static void styleHowToPlay(Button button) {
        applyStyle(button, COLOR_SOUND_TOGGLE, COLOR_SOUND_TOGGLE_TEXT);
    }

    public static void styleStickerBook(Button button) {
        applyStyle(button, COLOR_SOLUTION, Color.WHITE);
    }

    public static void styleQuit(Button button) {
        applyStyle(button, COLOR_QUIT, COLOR_QUIT_TEXT);
    }
    public static void stylePlayAgain(Button button) {
        applyStyle(button, COLOR_PLAY_AGAIN, Color.WHITE);
    }

    public static void styleUndo(Button button) {
        applyStyle(button, COLOR_UNDO, COLOR_UNDO_TEXT);
    }

    public static void styleMenu(Button button) {
        applyStyle(button, COLOR_MENU, Color.WHITE);
    }

    public static void styleHint(Button button) {
        applyStyle(button, COLOR_HINT, Color.WHITE);
    }

    public static void styleSolution(Button button) {
        applyStyle(button, COLOR_SOLUTION, Color.WHITE);
    }

    public static void stylePrimary(Button button) {
        applyStyle(button, COLOR_PRIMARY, Color.WHITE);
    }

    // ── Private Helper ────────────────────────────────────

    private static void applyStyle(Button button, int bgColor, int textColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(dpToPx(CORNER_RADIUS_DP, button));
        drawable.setColor(bgColor);
        // Reset state list animator — prevents Material from overriding background
        button.setStateListAnimator(null);
        // FIX: Forces MaterialButtons to accept the custom GradientDrawable background
        ViewCompat.setBackground(button, drawable);
        // FIX Alternative: Clear material tint properties if they are still overriding colors
        if (button instanceof MaterialButton) {
            ((MaterialButton) button).setBackgroundTintList(null);
        }
        button.setTextColor(textColor);
    }

    private static float dpToPx(int dp, Button button) {
        return dp * button.getContext().getResources()
                .getDisplayMetrics().density;
    }
}