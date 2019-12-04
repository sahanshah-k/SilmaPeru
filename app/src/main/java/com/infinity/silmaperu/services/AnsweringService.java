package com.infinity.silmaperu.services;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;

public class AnsweringService {

    private Context context;
    View rootView;

    public AnsweringService(Context context) {
        this.context = context;
        rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
    }

    public void passChars(Button clueButton, String movieName){

        Button answerButton = rootView.findViewById(1001);
        clueButton.setVisibility(View.INVISIBLE);
    }

}
