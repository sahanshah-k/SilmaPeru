package com.infinity.silmaperu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.infinity.silmaperu.R;
import com.infinity.silmaperu.services.LevelFirestoreService;

public class LevelActivity extends AppCompatActivity {

    int level;
    AppCompatImageView backButton;

    @Override
    public void onBackPressed() {
        Intent startIntent = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_activity);
        level = getIntent().getIntExtra("level", 0);

        TextView levelActivityTitle = findViewById(R.id.level_activity_title);
        backButton = findViewById(R.id.levelactivity_back);
        
        levelActivityTitle.setText("LEVEL " + level);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), StartActivity.class);
                startActivity(startIntent);
                finish();
            }
        });

        LevelFirestoreService levelFirestoreService = new LevelFirestoreService(LevelActivity.this);
        levelFirestoreService.getLevelData("level-" + level);
    }
}
