package com.example.balloonwala.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaPlayer;

import com.example.balloonwala.AppConstants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages all game sounds.
 * <p>
 * Tile tap and win fanfare are generated programmatically
 * using sine waves — no audio files needed for these.
 * <p>
 * Background music is optional — loaded from res/raw/background_music
 * if the file exists. App works silently without it.
 * <p>
 * Sound on/off preference is persisted in SharedPreferences.
 */
public class SoundHelper {

    private static final String KEY_SOUND_ENABLED = "sound_enabled";
    private static final int    SAMPLE_RATE       = 44100;

    // Win fanfare note frequencies (Hz) — C5 E5 G5 C6
    private static final int[]  FANFARE_NOTES     = { 523, 659, 784, 1047 };
    private static final int[]  FANFARE_DURATIONS = { 180, 180, 180, 400  };

    private final SharedPreferences prefs;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private MediaPlayer backgroundPlayer;

    public SoundHelper(Context context) {
        prefs = context.getSharedPreferences(
                AppConstants.PREFS_NAME, Context.MODE_PRIVATE);
        initialiseBackgroundMusic(context);
    }

    // ── Background Music ──────────────────────────────────

    private void initialiseBackgroundMusic(Context context) {
        // Gracefully skip if background_music.ogg hasn't been added yet
        int resId = context.getResources().getIdentifier(
                "background_music", "raw", context.getPackageName());
        if (resId == 0) return;

        try {
            backgroundPlayer = MediaPlayer.create(context, resId);
            if (backgroundPlayer != null) {
                backgroundPlayer.setLooping(true);
                backgroundPlayer.setVolume(0.08f, 0.08f); // soft background level
            }
        } catch (Exception e) {
            backgroundPlayer = null;
        }
    }

    public void startMusic() {
        if (!isSoundEnabled() || backgroundPlayer == null) return;
        if (!backgroundPlayer.isPlaying()) backgroundPlayer.start();
    }

    public void pauseMusic() {
        if (backgroundPlayer != null && backgroundPlayer.isPlaying()) {
            backgroundPlayer.pause();
        }
    }

    public void resumeMusic() {
        startMusic();
    }

    // ── Tile Tap ──────────────────────────────────────────

    /**
     * Plays a short soft pop sound — generated as a sine wave burst.
     * Runs on a background thread so it never blocks the UI.
     */
    public void playTileTap() {
        if (!isSoundEnabled()) return;
        executor.execute(() -> playBuffer(
                generateSineWave(700, 60, 0.8f)));
    }

    /**
     * Plays a satisfying "pop" sound — slightly higher pitch and shorter
     * than the tile tap.
     */
    public void playPop() {
        if (!isSoundEnabled()) return;
        executor.execute(() -> playBuffer(
                generateSineWave(900, 40, 0.9f)));
    }

    // ── Win Fanfare ───────────────────────────────────────

    /**
     * Plays a C–E–G–C ascending fanfare — generated from sine waves.
     * Runs on a background thread.
     */
    public void playWinFanfare() {
        if (!isSoundEnabled()) return;
        executor.execute(() -> {
            for (int i = 0; i < FANFARE_NOTES.length; i++) {
                playBuffer(generateSineWave(
                        FANFARE_NOTES[i], FANFARE_DURATIONS[i], 0.7f));
            }
        });
    }

    // ── Sound Toggle ──────────────────────────────────────

    public boolean isSoundEnabled() {
        return prefs.getBoolean(KEY_SOUND_ENABLED, true);
    }

    public void setSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply();
        if (enabled) startMusic();
        else pauseMusic();
    }

    // ── Release ───────────────────────────────────────────

    public void release() {
        executor.shutdown();
        if (backgroundPlayer != null) {
            backgroundPlayer.release();
            backgroundPlayer = null;
        }
    }

    // ── Sound Generation ──────────────────────────────────

    /**
     * Generates a sine wave as a 16-bit PCM byte buffer.
     * Applies a smooth bell-shaped envelope (sin^2) to avoid clicks.
     *
     * @param frequency  note frequency in Hz
     * @param durationMs duration in milliseconds
     * @param volume     amplitude 0.0–1.0
     */
    private byte[] generateSineWave(int frequency, int durationMs, float volume) {
        int    numSamples = (durationMs * SAMPLE_RATE) / 1000;
        byte[] buffer     = new byte[numSamples * 2]; // 16-bit PCM = 2 bytes per sample

        for (int i = 0; i < numSamples; i++) {
            double t        = (double) i / SAMPLE_RATE;
            double envelope = Math.sin(Math.PI * i / numSamples); // fade in/out
            double sample   = Math.sin(2 * Math.PI * frequency * t) * envelope * volume;
            short  pcm      = (short) (sample * Short.MAX_VALUE);
            buffer[2 * i]     = (byte) (pcm & 0xFF);
            buffer[2 * i + 1] = (byte) ((pcm >> 8) & 0xFF);
        }
        return buffer;
    }

    /**
     * Plays a raw PCM byte buffer using AudioTrack.
     * Blocks until playback is complete — must be called off the main thread.
     */
    private void playBuffer(byte[] buffer) {
        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        AudioFormat format = new AudioFormat.Builder()
                .setSampleRate(SAMPLE_RATE)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build();

        int minBufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AudioTrack track = new AudioTrack.Builder()
                .setAudioAttributes(attrs)
                .setAudioFormat(format)
                .setBufferSizeInBytes(Math.max(buffer.length, minBufferSize))
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build();

        // Stop and release when playback reaches the end — no busy-wait
        track.setNotificationMarkerPosition(buffer.length / 2);
        track.setPlaybackPositionUpdateListener(
            new AudioTrack.OnPlaybackPositionUpdateListener() {
                @Override
                public void onMarkerReached(AudioTrack audioTrack) {
                    audioTrack.stop();
                    audioTrack.release();
                }

                @Override
                public void onPeriodicNotification(AudioTrack audioTrack) {
                    // Not used
                }
            });

        track.write(buffer, 0, buffer.length);

        // Guard against uninitialized AudioTrack — can happen if audio
        // hardware is unavailable. Avoids IllegalStateException on play()
        if (track.getState() == AudioTrack.STATE_UNINITIALIZED) {
            track.release();
            return;
        }

        track.play();
    }
}
