package com.example.balloonwala.helpers;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

/**
 * Displays a visual hint by pulsing the tile that should
 * be moved next toward the solution.
 * <p>
 * The tile scales up and down 3 times over ~1.8 seconds,
 * drawing the child's eye to the correct piece.
 * <p>
 * Does not auto-make the move — the player still taps it.
 */
public class HintHelper {

    private static final long PULSE_DURATION_MS = 600;
    private static final int  PULSE_REPEAT      = 3;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private ObjectAnimator currentAnimator;

    // ── Show Hint ─────────────────────────────────────────

    /**
     * Pulses the given tile button to show it's the next correct move.
     * Resets the tile scale cleanly after animation completes.
     *
     * @param tileButton the button to highlight
     */
    public void showHint(Button tileButton) {
        cancelCurrent();

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(
                "scaleX", 1f, 1.18f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(
                "scaleY", 1f, 1.18f, 1f);

        currentAnimator = ObjectAnimator.ofPropertyValuesHolder(
                tileButton, scaleX, scaleY);
        currentAnimator.setDuration(PULSE_DURATION_MS);
        currentAnimator.setRepeatCount(PULSE_REPEAT);
        currentAnimator.setRepeatMode(ObjectAnimator.RESTART);
        currentAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        // Reset scale cleanly when animation ends
        currentAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                tileButton.setScaleX(1f);
                tileButton.setScaleY(1f);
            }
        });

        currentAnimator.start();
    }

    // ── Cancel ────────────────────────────────────────────

    /**
     * Cancels any active hint animation and resets the tile scale.
     * Call from onDestroy or when starting a new game.
     */
    public void cancel() {
        cancelCurrent();
        handler.removeCallbacksAndMessages(null);
    }

    private void cancelCurrent() {
        if (currentAnimator != null && currentAnimator.isRunning()) {
            currentAnimator.cancel();
            currentAnimator = null;
        }
    }
}
