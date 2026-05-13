package com.example.balloonwala;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/* JADX INFO: loaded from: classes2.dex */
public class MainActivity extends AppCompatActivity {
    public static final String COLUMNS = "com.example.balloonwala.MESSAGE";

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void renderEightPuzzle(View view) {
        Intent intent = new Intent(this, PuzzleActivity.class);
        intent.putExtra(COLUMNS, 9);
        startActivity(intent);
    }

    public void renderFifteenPuzzle(View view) {
        Intent intent = new Intent(this, PuzzleActivity.class);
        intent.putExtra(COLUMNS, 16);
        startActivity(intent);
    }
}
