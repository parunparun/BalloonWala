package com.example.balloonwala;

import android.app.Application;

import com.example.balloonwala.helpers.SoundHelper;
import com.example.balloonwala.helpers.SpeechHelper;
import com.example.balloonwala.helpers.StickerHelper;

/**
 * Application class — lives for the entire app lifetime.
 * <p>
 * Holds single instances of helpers so they persist across activities.
 * <p>
 * ⚠️ Important: Add android:name=".BalloonWalaApp" to the
 * <application> tag in AndroidManifest.xml
 */
public class BalloonWalaApp extends Application {

    private SoundHelper soundHelper;
    private SpeechHelper speechHelper;
    private StickerHelper stickerHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        soundHelper = new SoundHelper(this);
        speechHelper = new SpeechHelper(this);
        stickerHelper = new StickerHelper(this);
    }

    public SoundHelper getSoundHelper() {
        return soundHelper;
    }

    public SpeechHelper getSpeechHelper() {
        return speechHelper;
    }

    public StickerHelper getStickerHelper() {
        return stickerHelper;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (soundHelper != null) {
            soundHelper.release();
        }
        if (speechHelper != null) {
            speechHelper.shutdown();
        }
    }
}
