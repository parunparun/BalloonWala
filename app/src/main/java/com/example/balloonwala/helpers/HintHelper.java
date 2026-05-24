package com.example.balloonwala.helpers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.example.balloonwala.R;
import com.example.balloonwala.views.HintArrowView;

/**
 * Displays a three-part visual hint:
 * <p>
 * 1. PULSE   — the correct tile scales up and down 3 times
 * 2. GLOW    — the empty slot gets an amber border and tint
 * 3. ARROW   — a custom-drawn amber arrow drawn from the center of
 *              the hint tile to the center of the empty slot, making
 *              the direction of movement unmistakably clear for kids
 * <p>
 * The player still makes the move themselves — hint only shows
 * which tile to move and where it goes.
 */
public class HintHelper {

    private static final long PULSE_DURATION_MS = 600;
    private static final int  PULSE_REPEAT      = 3;

    private final HintArrowView arrowView;
    private ObjectAnimator      currentAnimator;
    private Button              glowingEmptyButton;

    public HintHelper(HintArrowView arrowView) {
        this.arrowView = arrowView;
    }

    // ── Show Hint ─────────────────────────────────────────

    /**
     * Shows the full three-part hint:
     * pulse on tileButton, glow on emptyButton, arrow between them.
     *
     * @param tileButton  the tile that should move next
     * @param emptyButton the adjacent empty slot to slide into
     */
    public void showHint(Button tileButton, Button emptyButton) {
        cancelCurrent();

        // 1 — Pulse the hint tile
        pulseTile(tileButton);

        // 2 — Glow the empty slot
        glowingEmptyButton = emptyButton;
        emptyButton.setBackground(ContextCompat.getDrawable(
                emptyButton.getContext(), R.drawable.tile_hint_glow));

        // 3 — Draw arrow after layout has settled (post ensures coordinates are ready)
        tileButton.post(() -> {
            // Guard: hint may have been canceled before post fires
            if (glowingEmptyButton == null || arrowView == null) return;

            float[] tileCenter  = getCenter(tileButton,  arrowView);
            float[] emptyCenter = getCenter(emptyButton, arrowView);
            arrowView.showArrow(
                    tileCenter[0],  tileCenter[1],
                    emptyCenter[0], emptyCenter[1]);
        });
    }

    // ── Cancel ────────────────────────────────────────────

    /**
     * Cancels all hint visuals:
     * stops pulse, resets tile scale, removes glow,
     * clears arrow from overlay.
     */
    public void cancel() {
        cancelCurrent();

        if (glowingEmptyButton != null) {
            TileStyleHelper.applyStyle(glowingEmptyButton);
            glowingEmptyButton = null;
        }

        if (arrowView != null) {
            arrowView.clearArrow();
        }
    }

    // ── Pulse ─────────────────────────────────────────────

    private void pulseTile(Button tileButton) {
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

        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                tileButton.setScaleX(1f);
                tileButton.setScaleY(1f);
            }
        });

        currentAnimator.start();
    }

    private void cancelCurrent() {
        if (currentAnimator != null && currentAnimator.isRunning()) {
            currentAnimator.cancel();
            currentAnimator = null;
        }
    }

    // ── Arrow Coordinate Calculation ──────────────────────

    /**
     * Returns the center of `view` in `overlayView`'s local coordinate space.
     * <p>
     * Using center-to-center (rather than edge-to-edge) guarantees the
     * arrow is always visible regardless of whether tiles have a gap
     * between them — horizontally adjacent tiles have zero gap.
     */
    private float[] getCenter(View view, View overlayView) {
        int[] viewLoc    = new int[2];
        int[] overlayLoc = new int[2];
        view.getLocationOnScreen(viewLoc);
        overlayView.getLocationOnScreen(overlayLoc);
        return new float[]{
                (viewLoc[0] - overlayLoc[0]) + view.getWidth()  / 2f,
                (viewLoc[1] - overlayLoc[1]) + view.getHeight() / 2f
        };
    }
}
