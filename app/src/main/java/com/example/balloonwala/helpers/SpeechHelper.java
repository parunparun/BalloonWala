package com.example.balloonwala.helpers;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Helper to manage TextToSpeech lifecycle and operations.
 */
public class SpeechHelper implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private boolean initialized = false;

    private String pendingSpeech = null;

    public SpeechHelper(Context context) {
        tts = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("SpeechHelper", "Language not supported");
            } else {
                initialized = true;
                // Speak anything that was requested before we were ready
                if (pendingSpeech != null) {
                    speak(pendingSpeech);
                    pendingSpeech = null;
                }
            }
        } else {
            Log.e("SpeechHelper", "Initialization failed");
        }
    }

    /**
     * Reads the provided text aloud.
     */
    public void speak(String text) {
        if (initialized && tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            pendingSpeech = text;
        }
    }

    /**
     * Stops any current speech immediately.
     */
    public void stop() {
        if (tts != null) {
            tts.stop();
        }
    }

    /**
     * Stops and releases TTS resources.
     */
    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
