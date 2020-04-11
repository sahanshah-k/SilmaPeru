package com.infinity.silmaperu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.infinity.silmaperu.R;

public class LaunchAppActivity extends AppCompatActivity {

    private ImageView playImageView;
    private Intent intent;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_app);

        playImageView = findViewById(R.id.play);
        intent = new Intent(getApplicationContext(), StartActivity.class);
        playImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                finish();
            }
        });
    }
}
