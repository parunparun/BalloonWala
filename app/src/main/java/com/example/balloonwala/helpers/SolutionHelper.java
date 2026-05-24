package com.example.balloonwala.helpers;

import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import com.example.balloonwala.utils.GenericUtils;
import java.util.List;

/**
 * Autoplay the puzzle solution step by step.
 * <p>
 * For each move in the solution:
 * 1. Finds the tile button to move (by tile number)
 * 2. Finds the adjacent empty button
 * 3. Animates the slide via AnimationHelper
 * 4. Swaps tile content and applies colors
 * 5. Waits a short delay then plays the next move
 * <p>
 * Blocks player input during playback via isPlaying().
 */
public class SolutionHelper {

    private static final long STEP_DELAY_MS = 350; // pause between moves

    private final Handler  handler    = new Handler(Looper.getMainLooper());
    private boolean        playing    = false;

    // ── Play ──────────────────────────────────────────────

    /**
     * Begins autoplaying the solution.
     *
     * @param moves      ordered list of tile numbers to move
     * @param buttonList current ordered list of puzzle tile buttons
     * @param onComplete called when all moves are done
     */
    public void playSolution(
            List<Integer>  moves,
            List<Button>   buttonList,
            Runnable       onComplete) {

        playing = true;
        playStep(moves, buttonList, 0, onComplete);
    }

    private void playStep(
            List<Integer> moves,
            List<Button>  buttonList,
            int           index,
            Runnable      onComplete) {

        // Stopped externally (e.g. onDestroy) — do NOT call onComplete
        if (!playing) return;

        if (index >= moves.size()) {
            playing = false;
            if (onComplete != null) onComplete.run();
            return;
        }

        int    tileNumber  = moves.get(index);
        Button tileButton  = GenericUtils.findButtonForTile(tileNumber, buttonList);
        Button emptyButton = GenericUtils.findEmptyButton(buttonList);

        // Safety guard — solver guarantees these are always valid
        if (tileButton == null || emptyButton == null) {
            playing = false;
            return;
        }

        // Animate slide → swap text → apply colors → schedule next step
        AnimationHelper.animateTileSlide(tileButton, emptyButton, () -> {
            GenericUtils.swapData(tileButton, emptyButton);
            TileStyleHelper.applyStyle(tileButton);
            TileStyleHelper.applyStyle(emptyButton);

            handler.postDelayed(
                    () -> playStep(moves, buttonList, index + 1, onComplete),
                    STEP_DELAY_MS);
        });
    }

    // ── Stop ──────────────────────────────────────────────

    /**
     * Stops autoplay immediately.
     * Call from onDestroy or when the player cancels.
     */
    public void stop() {
        playing = false;
        handler.removeCallbacksAndMessages(null);
    }

    public boolean isPlaying() {
        return playing;
    }
}
