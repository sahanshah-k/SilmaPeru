package com.infinity.silmaperu.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.infinity.silmaperu.R;
import com.infinity.silmaperu.services.LevelFirestoreService;

public class LevelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_activity);

        LevelFirestoreService levelFirestoreService = new LevelFirestoreService(LevelActivity.this);
        levelFirestoreService.getLevelData("level-1");
    }
}
