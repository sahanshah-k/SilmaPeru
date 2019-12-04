package com.infinity.silmaperu.utilities;

import java.util.ArrayList;
import java.util.List;

public class StringUtilsCustom {

    public String shuffle(String input) {
        List<Character> characters = new ArrayList<Character>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(input.length());
        while (characters.size() != 0) {
            int randPicker = (int) (Math.random() * characters.size());
            output.append(characters.remove(randPicker));
        }
        return output.toString();
    }

    public String findRemaining(String word) {
        String allLetters = "gmnrbpvhfdlyakjesictougmnrbpvhfdlyakjesictougmnrbpvhfdlyakjesictougmnrbpvhfdlyakjesictou";
        int remainingLength = 0;

        int wordLength = word.length();

        if (wordLength < 8) {
            remainingLength = 16 - wordLength;
        } else {
            remainingLength = 8 - ((wordLength) % 8);
        }

        for (char c : word.toCharArray()) {
            String temp = String.valueOf(c);
            if (allLetters.contains(temp)) {
                allLetters = allLetters.replaceAll(temp, "");
            }
        }
        allLetters = allLetters.substring(0, remainingLength);

        allLetters = shuffle(allLetters.toUpperCase());


        return shuffle(allLetters.concat(word.toUpperCase()));
    }

}
