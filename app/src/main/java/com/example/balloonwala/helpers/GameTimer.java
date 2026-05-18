package com.example.balloonwala.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.widget.Chronometer;

import com.example.balloonwala.AppConstants;

/**
 * Manages the game timer using Android's Chronometer widget.
 * <p>
 * Responsibilities:
 * - Start, stop, pause, resume and reset the timer
 * - Format elapsed time into a readable string
 * - Track and persist the best time per puzzle mode
 *   using SharedPreferences
 */
public class GameTimer {

    // SharedPreferences keys for best times
    private static final String KEY_BEST_TIME_8  = "best_time_8_puzzle";
    private static final String KEY_BEST_TIME_15 = "best_time_15_puzzle";
    private static final long   NO_BEST_TIME     = -1L;

    private final Chronometer       chronometer;
    private final SharedPreferences prefs;
    private boolean                 running = false;
    private long                    pausedAt = 0L;

    public GameTimer(Chronometer chronometer, Context context) {
        this.chronometer = chronometer;
        this.prefs = context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE);
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
            pausedAt = SystemClock.elapsedRealtime();
        }
    }
    

    /** Resumes the timer after a pause — excludes background time. */
    public void resume() {
        if (running) {
            // Shift base forward by however long we were paused
            // so background time is never counted as game time
            long pauseDuration = SystemClock.elapsedRealtime() - pausedAt;
            chronometer.setBase(chronometer.getBase() + pauseDuration);
            chronometer.start();
        }
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
        return minutes > 0
                ? String.format("%d min %02d sec", minutes, seconds)
                : String.format("%d sec", seconds);
    }

    // ── Best Time ─────────────────────────────────────────

    /**
     * Checks if the current elapsed time is a new best time.
     * If so, saves it and returns true.
     *
     * @param columns 9 for 8-puzzle, 16 for 15-puzzle
     */
    public boolean checkAndSaveBestTime(int columns) {
        String key = getKey(columns);
        long currentTime = getElapsedMillis();
        long bestTime = prefs.getLong(key, NO_BEST_TIME);

        if (bestTime == NO_BEST_TIME || currentTime < bestTime) {
            prefs.edit().putLong(key, currentTime).apply();
            return true; // new best time!
        }
        return false;
    }

    private String getKey(int columns) {
        return columns == AppConstants.EIGHT_PUZZLE
                ? KEY_BEST_TIME_8
                : KEY_BEST_TIME_15;
    }
}
