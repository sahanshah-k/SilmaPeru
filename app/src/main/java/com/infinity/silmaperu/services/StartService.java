package com.infinity.silmaperu.services;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import io.realm.Realm;

public class StartService {

    Realm realm;
    private View rootView;
    private Context context;


    public StartService(Context context) {
        this.context = context;
        realm = Realm.getDefaultInstance();
        rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
    }



}
