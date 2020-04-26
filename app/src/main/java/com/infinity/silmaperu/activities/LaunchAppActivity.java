package com.infinity.silmaperu.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.infinity.silmaperu.R;

public class LaunchAppActivity extends AppCompatActivity {

    private ImageView playImageView;
    private ImageView helpImageView;
    private ImageView byeImageView;
    private Intent intent;
    private Intent aboutIntent;
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_app);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        playImageView = findViewById(R.id.play);
        helpImageView = findViewById(R.id.help);
        byeImageView = findViewById(R.id.bye);

        intent = new Intent(getApplicationContext(), StartActivity.class);
        aboutIntent = new Intent(getApplicationContext(), AboutActivity.class);
        playImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                finish();
            }
        });

        helpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(aboutIntent);
                finish();
            }
        });

        byeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setMessage("Are you sure to say Bye?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }
}
