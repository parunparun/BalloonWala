package com.example.balloonwala.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.balloonwala.R;

/**
 * Manages all UI updates for the puzzle screen.
 * <p>
 * Responsibilities:
 * - Update the live move counter display
 * - Show / clear the solved message
 * - Enable / disable the undo button
 * - Show the confirmation dialog (Play Again / Back)
 * <p>
 * PuzzleActivity holds an instance and delegates
 * all UI changes here — keeping the activity clean.
 */
public class UIHelper {
    private final Context context;
    private final TextView movesCountTextView;
    private final Button   undoButton;

    public UIHelper(
            Context  context) {
        this.context = context;
        this.movesCountTextView = null;
        this.undoButton = null;
    }

    public UIHelper(
            Context  context,
            TextView movesCountTextView,
            Button   undoButton) {

        this.context             = context;
        this.movesCountTextView  = movesCountTextView;
        this.undoButton          = undoButton;
    }

    // ── Move Counter ──────────────────────────────────────

    /** Updates the live move counter shown above the puzzle grid. */
    public void updateMovesDisplay(int count) {
        movesCountTextView.setText(String.valueOf(count));
    }

    // ── Undo Button ───────────────────────────────────────

    /** Enables or disables the Undo button. */
    public void setUndoEnabled(boolean enabled) {
        undoButton.setEnabled(enabled);
        undoButton.setAlpha(enabled ? 1.0f : 0.4f);
    }

    // ── Confirm Dialog ────────────────────────────────────

    /**
     * Shows a confirmation dialog with Yes / No buttons.
     * Runs onConfirm if the user taps Yes.
     * <p>
     * Used for both Play Again and Back to Main Menu.
     */
    public void showConfirmDialog(String title, int messageResId, Runnable onConfirm) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(messageResId)
                .setIcon(R.mipmap.balloon_wala_alert)
                .setPositiveButton(android.R.string.ok,
                        (dialog, which) -> onConfirm.run())
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        alertDialog.setOnShowListener(d -> {
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                    .setTextColor(context.getResources()
                            .getColor(android.R.color.holo_red_light));
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(
                            context, android.R.color.holo_green_light));
        });

        alertDialog.show();
    }
}
