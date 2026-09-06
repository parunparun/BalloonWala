package com.example.balloonwala;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balloonwala.helpers.ButtonStyleHelper;
import com.example.balloonwala.helpers.SoundHelper;
import com.example.balloonwala.helpers.SpeechHelper;
import com.example.balloonwala.helpers.StickerHelper;
import com.example.balloonwala.helpers.UIHelper;

import java.util.List;
import java.util.Set;

/**
 * Home screen — puzzle mode selection.
 * <p>
 * Manages background music lifecycle and the sound toggle button.
 * Delegates all sound state to SoundHelper via BalloonWalaApp.
 */
public class MainActivity extends AppCompatActivity {

    private SoundHelper soundHelper;
    private SpeechHelper speechHelper;
    private StickerHelper stickerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Full-screen fun — no toolbar on the home screen
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        soundHelper = ((BalloonWalaApp) getApplication()).getSoundHelper();
        speechHelper = ((BalloonWalaApp) getApplication()).getSpeechHelper();
        stickerHelper = ((BalloonWalaApp) getApplication()).getStickerHelper();
        updateSoundToggleButton();
        styleButtons();
        startBalloonFloat();
    }

    private void startBalloonFloat() {
        View balloon = findViewById(R.id.homeBalloon);
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                balloon, "translationY", 0f, -30f);
        animator.setDuration(1500);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    // ── Lifecycle — music ──────────────────────────────────

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("PuzzleDebug", "onResume called");
        soundHelper.resumeMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundHelper.pauseMusic();
    }

    // ── Navigation ─────────────────────────────────────────

    public void renderEightPuzzle(View view) {
        Intent intent = new Intent(this, PuzzleActivity.class);
        intent.putExtra(NavigationConstants.COLUMNS, AppConstants.EIGHT_PUZZLE);
        startActivity(intent);
    }

    public void renderFifteenPuzzle(View view) {
        Intent intent = new Intent(this, PuzzleActivity.class);
        intent.putExtra(NavigationConstants.COLUMNS, AppConstants.FIFTEEN_PUZZLE);
        startActivity(intent);
    }

    // ── Sound Toggle ───────────────────────────────────────

    public void toggleSound(View view) {
        soundHelper.setSoundEnabled(!soundHelper.isSoundEnabled());
        updateSoundToggleButton();
    }

    private void updateSoundToggleButton() {
        Button btn = findViewById(R.id.soundToggleBtn);
        if (soundHelper.isSoundEnabled()) {
            btn.setText(getString(R.string.sound_on));
        } else {
            btn.setText(getString(R.string.sound_off));
        }
    }

    private void styleButtons() {
        ButtonStyleHelper.styleHowToPlay(findViewById(R.id.howToPlayBtn));
        ButtonStyleHelper.styleSoundToggle(findViewById(R.id.soundToggleBtn));
        ButtonStyleHelper.styleStickerBook(findViewById(R.id.stickerBookBtn));
        ButtonStyleHelper.styleQuit(findViewById(R.id.quitBtn));
    }

    public void showHowToPlay(View view) {
        String rules = getString(R.string.how_to_play_rules);
        speechHelper.speak(rules);

        UIHelper uiHelper = new UIHelper(this);
        uiHelper.showInfoDialog(
                getString(R.string.how_to_play_title),
                R.string.how_to_play_rules,
                () -> speechHelper.stop()); // STOP speaking when "Got it" is clicked
    }

    public void showStickerBook(View view) {
        UIHelper uiHelper = new UIHelper(this);
        uiHelper.showStickerBook(stickerHelper, () -> speechHelper.stop());
    }

    public void quitApp(View view) {
        UIHelper uiHelper = new UIHelper(this);
        uiHelper.showConfirmDialog(
                getString(R.string.quit_confirm_title),
                R.string.quit_confirm_message,
                () -> {
                    finishAffinity();
                });
    }
}
