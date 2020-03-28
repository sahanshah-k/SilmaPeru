package com.infinity.silmaperu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.infinity.silmaperu.MainActivity;
import com.infinity.silmaperu.R;
import com.infinity.silmaperu.domain.ListModel;
import com.infinity.silmaperu.domain.MovieData;
import com.infinity.silmaperu.services.StartService;
import com.infinity.silmaperu.services.SyncService;
import com.infinity.silmaperu.utilities.CustomAdapter;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static com.infinity.silmaperu.config.Constants.TOTAL_LEVELS;

public class StartActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<ListModel> dataModels;
    Realm realm;
    Intent intent;


    private static CustomAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        realm = Realm.getDefaultInstance();

        SyncService syncService = new SyncService(StartActivity.this);

        StartService startService = new StartService(StartActivity.this);

        listView=(ListView)findViewById(R.id.level_list);

        dataModels= new ArrayList<>();

        intent = new Intent(getApplicationContext(), LevelActivity.class);

        System.out.println("Started");

        syncService.checkAndSync();

        for(int i = 0; i< TOTAL_LEVELS ; i++) {
            List<MovieData> movieDataList =  realm.where(MovieData.class).equalTo("levelId", "level-"+(i+1)).findAll();
            int tempTotal = 0;
            int tempDone = 0;
            for (MovieData movieData : movieDataList) {
                if(null != movieData.getStatus() && movieData.getStatus().equals("done")) {
                    tempDone++;
                }
                tempTotal++;
            }
            dataModels.add(new ListModel(i +1,tempTotal,tempDone));
        }

        adapter= new CustomAdapter(dataModels,getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListModel dataModel= dataModels.get(position);
                intent.putExtra("level", dataModel.getLevel());
                startActivity(intent);

            }
        });

    }

}
