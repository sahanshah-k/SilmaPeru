package com.infinity.silmaperu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.infinity.silmaperu.activities.LevelActivity;
import com.infinity.silmaperu.services.FirestoreService;


public class MainActivity extends AppCompatActivity {

    FirestoreService firestoreService;
    private String movieId;
    private String level;
    AppCompatImageView backButton;

    @Override
    public void onBackPressed() {
        Intent levelActivity = new Intent(getApplicationContext(), LevelActivity.class);
        levelActivity.putExtra("level", Integer.parseInt(level.substring(6)));
        finish();
        startActivity(levelActivity);
    }

    @Override
    protected void onPause() {
        super.onPause();
        firestoreService.updateMap();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firestoreService.updateMap();
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        level = getIntent().getStringExtra("level");
        movieId = getIntent().getStringExtra("movieId");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        backButton = findViewById(R.id.mainactivity_back);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent levelActivity = new Intent(getApplicationContext(), LevelActivity.class);
                levelActivity.putExtra("level", Integer.parseInt(level.substring(6)));
                finish();
                startActivity(levelActivity);
            }
        });

        firestoreService = new FirestoreService(MainActivity.this);


        firestoreService.getCurrentMovieData(level, movieId);



    }
}
