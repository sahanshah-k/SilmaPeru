package com.infinity.silmaperu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.infinity.silmaperu.R;

public class AboutActivity extends AppCompatActivity {

    AppCompatImageView backButton;

    @Override
    public void onBackPressed() {
        Intent startIntent = new Intent(getApplicationContext(), LaunchAppActivity.class);
        startActivity(startIntent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);

        backButton = findViewById(R.id.aboutactivity_back);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), LaunchAppActivity.class);
                startActivity(startIntent);
                finish();
            }
        });

    }

}
