package com.infinity.silmaperu.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.infinity.silmaperu.R;
import com.infinity.silmaperu.services.LevelFirestoreService;

public class LevelActivity extends AppCompatActivity {

    int level;

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

        levelActivityTitle.setText("LEVEL " + level);

        LevelFirestoreService levelFirestoreService = new LevelFirestoreService(LevelActivity.this);
        levelFirestoreService.getLevelData("level-" + level);
    }
}
