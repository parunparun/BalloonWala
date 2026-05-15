package com.example.balloonwala;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void renderEightPuzzle(View view) {
        Intent intent = new Intent(this, PuzzleActivity.class);
        intent.putExtra(AppConstants.COLUMNS, AppConstants.EIGHT_PUZZLE);
        startActivity(intent);
    }

    public void renderFifteenPuzzle(View view) {
        Intent intent = new Intent(this, PuzzleActivity.class);
        intent.putExtra(AppConstants.COLUMNS, AppConstants.FIFTEEN_PUZZLE);
        startActivity(intent);
    }
}
