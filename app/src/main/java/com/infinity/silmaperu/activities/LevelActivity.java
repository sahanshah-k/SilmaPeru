package com.infinity.silmaperu.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.infinity.silmaperu.R;
import com.infinity.silmaperu.services.LevelFirestoreService;

public class LevelActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        Intent startIntent = new Intent(getApplicationContext(), StartActivity.class);
        finish();
        startActivity(startIntent);
    }

    int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_activity);
        level = getIntent().getIntExtra("level", 0);


        LevelFirestoreService levelFirestoreService = new LevelFirestoreService(LevelActivity.this);
        levelFirestoreService.getLevelData("level-" + level);
    }
}
