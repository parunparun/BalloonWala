package com.example.balloonwala.helpers;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.balloonwala.BalloonWalaApp;
import com.example.balloonwala.R;
import java.util.Random;

/**
 * Handles the puzzle solved celebration experience.
 * <p>
 * Responsibilities:
 * - Show / hide the win overlay
 * - Run the balloon shower animation (balloons float upward)
 * - Display solved stats and new best indicator
 */
public class CelebrationHelper {

    private static final String[] BALLOON_EMOJIS = {
            "🎈", "🎈", "🎈", "🎉", "⭐", "🎊", "🎈"
    };

    private static final int BALLOON_COUNT        = 12;
    private static final int BALLOON_INTERVAL  = 350;
    private static final int BALLOON_DURATION  = 3000;
    private static final int BALLOON_LOOP_DELAY   = BALLOON_COUNT * BALLOON_INTERVAL;

    private final Activity      activity;
    private final View          celebrationOverlay;
    private final FrameLayout   balloonContainer;
    private final TextView      celebrationStats;
    private final TextView      celebrationBest;
    private final Button        celebrationPlayAgain;
    private final Button        celebrationDismiss;
    
    private final SoundHelper   soundHelper;
    private final SpeechHelper  speechHelper;

    private final Handler       handler = new Handler(Looper.getMainLooper());
    private final Random        random  = new Random();
    private boolean             running = false;

    public CelebrationHelper(Activity activity) {
        this.activity           = activity;
        this.celebrationOverlay = activity.findViewById(R.id.celebrationOverlay);
        this.balloonContainer   = activity.findViewById(R.id.balloonContainer);
        this.celebrationStats   = activity.findViewById(R.id.celebrationStats);
        this.celebrationBest    = activity.findViewById(R.id.celebrationBest);
        this.celebrationPlayAgain = activity.findViewById(R.id.celebrationPlayAgain);
        this.celebrationDismiss   = activity.findViewById(R.id.celebrationDismiss);
        
        // Grab SoundHelper from the Application instance
        BalloonWalaApp app = (BalloonWalaApp) activity.getApplication();
        this.soundHelper = app.getSoundHelper();
        this.speechHelper = app.getSpeechHelper();
    }

    // ── Show / Hide ───────────────────────────────────────

    /**
     * Shows the celebration overlay with balloon shower.
     *
     * @param stats      e.g. "31 moves · 2 min 10 sec"
     * @param newBest    true if this is a new best score/time
     * @param unlockedSticker the sticker unlocked (if any)
     * @param onPlayAgain runs when the player taps Play Again
     */
    public void showCelebration(String stats, boolean newBest, String unlockedSticker, Runnable onPlayAgain) {
        celebrationStats.setText(stats);

        if (unlockedSticker != null) {
            if (unlockedSticker.equals("FULL")) {
                celebrationBest.setText("Collection Complete! 🌟");
            } else {
                celebrationBest.setText(activity.getString(R.string.new_sticker_reward, unlockedSticker));
            }
            celebrationBest.setVisibility(View.VISIBLE);
        } else if (newBest) {
            celebrationBest.setText("🏆 New Best!");
            celebrationBest.setVisibility(View.VISIBLE);
        } else {
            celebrationBest.setVisibility(View.GONE);
        }

        celebrationPlayAgain.setOnClickListener(v -> {
            // Prevent multiple clicks while resetting
            celebrationPlayAgain.setEnabled(false);
            speechHelper.stop();
            hideCelebration();
            onPlayAgain.run();
        });

        celebrationDismiss.setOnClickListener(v -> {
            speechHelper.stop();
            hideCelebration();
        });

        // Fade the overlay in
        celebrationOverlay.setAlpha(0f);
        celebrationOverlay.setVisibility(View.VISIBLE);
        celebrationOverlay.animate()
                .alpha(1f)
                .setDuration(400)
                .start();

        // Start balloon shower
        running = true;
        launchBalloonLoop();
    }

    /** Hides the overlay and stops all balloon animations. */
    public void hideCelebration() {
        running = false;
        handler.removeCallbacksAndMessages(null);
        balloonContainer.removeAllViews();
        celebrationOverlay.setVisibility(View.GONE);
    }

    public boolean isShowing() {
        return celebrationOverlay.getVisibility() == View.VISIBLE;
    }

    // ── Balloon Animation ─────────────────────────────────

    /**
     * Launches balloons in a continuous loop as long as
     * the celebration overlay is visible.
     */
    private void launchBalloonLoop() {
        for (int i = 0; i < BALLOON_COUNT; i++) {
            final int delay = i * BALLOON_INTERVAL;
            handler.postDelayed(this::launchOneBalloon, delay);
        }

        // Loop — relaunch the shower after all balloons are done
        handler.postDelayed(() -> {
            if (running) {
                launchBalloonLoop();
            }
        }, BALLOON_LOOP_DELAY + BALLOON_DURATION);
    }

    private void launchOneBalloon() {
        if (!running) return;

        // Pick a random emoji
        String emoji = BALLOON_EMOJIS[random.nextInt(BALLOON_EMOJIS.length)];

        // Create balloon TextView
        TextView balloon = new TextView(activity);
        balloon.setText(emoji);
        balloon.setTextSize(random.nextInt(16) + 24f); // 24–40sp
        balloon.setAlpha(0.9f);
        
        // INTERACTIVE: Make balloon poppable
        balloon.setOnClickListener(v -> {
            soundHelper.playPop();
            balloon.animate().cancel(); // Stop floating
            balloonContainer.removeView(balloon);
        });

        // Random horizontal position
        int containerWidth = balloonContainer.getWidth();
        float startX = containerWidth > 0
                ? random.nextFloat() * (containerWidth - 60)
                : random.nextFloat() * 400;

        // Start at the bottom, animate upward
        balloon.setX(startX);
        balloon.setY(balloonContainer.getHeight() + 60);

        balloonContainer.addView(balloon);

        // Slight horizontal wobble
        float wobble = (random.nextFloat() - 0.5f) * 80f;

        balloon.animate()
                .translationY(-(balloonContainer.getHeight() + 120))
                .translationXBy(wobble)
                .setDuration(BALLOON_DURATION)
                .withEndAction(() -> balloonContainer.removeView(balloon))
                .start();
    }
}
