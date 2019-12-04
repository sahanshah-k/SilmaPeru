package com.infinity.silmaperu.services;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.infinity.silmaperu.R;
import com.infinity.silmaperu.config.GlideApp;
import com.infinity.silmaperu.utilities.ImageUtils;
import com.infinity.silmaperu.utilities.StringUtilsCustom;

import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static android.content.ContentValues.TAG;

public class FirestoreService {

    ImageView movieImage;
    String guessedName = "";
    MediaPlayer mp;
    PhotoViewAttacher zoomAttacher;
    private FirebaseFirestore db;
    private View rootView;
    private Context context;
    private Map<String, Object> currentMovie;
    private StorageReference mStorageRef;
    private Map<String, String> mappingMap;


    public FirestoreService(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        mappingMap = new HashMap<>();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        mp = MediaPlayer.create(context, R.raw.clue_click);

    }

    public void getCurrentMovieData(final String level, final String movieArg) {

        movieImage = rootView.findViewById(R.id.movieImage);

        final DocumentReference docRef = db.collection("user1").document(level);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot document, @Nullable FirebaseFirestoreException e) {
                if (document != null && document.exists()) {


                    StringUtilsCustom stringUtilsCustom = new StringUtilsCustom();

                    //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    currentMovie = (Map<String, Object>) document.get(movieArg);

                    guessedName = (String) currentMovie.get("guessedName");
                    String movieName = (String) currentMovie.get("movieName");
                    String clueMovieName = (String) currentMovie.get("shuffledName");
                    mappingMap = (Map<String, String>) currentMovie.get("mappingMap");

                    if (mappingMap == null) {
                        mappingMap = new HashMap<>();
                    }

                    if (guessedName == null) {
                        String tempGuessedName = "";
                        for (int i = 0; i < movieName.length(); i++) {
                            tempGuessedName += "*";
                        }
                        guessedName = tempGuessedName;
                    }

                    currentMovie.put("guessedName", guessedName);
                    String imageUrl = "gs://movie-guess-c1b59.appspot.com/" + level + "/" + movieName.toLowerCase().replace(" ", "_") + ".jpg";

                    final ArrayList<Integer> clueHidden = new ArrayList<>();
                    for (Map.Entry<String, String> entry : mappingMap.entrySet()) {
                        clueHidden.add(Integer.parseInt(entry.getValue()));
                    }

                    mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);


                    GlideApp.with(context.getApplicationContext())
                            .asBitmap()
                            .load(mStorageRef)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ImageUtils.getRoundedImageTarget(context, movieImage, (float) 25.00));

                    LinearLayout.LayoutParams linearLayoutAnswer = new LinearLayout.LayoutParams(100, 100);
                    linearLayoutAnswer.setMargins(5, 5, 5, 5);

                    LinearLayout.LayoutParams linearLayoutClue = new LinearLayout.LayoutParams(115, 115);

                    LinearLayout buttonGroup = new LinearLayout(context);
                    LinearLayout.LayoutParams buttonGroupLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    buttonGroupLayoutParams.setMargins(20, 0, 20, 0);

                    buttonGroup.setGravity(Gravity.CENTER);
                    buttonGroup.setLayoutParams(buttonGroupLayoutParams);
                    linearLayoutClue.setMargins(0, 0, 0, 0);
                    for (int i = 0; i < movieName.length(); i++) {
                        FlexboxLayout flexboxLayout = rootView.findViewById(R.id.answer_layout);
                        final Button button = new Button(context);
                        button.setLayoutParams(linearLayoutAnswer);
                        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
                        button.setTypeface(Typeface.createFromAsset(context.getAssets(), "guess_font_primetime.ttf"));
                        button.setGravity(Gravity.CENTER);
                        button.setBackgroundResource(R.drawable.answer_button);
                        button.setTextColor(context.getResources().getColor(R.color.colorWhite));
                        //button.setBackground(R.drawable.answer_button);
                        button.setId(i + 1000);
                        //button.setBackgroundColor(R.color.colorWhite);

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String buttonIdString = String.valueOf(button.getId());
                                if (mappingMap.get(buttonIdString) != null) {

                                    Button clueButton = rootView.findViewById(Integer.parseInt(mappingMap.get(buttonIdString)));
                                    clueButton.setVisibility(View.VISIBLE);
                                    clueHidden.remove(new Integer(mappingMap.get(buttonIdString)));
                                    button.setText("");
                                    guessedName = guessedName.substring(0, button.getId() - 1000)
                                            + "*"
                                            + guessedName.substring(button.getId() - 1000 + 1);
                                    currentMovie.put("guessedName", guessedName);
                                    mappingMap.remove(buttonIdString);
                                    currentMovie.put("mappingMap", mappingMap);
                                }

                            }
                        });
                        //button.setText(String.valueOf(name.charAt(i)));
                        if (movieName.charAt(i) != ' ') {
                            buttonGroup.addView(button);
                        }
                        Log.i("TAG", "The index is" + button.getText());
                        if (movieName.charAt(i) == ' ' || i == (movieName.length() - 1)) {
                            flexboxLayout.addView(buttonGroup);
                            buttonGroup = new LinearLayout(context);
                            buttonGroup.setGravity(Gravity.CENTER);
                            buttonGroup.setLayoutParams(buttonGroupLayoutParams);
                            continue;
                        }


                    }
                    if (clueMovieName == null) {
                        clueMovieName = stringUtilsCustom.findRemaining(movieName.replaceAll(" ", ""));
                        currentMovie.put("shuffledName", clueMovieName);
                    }


                    for (int i = 0; i < clueMovieName.length(); i++) {
                        String charClue = String.valueOf(clueMovieName.charAt(i));
                        final Button button = new Button(context);
                        button.setId(2000 + i);
                        button.setLayoutParams(linearLayoutClue);
                        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
                        button.setTypeface(Typeface.createFromAsset(context.getAssets(), "guess_font_primetime.ttf"));
                        button.setGravity(Gravity.CENTER);
                        FlexboxLayout flexboxLayout = rootView.findViewById(R.id.name_scrambled_layout);

                        button.setText(charClue);
                        final int finalI = i;
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                buttonAction(button);
                            }
                        });
                        flexboxLayout.addView(button);

                        if (clueHidden.contains(i + 2000)) {
                            button.setVisibility(View.INVISIBLE);
                        }
                    }

                    for (int i = 0; i < movieName.length(); i++) {
                        Button answerButton = rootView.findViewById(1000 + i);
                        if (guessedName.charAt(i) != '*') {
                            answerButton.setText(String.valueOf(guessedName.charAt(i)));
                        }
                    }
                } else {
                    Log.d(TAG, "No such document");
                }


            }
        });

    }

    public void updateMap(String level, String movieArg) {
        final DocumentReference updateDocRef = db.collection("user1").document(level);
        if (currentMovie != null) {
            updateDocRef.update(movieArg, currentMovie);
        }
    }


    public void buttonAction(final Button clueButton) {

        String movieName = (String) currentMovie.get("movieName");

        String pureGuessedName = guessedName.replace("*", "");
        String pureMovieName = movieName.replace(" ", "").toUpperCase();

        int checkLength = pureGuessedName.length() - pureMovieName.length();

        if (checkLength != 0) {
            mp.start();
            clueButton.setVisibility(View.INVISIBLE);
            for (int i = 0; i < movieName.length(); i++) {
                if (movieName.charAt(i) == ' ') {
                    i++;
                }
                if (guessedName.charAt(i) == '*') {
                    guessedName = guessedName.substring(0, i)
                            + clueButton.getText().toString()
                            + guessedName.substring(i + 1);
                    currentMovie.put("guessedName", guessedName);
                    mappingMap.put(String.valueOf(1000 + i), String.valueOf(clueButton.getId()));
                    currentMovie.put("mappingMap", mappingMap);
                    break;
                }
            }
            for (int i = 0; i < movieName.length(); i++) {
                Button answerButton = rootView.findViewById(1000 + i);
                if (guessedName.charAt(i) != '*') {
                    answerButton.setText(String.valueOf(guessedName.charAt(i)));
                }
            }

            pureGuessedName = guessedName.replace("*", "");

            if (pureGuessedName.equals(pureMovieName)) {
                MediaPlayer successSound = MediaPlayer.create(context, R.raw.success);
                Toast.makeText(context, "Awesome", Toast.LENGTH_LONG).show();
                currentMovie.put("status", "done");
                successSound.start();
               
            } else if (pureGuessedName.length() == pureMovieName.length()) {
                MediaPlayer wrongSound = MediaPlayer.create(context, R.raw.wrong);
                Toast.makeText(context, "Wrong Answer!", Toast.LENGTH_LONG).show();
                wrongSound.start();
            }

        }
    }

    public void wikiCall(String title) throws IOException {
        title = "Kali (2016 film)";
        String urlString = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro&explaintext&redirects=1&titles=" + title;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responsecode = conn.getResponseCode();

        if (responsecode != 200)
            throw new RuntimeException("HttpResponseCode:" + responsecode);
        else {
            String inline = "";
            Scanner sc = new Scanner(url.openStream());
            while(sc.hasNext())
            {
                inline+=sc.nextLine();
            }
            System.out.println(inline);
            sc.close();
            JSONParser parse = new JSONParser();
            Toast.makeText(context, inline, Toast.LENGTH_LONG).show();

        }


    }


}
