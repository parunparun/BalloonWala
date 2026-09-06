package com.example.balloonwala.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.view.Gravity;

import androidx.core.content.ContextCompat;

import com.example.balloonwala.R;

import java.util.List;
import java.util.Set;

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

    /**
     * Shows a dialog with a custom View.
     */
    public void showCustomDialog(String title, View customView, Runnable onDismiss) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(customView)
                .setIcon(R.mipmap.balloon_wala_alert)
                .setPositiveButton(R.string.how_to_play_got_it, null)
                .setOnDismissListener(dialog -> {
                    if (onDismiss != null) onDismiss.run();
                })
                .show();
    }

    /**
     * Shows a simple info dialog with a single "Got it!" button.
     * Used for the "How to Play" screen.
     */
    public void showInfoDialog(String title, int messageResId, Runnable onDismiss) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(messageResId)
                .setIcon(R.mipmap.balloon_wala_alert)
                .setPositiveButton(R.string.how_to_play_got_it, null)
                .setOnDismissListener(dialog -> {
                    if (onDismiss != null) onDismiss.run();
                })
                .show();
    }

    /**
     * Shows a simple info dialog with a custom message string.
     */
    public void showInfoDialog(String title, String message, Runnable onDismiss) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.mipmap.balloon_wala_alert)
                .setPositiveButton(R.string.how_to_play_got_it, null)
                .setOnDismissListener(dialog -> {
                    if (onDismiss != null) onDismiss.run();
                })
                .show();
    }

    /**
     * Shows the Sticker Book gallery.
     */
    public void showStickerBook(StickerHelper stickerHelper, Runnable onDismiss) {
        Set<String> unlocked = stickerHelper.getUnlockedStickers();
        List<String> all = stickerHelper.getAllStickers();

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_sticker_book, null);
        GridView gridView = dialogView.findViewById(R.id.stickerGridView);

        gridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() { return all.size(); }
            @Override
            public Object getItem(int position) { return all.get(position); }
            @Override
            public long getItemId(int position) { return position; }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) convertView;
                if (tv == null) {
                    tv = new TextView(context);
                    tv.setLayoutParams(new GridView.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 120));
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextSize(32);
                }

                String sticker = all.get(position);
                if (unlocked.contains(sticker)) {
                    tv.setText(sticker);
                    tv.setAlpha(1.0f);
                } else {
                    tv.setText("❓");
                    tv.setAlpha(0.2f);
                }
                return tv;
            }
        });

        showCustomDialog(
                context.getString(R.string.sticker_book) + " (" + unlocked.size() + "/" + all.size() + ")",
                dialogView,
                onDismiss
        );
    }
}
