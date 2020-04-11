package com.infinity.silmaperu.utilities;

import android.content.Context;
import android.media.MediaPlayer;

import com.infinity.silmaperu.R;

public class SoundUtil {

    public static  void playWrongSound(Context context) {
        MediaPlayer wrongSound = MediaPlayer.create(context, R.raw.wrong);
        wrongSound.start();
    }
}
