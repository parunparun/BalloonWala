package com.example.balloonwala;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Splash screen shown briefly on app launch.
 * Navigates automatically to MainActivity after a short delay.
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Local variable — only needed here in onCreate
        ImageView balloon = findViewById(R.id.imageView);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        balloon.setAnimation(animation);

        // Use Looper.getMainLooper() to explicitly tie the handler to the main thread
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            SplashActivity.this.startActivity(intent);
            SplashActivity.this.finish();
        }, SPLASH_DELAY_MS);
    }
}
