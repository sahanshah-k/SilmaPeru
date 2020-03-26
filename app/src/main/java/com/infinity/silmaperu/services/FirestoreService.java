package com.infinity.silmaperu.services;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.text.Layout;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.StorageReference;
import com.infinity.silmaperu.R;
import com.infinity.silmaperu.config.GlideApp;
import com.infinity.silmaperu.domain.MappingMap;
import com.infinity.silmaperu.domain.MovieData;
import com.infinity.silmaperu.utilities.Constants;
import com.infinity.silmaperu.utilities.ImageUtils;
import com.infinity.silmaperu.utilities.MyLeadingMarginSpan2;
import com.infinity.silmaperu.utilities.StringUtilsCustom;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;

public class FirestoreService {

    Realm realm;
    ImageView movieImage;
    String guessedName = "";
    MediaPlayer mp;
    String status = "";
    FlexboxLayout flexboxLayoutClue;
    String wikiContentString = "";
    private FirebaseFirestore db;
    private View rootView;
    private Context context;
    private MovieData movieData;
    private RealmList<MappingMap> mappingMap;


    public FirestoreService(Context context) {
        this.context = context;
        realm = Realm.getDefaultInstance();
        db = FirebaseFirestore.getInstance();
        rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        mp = MediaPlayer.create(context, R.raw.clue_click);
    }

    public MovieData copyToNewObject(MovieData movieDataParam) {
        return new MovieData(
                movieDataParam.getLevelId(),
                movieDataParam.getMovieId(),
                movieDataParam.getGuessedName(),
                movieDataParam.getMovieName(),
                movieDataParam.getShuffledName(),
                movieDataParam.getStatus(),
                movieDataParam.getWikiContent(),
                movieDataParam.getMappingMap()
        );
    }

    public void getCurrentMovieData(final String level, final String movieArg) {

        movieImage = rootView.findViewById(R.id.movieImage);

        MovieData movieDataTemp = realm.where(MovieData.class).equalTo("levelId", level).and().equalTo("movieId", movieArg).findFirst();

        movieData = copyToNewObject(movieDataTemp);

        StringUtilsCustom stringUtilsCustom = new StringUtilsCustom();

        //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
        //currentMovie = (Map<String, Object>) document.get(movieArg);

        //guessedName = (String) currentMovie.get("guessedName");
        guessedName = movieData.getGuessedName();
        //String movieName = (String) currentMovie.get("movieName");
        String movieName = movieData.getMovieName();
        //String clueMovieName = (String) currentMovie.get("shuffledName");
        String clueMovieName = movieData.getShuffledName();
        //mappingMap = (Map<String, String>) currentMovie.get("mappingMap");
        mappingMap = movieData.getMappingMap();
        //status = (String) currentMovie.get("status");
        status = movieData.getStatus();
        wikiContentString = movieData.getWikiContent();

                   /* if (mappingMap == null) {
                        mappingMap = new HashMap<>();
                    }*/

        if (guessedName == null) {
            String tempGuessedName = "";
            for (int i = 0; i < movieName.length(); i++) {
                tempGuessedName += "*";
            }
            guessedName = tempGuessedName;
        }

        //currentMovie.put("guessedName", guessedName);
        movieData.setGuessedName(guessedName);
        //String imageUrl = "gs://movie-guess-c1b59.appspot.com/" + level + "/" + movieName.toLowerCase().replace(" ", "_") + ".jpg";
        String imageName = movieName.toLowerCase().replace(" ", "_") + ".jpg";

        final ArrayList<Integer> clueHidden = new ArrayList<>();
//                    for (Map.Entry<String, String> entry : mappingMap.entrySet()) {
//                        clueHidden.add(Integer.parseInt(entry.getValue()));
//                    }

        for (MappingMap entry : mappingMap) {
            clueHidden.add(Integer.parseInt(entry.getValue()));
        }

        // mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);


        GlideApp.with(context.getApplicationContext())
                .asBitmap()
                .load(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "SilmaPeru" + File.separator + imageName)
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
                    final String buttonIdString = String.valueOf(button.getId());
                    final MappingMap mappingMapButtonId = mappingMap.where().equalTo("key", buttonIdString).findFirst();
                    if (mappingMapButtonId != null && (null == status || !status.equals("done"))) {

                        Button clueButton = rootView.findViewById(Integer.parseInt(mappingMapButtonId.getValue()));
                        clueButton.setVisibility(View.VISIBLE);
                        clueHidden.remove(new Integer(mappingMapButtonId.getValue()));
                        button.setText("");
                        guessedName = guessedName.substring(0, button.getId() - 1000)
                                + "*"
                                + guessedName.substring(button.getId() - 1000 + 1);
                        //currentMovie.put("guessedName", guessedName);
                        movieData.setGuessedName(guessedName);
                        //mappingMap.remove(buttonIdString);
             /*                       realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            RealmResults<MappingMap> result = realm.where(MappingMap.class).equalTo("key",buttonIdString).findAll();
                                            result.deleteAllFromRealm();
                                        }
                                    });*/
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                mappingMap.remove(mappingMapButtonId);
                            }
                        });
                        //currentMovie.put("mappingMap", mappingMap);
                        movieData.setMappingMap(mappingMap);
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
            //currentMovie.put("shuffledName", clueMovieName);
            movieData.setShuffledName(clueMovieName);
        }


        for (int i = 0; i < clueMovieName.length(); i++) {
            String charClue = String.valueOf(clueMovieName.charAt(i));
            final Button button = new Button(context);
            button.setId(2000 + i);
            button.setLayoutParams(linearLayoutClue);
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
            button.setTypeface(Typeface.createFromAsset(context.getAssets(), "guess_font_primetime.ttf"));
            button.setGravity(Gravity.CENTER);
            flexboxLayoutClue = rootView.findViewById(R.id.name_scrambled_layout);

            button.setText(charClue);
            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    buttonAction(button);
                }
            });
            if (null == status || !status.equals("done")) {
                flexboxLayoutClue.addView(button);

            }
            if (clueHidden.contains(i + 2000)) {
                button.setVisibility(View.INVISIBLE);
            }
        }

        if (null != status && status.equals("done")) {

            SpannableString ss;

            if (wikiContentString.length() > 465) {
                ss = new SpannableString(wikiContentString.substring(0, 464) + "...");

            } else {
                ss = new SpannableString(wikiContentString);
            }
            ss.setSpan(new MyLeadingMarginSpan2(3, 160), 0, ss.length(), 0);

            TextView wikiContentTextView = rootView.findViewById(R.id.wikiContent);
            RelativeLayout wikiRelativeLayout = rootView.findViewById(R.id.wikiLayout);
            wikiRelativeLayout.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                wikiContentTextView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
            }

            wikiContentTextView.setText(ss);

            RelativeLayout winMessageLayout = rootView.findViewById(R.id.winMessageLayout);
            winMessageLayout.setVisibility(View.VISIBLE);

            TextView winMessage = rootView.findViewById(R.id.winMessage);
            winMessage.setTypeface(Typeface.createFromAsset(context.getAssets(), "win_font.ttf"));
            winMessage.setText(Constants.getRandomWinMessage());
            winMessage.setTextColor(context.getResources().getColor(Constants.getRandomColor()));


        }

        for (int i = 0; i < movieName.length(); i++) {
            Button answerButton = rootView.findViewById(1000 + i);
            if (guessedName.charAt(i) != '*') {
                answerButton.setText(String.valueOf(guessedName.charAt(i)));
            }
        }


    }

    public void updateMap() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(movieData);
            }
        });

       /* final DocumentReference updateDocRef = db.collection("user1").document(level);
        if (currentMovie != null) {
            updateDocRef.update(movieArg, currentMovie);
        }*/
    }


    public void buttonAction(final Button clueButton) {

        //String movieName = (String) currentMovie.get("movieName");
        String movieName = movieData.getMovieName();

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
                    //currentMovie.put("guessedName", guessedName);
                    movieData.setGuessedName(guessedName);
                    //mappingMap.put(String.valueOf(1000 + i), String.valueOf(clueButton.getId()));
                    final MappingMap mappingMapGuessed = new MappingMap(String.valueOf(1000 + i), String.valueOf(clueButton.getId()));
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mappingMap.add(mappingMapGuessed);
                        }
                    });
                    //currentMovie.put("mappingMap", mappingMap);
                    movieData.setMappingMap(mappingMap);
                    movieData.setMappingMap(mappingMap);
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
                //currentMovie.put("status", "done");
                movieData.setStatus("done");
                flexboxLayoutClue.setVisibility(View.INVISIBLE);
                SpannableString ss;

                if (wikiContentString.length() > 465) {
                    ss = new SpannableString(wikiContentString.substring(0, 464) + "...");

                } else {
                    ss = new SpannableString(wikiContentString);
                }
                ss.setSpan(new MyLeadingMarginSpan2(3, 160), 0, ss.length(), 0);

                TextView wikiContentTextView = rootView.findViewById(R.id.wikiContent);
                RelativeLayout wikiRelativeLayout = rootView.findViewById(R.id.wikiLayout);
                wikiRelativeLayout.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    wikiContentTextView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                }

                wikiContentTextView.setText(ss);

                RelativeLayout winMessageLayout = rootView.findViewById(R.id.winMessageLayout);
                winMessageLayout.setVisibility(View.VISIBLE);

                TextView winMessage = rootView.findViewById(R.id.winMessage);
                winMessage.setText(Constants.getRandomWinMessage());
                winMessage.setTypeface(Typeface.createFromAsset(context.getAssets(), "win_font.ttf"));

                winMessage.setTextColor(Constants.getRandomColor());
                successSound.start();

            } else if (pureGuessedName.length() == pureMovieName.length()) {
                MediaPlayer wrongSound = MediaPlayer.create(context, R.raw.wrong);
                Toast.makeText(context, "Wrong Answer!", Toast.LENGTH_LONG).show();
                wrongSound.start();
            }

        }
    }

}
