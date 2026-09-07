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
 * using sine waves.
 * <p>
 * Optimized: Caches generated PCM buffers for common sounds.
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

    // Cached PCM buffers to avoid recalculating math
    private final byte[] tapBuffer;
    private final byte[] popBuffer;

    public SoundHelper(Context context) {
        prefs = context.getSharedPreferences(
                AppConstants.PREFS_NAME, Context.MODE_PRIVATE);
        
        // Pre-generate common sounds
        tapBuffer = generateSineWave(700, 60, 0.8f);
        popBuffer = generateSineWave(900, 40, 0.9f);
        
        initialiseBackgroundMusic(context);
    }

    // ── Background Music ──────────────────────────────────

    private void initialiseBackgroundMusic(Context context) {
        int resId = context.getResources().getIdentifier(
                "background_music", "raw", context.getPackageName());
        if (resId == 0) return;

        try {
            backgroundPlayer = MediaPlayer.create(context, resId);
            if (backgroundPlayer != null) {
                backgroundPlayer.setLooping(true);
                backgroundPlayer.setVolume(0.08f, 0.08f);
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

    // ── Sound Effects ─────────────────────────────────────

    public void playTileTap() {
        if (!isSoundEnabled()) return;
        executor.execute(() -> playBuffer(tapBuffer));
    }

    public void playPop() {
        if (!isSoundEnabled()) return;
        executor.execute(() -> playBuffer(popBuffer));
    }

    public void playWinFanfare() {
        if (!isSoundEnabled()) return;
        executor.execute(() -> {
            for (int i = 0; i < FANFARE_NOTES.length; i++) {
                playBuffer(generateSineWave(
                        FANFARE_NOTES[i], FANFARE_DURATIONS[i], 0.7f));
            }
        });
    }

    // ── Settings ──────────────────────────────────────────

    public boolean isSoundEnabled() {
        return prefs.getBoolean(KEY_SOUND_ENABLED, true);
    }

    public void setSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply();
        if (enabled) startMusic();
        else pauseMusic();
    }

    public void release() {
        executor.shutdown();
        if (backgroundPlayer != null) {
            backgroundPlayer.release();
            backgroundPlayer = null;
        }
    }

    // ── Sound Generation ──────────────────────────────────

    private byte[] generateSineWave(int frequency, int durationMs, float volume) {
        int    numSamples = (durationMs * SAMPLE_RATE) / 1000;
        byte[] buffer     = new byte[numSamples * 2];

        for (int i = 0; i < numSamples; i++) {
            double t        = (double) i / SAMPLE_RATE;
            double envelope = Math.sin(Math.PI * i / numSamples);
            double sample   = Math.sin(2 * Math.PI * frequency * t) * envelope * volume;
            short  pcm      = (short) (sample * Short.MAX_VALUE);
            buffer[2 * i]     = (byte) (pcm & 0xFF);
            buffer[2 * i + 1] = (byte) ((pcm >> 8) & 0xFF);
        }
        return buffer;
    }

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

        try {
            AudioTrack track = new AudioTrack.Builder()
                    .setAudioAttributes(attrs)
                    .setAudioFormat(format)
                    .setBufferSizeInBytes(Math.max(buffer.length, minBufferSize))
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build();

            track.write(buffer, 0, buffer.length);

            if (track.getState() == AudioTrack.STATE_INITIALIZED) {
                track.setNotificationMarkerPosition(buffer.length / 2);
                track.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
                    @Override
                    public void onMarkerReached(AudioTrack t) {
                        t.stop();
                        t.release();
                    }
                    @Override public void onPeriodicNotification(AudioTrack t) {}
                });
                track.play();
            } else {
                track.release();
            }
        } catch (Exception e) {
            // Silently ignore audio hardware errors
        }
    }
}
