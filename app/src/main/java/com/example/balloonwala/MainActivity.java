package com.example.balloonwala;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.balloonwala.helpers.SoundHelper;

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
        btn.setText(soundHelper.isSoundEnabled()
                ? getString(R.string.sound_on)
                : getString(R.string.sound_off));
    }
}
