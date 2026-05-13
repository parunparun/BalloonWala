package com.example.balloonwala;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

/* JADX INFO: loaded from: classes2.dex */
public class SplashActivity extends AppCompatActivity {
    ImageView balloon;
    Animation balloonAnimation;

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.balloon = findViewById(R.id.imageView);
        Animation animationLoadAnimation = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        this.balloonAnimation = animationLoadAnimation;
        this.balloon.setAnimation(animationLoadAnimation);
        // from class: com.example.balloonwala.SplashActivty.1
// java.lang.Runnable
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            SplashActivity.this.startActivity(intent);
            SplashActivity.this.finish();
        }, 9000L);
    }
}
