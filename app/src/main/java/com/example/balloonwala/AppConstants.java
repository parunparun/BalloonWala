package com.example.balloonwala;

/**
 * Single source of truth for all shared constants.
 * <p>
 * Centralizes:
 * - Puzzle mode identifiers (replaces magic numbers 9 and 16)
 * - SharedPreferences name (previously duplicated in GameTimer and GameState)
 */
public final class AppConstants {

    // ── Puzzle Modes ──────────────────────────────────────
    /** Column count for 8-puzzle (3×3 grid = 9 cells) */
    public static final int EIGHT_PUZZLE   = 9;

    /** Column count for 15-puzzle (4×4 grid = 16 cells) */
    public static final int FIFTEEN_PUZZLE = 16;

    // ── SharedPreferences ─────────────────────────────────
    /** Shared preferences file name used across the app */
    public static final String PREFS_NAME  = "BalloonWalaPrefs";

    // Private constructor to prevent instantiation
    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
