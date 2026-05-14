package com.example.balloonwala.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.widget.Chronometer;

/**
 * Manages the game timer using Android's Chronometer widget.
 *
 * Responsibilities:
 * - Start, stop, pause, resume and reset the timer
 * - Format elapsed time into a readable string
 * - Track and persist the best time per puzzle mode
 *   using SharedPreferences
 */
public class GameTimer {

    // SharedPreferences keys for best times
    private static final String PREFS_NAME          = "BalloonWalaPrefs";
    private static final String KEY_BEST_TIME_8     = "best_time_8_puzzle";
    private static final String KEY_BEST_TIME_15    = "best_time_15_puzzle";
    private static final long   NO_BEST_TIME        = -1L;

    private final Chronometer chronometer;
    private final SharedPreferences prefs;
    private boolean running = false;

    public GameTimer(Chronometer chronometer, Context context) {
        this.chronometer = chronometer;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ── Controls ──────────────────────────────────────────

    /** Resets and starts the timer from zero. */
    public void start() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        running = true;
    }

    /** Stops the timer permanently (e.g. puzzle solved). */
    public void stop() {
        if (running) {
            chronometer.stop();
            running = false;
        }
    }

    /** Pauses the timer (e.g. app goes to background). */
    public void pause() {
        if (running) {
            chronometer.stop();
        }
    }

    /** Resumes the timer after a pause. */
    public void resume() {
        if (running) {
            chronometer.start();
        }
    }

    /** Resets the timer back to zero without starting it. */
    public void reset() {
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    // ── Elapsed Time ──────────────────────────────────────

    /** Returns elapsed milliseconds since the timer started. */
    public long getElapsedMillis() {
        return SystemClock.elapsedRealtime() - chronometer.getBase();
    }

    /**
     * Formats elapsed milliseconds into a human-readable string.
     * e.g. "43 sec" or "1 min 23 sec"
     */
    public String getFormattedTime() {
        return formatMillis(getElapsedMillis());
    }

    private String formatMillis(long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        if (minutes > 0) {
            return String.format("%d min %02d sec", minutes, seconds);
        } else {
            return String.format("%d sec", seconds);
        }
    }

    // ── Best Time ─────────────────────────────────────────

    /**
     * Checks if the current elapsed time is a new best time.
     * If so, saves it and returns true.
     *
     * @param columns 9 for 8-puzzle, 16 for 15-puzzle
     */
    public boolean checkAndSaveBestTime(int columns) {
        String key = columns == 9 ? KEY_BEST_TIME_8 : KEY_BEST_TIME_15;
        long currentTime = getElapsedMillis();
        long bestTime = prefs.getLong(key, NO_BEST_TIME);

        if (bestTime == NO_BEST_TIME || currentTime < bestTime) {
            prefs.edit().putLong(key, currentTime).apply();
            return true; // new best time!
        }
        return false;
    }

    /**
     * Returns the stored best time as a formatted string.
     * Returns null if no best time exists yet.
     *
     * @param columns 9 for 8-puzzle, 16 for 15-puzzle
     */
    public String getBestTimeFormatted(int columns) {
        String key = columns == 9 ? KEY_BEST_TIME_8 : KEY_BEST_TIME_15;
        long bestTime = prefs.getLong(key, NO_BEST_TIME);
        if (bestTime == NO_BEST_TIME) {
            return null;
        }
        return formatMillis(bestTime);
    }

    /** Clears the saved best time for a given puzzle mode. */
    public void clearBestTime(int columns) {
        String key = columns == 9 ? KEY_BEST_TIME_8 : KEY_BEST_TIME_15;
        prefs.edit().remove(key).apply();
    }
}
