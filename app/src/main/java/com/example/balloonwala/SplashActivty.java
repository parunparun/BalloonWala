package com.example.balloonwala;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

/* JADX INFO: loaded from: classes2.dex */
public class SplashActivty extends AppCompatActivity {
    ImageView balloon;
    Animation balloonAnimation;

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.balloon = (ImageView) findViewById(R.id.imageView);
        Animation animationLoadAnimation = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        this.balloonAnimation = animationLoadAnimation;
        this.balloon.setAnimation(animationLoadAnimation);
        new Handler().postDelayed(new Runnable() { // from class: com.example.balloonwala.SplashActivty.1
            @Override // java.lang.Runnable
            public void run() {
                Intent intent = new Intent(SplashActivty.this, (Class<?>) MainActivity.class);
                SplashActivty.this.startActivity(intent);
                SplashActivty.this.finish();
            }
        }, 9000L);
    }
}
