package com.infinity.silmaperu.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.infinity.silmaperu.R;
import com.infinity.silmaperu.services.SyncService;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        SyncService syncService = new SyncService(StartActivity.this);

        System.out.println("Started");

        syncService.syncAllData();

    }
}
