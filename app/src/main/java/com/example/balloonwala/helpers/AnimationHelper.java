package com.example.balloonwala.helpers;

import android.view.animation.DecelerateInterpolator;
import android.widget.Button;

/**
 * Animates puzzle tile slides.
 * <p>
 * When a tile is tapped, it smoothly slides toward the empty slot.
 * Text content is swapped only after the animation completes,
 * giving the illusion of the physical tile moving.
 * <p>
 * Uses an isAnimating flag to prevent double-taps during animation.
 */
public class AnimationHelper {

    private static final long SLIDE_DURATION_MS = 120;

    private static volatile boolean animating = false;

    /** Returns true if a tile slide is currently in progress. */
    public static boolean isAnimating() {
        return animating;
    }

    /**
     * Animates the tile at {@code from} sliding toward {@code to}.
     * <p>
     * Flow:
     * 1. Translate {@code from} to the position of {@code to}
     * 2. On completion: reset translation, run onComplete
     *    (caller swaps text and applies tile colors in onComplete)
     *
     * @param from       the tile button being tapped (has a number)
     * @param to         the empty tile button (slide target)
     * @param onComplete runs after animation — perform the text swap here
     */
    public static void animateTileSlide(Button from, Button to, Runnable onComplete) {
        if (animating) return;
        animating = true;

        // Calculate how far and in which direction to slide
        float dx = to.getX() - from.getX();
        float dy = to.getY() - from.getY();

        from.animate()
                .translationX(dx)
                .translationY(dy)
                .setDuration(SLIDE_DURATION_MS)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    // Reset visual position — text will be swapped by caller
                    from.setTranslationX(0f);
                    from.setTranslationY(0f);
                    animating = false;
                    onComplete.run();
                })
                .start();
    }
}
