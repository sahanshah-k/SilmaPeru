package com.infinity.silmaperu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.infinity.silmaperu.R;
import com.infinity.silmaperu.domain.ListModel;
import com.infinity.silmaperu.domain.MovieData;
import com.infinity.silmaperu.services.StartService;
import com.infinity.silmaperu.utilities.CustomAdapter;
import com.infinity.silmaperu.utilities.SoundUtil;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static com.infinity.silmaperu.config.Constants.TOTAL_LEVELS;

public class StartActivity extends AppCompatActivity {

    private static CustomAdapter adapter;
    ListView listView;
    ArrayList<ListModel> dataModels;
    Realm realm;
    Intent intent;
    Intent toMainMenu;
    AppCompatImageView backButton;
    private int doneMonitor;
    private int levelUnlockCounter;

    @Override
    public void onBackPressed() {
        toMainMenu = new Intent(getApplicationContext(), LaunchAppActivity.class);
        startActivity(toMainMenu);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        realm = Realm.getDefaultInstance();

        StartService startService = new StartService(StartActivity.this);

        listView = (ListView) findViewById(R.id.level_list);

        dataModels = new ArrayList<>();

        intent = new Intent(getApplicationContext(), LevelActivity.class);
        backButton = findViewById(R.id.startactivity_back);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainMenu = new Intent(getApplicationContext(), LaunchAppActivity.class);
                startActivity(toMainMenu);
                finish();
            }
        });

        System.out.println("Started");

        for (int i = 0; i < TOTAL_LEVELS; i++) {
            List<MovieData> movieDataList = realm.where(MovieData.class).equalTo("levelId", "level-" + (i + 1)).findAll();
            int tempTotal = 0;
            int tempDone = 0;
            int toUnlock = 0;
            boolean lockStatus = false;
            for (MovieData movieData : movieDataList) {
                if (null != movieData.getStatus() && movieData.getStatus().equals("done")) {
                    tempDone++;
                    doneMonitor++;
                }
                tempTotal++;
            }
            if (i > 1) {
                levelUnlockCounter += 15;
                toUnlock = levelUnlockCounter - doneMonitor;
                if (toUnlock > 0) {
                    lockStatus = true;
                }
            } else {
                lockStatus = false;
                toUnlock = 0;
            }
            dataModels.add(new ListModel(i + 1, tempTotal, tempDone, lockStatus, toUnlock));
        }

        adapter = new CustomAdapter(dataModels, getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListModel dataModel = dataModels.get(position);
                if (!dataModel.isLockStatus()) {
                    intent.putExtra("level", dataModel.getLevel());
                    startActivity(intent);
                    finish();
                } else {
                    SoundUtil.playWrongSound(StartActivity.this);
                }
            }
        });

    }

}
