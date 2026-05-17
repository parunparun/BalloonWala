package com.example.balloonwala;

import android.app.Application;

import com.example.balloonwala.helpers.SoundHelper;

/**
 * Application class — lives for the entire app lifetime.
 *
 * Holds a single SoundHelper instance so background music
 * continues seamlessly between MainActivity and PuzzleActivity
 * without restarting or duplicating players.
 *
 * ⚠️ Important: Add android:name=".BalloonWalaApp" to the
 * <application> tag in AndroidManifest.xml
 */
public class BalloonWalaApp extends Application {

    private SoundHelper soundHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        soundHelper = new SoundHelper(this);
    }

    public SoundHelper getSoundHelper() {
        return soundHelper;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (soundHelper != null) {
            soundHelper.release();
        }
    }
}
