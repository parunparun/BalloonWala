package com.example.balloonwala;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balloonwala.helpers.ButtonStyleHelper;
import com.example.balloonwala.helpers.SoundHelper;
import com.example.balloonwala.helpers.UIHelper;

/**
 * Home screen — puzzle mode selection.
 * <p>
 * Manages background music lifecycle and the sound toggle button.
 * Delegates all sound state to SoundHelper via BalloonWalaApp.
 */
public class MainActivity extends AppCompatActivity {

    private SoundHelper soundHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Full-screen fun — no toolbar on the home screen
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        soundHelper = ((BalloonWalaApp) getApplication()).getSoundHelper();
        updateSoundToggleButton();
        styleButtons();
        startBalloonFloat();
    }

    private void startBalloonFloat() {
        View balloon = findViewById(com.example.balloonwala.R.id.homeBalloon);
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
        ButtonStyleHelper.styleSoundToggle(findViewById(R.id.soundToggleBtn));
        ButtonStyleHelper.styleQuit(findViewById(R.id.quitBtn));
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
