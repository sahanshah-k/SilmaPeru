package com.infinity.silmaperu.utilities;

import java.util.ArrayList;
import java.util.Random;
import com.infinity.silmaperu.R;


public class Constants {



    public static String getRandomWinMessage() {

        Random randomGenerator = new Random();

        ArrayList<String> winMessages = new ArrayList<>();
        winMessages.add("WELL DONE!");
        winMessages.add("TASTY!");
        winMessages.add("DIVINE!");
        winMessages.add("AWESOME!");
        winMessages.add("SUPER!");

        int index = randomGenerator.nextInt(winMessages.size());

        return winMessages.get(index);
    }


    public static int getRandomColor() {
        int[] colorArray = new int[]{R.color.colorGreen, R.color.colorBlue, R.color.colorRed,
        R.color.colorViolet,R.color.colorLightGreen};

        Random randomGenerator = new Random();

        int index = randomGenerator.nextInt(colorArray.length);

        return colorArray[index];
    }
}
