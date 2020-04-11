package com.infinity.silmaperu.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.infinity.silmaperu.R;
import com.infinity.silmaperu.config.GlideApp;
import com.infinity.silmaperu.services.SyncService;


public class LoadingActivity extends AppCompatActivity {

    private ImageView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        loading = findViewById(R.id.loading);

        GlideApp.with(getApplicationContext())
                .asGif()
                .load(R.raw.loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(loading);

        SyncService syncService = new SyncService(LoadingActivity.this);

        syncService.checkAndSync();

    }
}
