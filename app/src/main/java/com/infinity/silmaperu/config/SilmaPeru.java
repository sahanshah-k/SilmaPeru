package com.infinity.silmaperu.config;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SilmaPeru extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());


        RealmConfiguration config =
                new RealmConfiguration.Builder()
                        .name("movie_guess.db")
                        .schemaVersion(1)
                        .deleteRealmIfMigrationNeeded()
                        .build();

        Realm.setDefaultConfiguration(config);
    }
}
