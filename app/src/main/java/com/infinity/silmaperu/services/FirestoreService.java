package com.infinity.silmaperu.services;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.FlexboxLayout;
import com.infinity.silmaperu.MainActivity;
import com.infinity.silmaperu.R;
import com.infinity.silmaperu.config.GlideApp;
import com.infinity.silmaperu.domain.MappingMap;
import com.infinity.silmaperu.domain.MovieData;
import com.infinity.silmaperu.utilities.Constants;
import com.infinity.silmaperu.utilities.ImageUtils;
import com.infinity.silmaperu.utilities.MyLeadingMarginSpan2;
import com.infinity.silmaperu.utilities.SoundUtil;
import com.infinity.silmaperu.utilities.StringUtilsCustom;

import java.io.File;
import java.io.FileOutputStream;
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
    private View rootView;
    private Context context;
    private MovieData movieData;
    private RealmList<MappingMap> mappingMap;
    private int tempAnsButton;
    private TextView dashText;
    private String imageName;
    private int width;
    private int height;
    String cachePath;
    private int layoutClueSide;
    private int layoutAnswerSide;
    private int answerSide;
    private int layoutMargin;
    private int buttonGroupMargin;
    private int spannableMargin;
    private int spannableLength;
    private int fontSize;
    private int scrollViewHeight;
    private int winMessageFontSize;
    private int winMessageHeight;
    private int wikiImageSize;

    public FirestoreService(Context context) {
        this.context = context;
        realm = Realm.getDefaultInstance();
        rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        mp = MediaPlayer.create(context, R.raw.clue_click);
        cachePath = context.getExternalCacheDir().getPath();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;//2260
        answerSide = (int)Math.round(height / 2.825);//800
        layoutAnswerSide = (int)Math.round(height / 22.6);//100
        buttonGroupMargin = (int)Math.round(height / 113);//20
        fontSize = (int)Math.round(height / 86.92);//26
        layoutMargin = (int)Math.round(height / 452);//5
        layoutClueSide = (int)Math.round(height / 19.7);//115
        spannableLength = (int)Math.round(height / 4.86);//465
        spannableMargin = (int)Math.round(height / 14.125);//160
        scrollViewHeight = (int)Math.round(height / 3.4769);//650
        winMessageFontSize = (int)Math.round(height / 53.80);//42
        winMessageHeight = (int)Math.round(height / 9.04);//250
        wikiImageSize = (int)Math.round(height / 15.067);//150


        int buttonCountMinus = width - (buttonGroupMargin * 2) - 16;
        int buttonWidth = layoutAnswerSide + (layoutMargin * 2);
        if (width > 0) {
            width = buttonCountMinus/buttonWidth;//9
        } else {
            width = 9;
        }
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
        movieImage.getLayoutParams().height = answerSide;
        movieImage.getLayoutParams().width = answerSide;

        MovieData movieDataTemp = realm.where(MovieData.class).equalTo("levelId", level).and().equalTo("movieId", movieArg).findFirst();

        movieData = copyToNewObject(movieDataTemp);

        StringUtilsCustom stringUtilsCustom = new StringUtilsCustom();
        guessedName = movieData.getGuessedName();
        String movieName = movieData.getMovieName();
        String clueMovieName = movieData.getShuffledName();
        mappingMap = movieData.getMappingMap();
        status = movieData.getStatus();
        wikiContentString = movieData.getWikiContent();

        if (guessedName == null) {
            String tempGuessedName = "";
            for (int i = 0; i < movieName.length(); i++) {
                tempGuessedName += "*";
            }
            guessedName = tempGuessedName;
        }

        movieData.setGuessedName(guessedName);
        imageName = movieName.toLowerCase().replace(" ", "_") + ".jpg";

        final ArrayList<Integer> clueHidden = new ArrayList<>();

        for (MappingMap entry : mappingMap) {
            clueHidden.add(Integer.parseInt(entry.getValue()));
        }
        RequestOptions requestOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL);

        GlideApp.with(context.getApplicationContext())
                .asBitmap()
                .apply(requestOptions)
                .load(cachePath + File.separator + "SilmaPeru" + File.separator + imageName)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ImageUtils.getRoundedImageTarget(context, movieImage, (float) 25.00));

        LinearLayout.LayoutParams linearLayoutAnswer = new LinearLayout.LayoutParams(layoutAnswerSide, layoutAnswerSide);
        linearLayoutAnswer.setMargins(layoutMargin, layoutMargin, layoutMargin, layoutMargin);

        LinearLayout.LayoutParams linearLayoutClue = new LinearLayout.LayoutParams(layoutClueSide, layoutClueSide);

        LinearLayout buttonGroup = new LinearLayout(context);
        LinearLayout.LayoutParams buttonGroupLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonGroupLayoutParams.setMargins(buttonGroupMargin, 0, buttonGroupMargin, 0);

        buttonGroup.setGravity(Gravity.CENTER);
        buttonGroup.setLayoutParams(buttonGroupLayoutParams);
        linearLayoutClue.setMargins(layoutMargin, layoutMargin, layoutMargin, layoutMargin);
        for (int i = 0; i < movieName.length(); i++) {
            FlexboxLayout flexboxLayout = rootView.findViewById(R.id.answer_layout);
            final Button button = new Button(context);
            button.setLayoutParams(linearLayoutAnswer);
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);
            button.setPadding(0, 0, 0, 0);
            button.setTypeface(Typeface.createFromAsset(context.getAssets(), "seguibl.ttf"));
            button.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                button.setBackground(context.getDrawable(R.drawable.button_answer));
            }
            button.setTextColor(context.getResources().getColor(R.color.colorWhite));
            button.setId(i + 1000);

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
                        movieData.setGuessedName(guessedName);
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                mappingMap.remove(mappingMapButtonId);
                            }
                        });
                        movieData.setMappingMap(mappingMap);
                    }

                }
            });

            dashText = new TextView(context);
            dashText.setBackgroundResource(R.drawable.return_button);

            if (movieName.charAt(i) != ' ') {
                tempAnsButton++;
                buttonGroup.addView(button);

                if (tempAnsButton == (width-1) && movieName.length() > ((i + 1) + 1) && checkNotWhiteSpace(movieName, (i + 1) + 1) && checkNotWhiteSpace(movieName, i + 1)) {
                    buttonGroup.addView(dashText);
                }

/*                if (tempAnsButton == 8 && movieName.length() > ((i + 1) + 1) && movieName.charAt(((i + 1) + 2)) != ' ' && movieName.charAt(((i + 1) + 1)) != ' ' && movieName.charAt(((i + 1))) != ' ') {
                    buttonGroup.addView(dashText);
                }*/
            }

            Log.i("TAG", "The index is" + button.getText());
            if (movieName.charAt(i) == ' ' || i == (movieName.length() - 1) || buttonGroup.getChildCount() == width) {
                tempAnsButton = 0;
                flexboxLayout.addView(buttonGroup);
                buttonGroup = new LinearLayout(context);
                buttonGroup.setGravity(Gravity.CENTER);
                buttonGroup.setLayoutParams(buttonGroupLayoutParams);
                continue;
            }


        }
        if (clueMovieName == null) {
            clueMovieName = stringUtilsCustom.findRemaining(movieName.replaceAll(" ", ""));
            movieData.setShuffledName(clueMovieName);
        }


        for (int i = 0; i < clueMovieName.length(); i++) {
            String charClue = String.valueOf(clueMovieName.charAt(i));
            final Button button = new Button(context);
            button.setId(2000 + i);
            button.setLayoutParams(linearLayoutClue);
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);
            button.setPadding(0, 0, 0, 0);
            button.setTypeface(Typeface.createFromAsset(context.getAssets(), "seguibl.ttf"));
            button.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                button.setBackground(context.getDrawable(R.drawable.button_clue));
            }
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

            if (wikiContentString.length() > spannableLength) {
                ss = new SpannableString(wikiContentString.substring(0, spannableLength - 1) + "...");

            } else {
                ss = new SpannableString(wikiContentString);
            }
            ss.setSpan(new MyLeadingMarginSpan2(3, spannableMargin), 0, ss.length(), 0);

            TextView wikiContentTextView = rootView.findViewById(R.id.wikiContent);

            ScrollView scrollView = rootView.findViewById(R.id.scrollViewWiki);
            scrollView.getLayoutParams().height = scrollViewHeight;
            scrollView.setVisibility(View.VISIBLE);

            RelativeLayout wikiRelativeLayout = rootView.findViewById(R.id.wikiLayout);
            wikiRelativeLayout.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                wikiContentTextView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
            }

            wikiContentTextView.setText(ss);
            ImageView wikiGlobe = rootView.findViewById(R.id.wiki_image_image_view);
            wikiGlobe.getLayoutParams().height = wikiImageSize;
            wikiGlobe.getLayoutParams().width = wikiImageSize;


            RelativeLayout winMessageLayout = rootView.findViewById(R.id.winMessageLayout);
            winMessageLayout.getLayoutParams().height = winMessageHeight;
            winMessageLayout.setVisibility(View.VISIBLE);

            TextView winMessage = rootView.findViewById(R.id.winMessage);
            winMessage.setTypeface(Typeface.createFromAsset(context.getAssets(), "win_font.ttf"));
            winMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP, winMessageFontSize);
            winMessage.setText(Constants.getRandomWinMessage());
            winMessage.setTextColor(context.getResources().getColor(Constants.getRandomColor()));


        }

        for (int i = 0; i < movieName.length(); i++) {
            Button answerButton = rootView.findViewById(1000 + i);
            if (null != answerButton) {
                if (guessedName.charAt(i) != '*') {
                    answerButton.setText(String.valueOf(guessedName.charAt(i)));
                }
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

    }


    public void buttonAction(final Button clueButton) {

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
                    movieData.setGuessedName(guessedName);
                    final MappingMap mappingMapGuessed = new MappingMap(String.valueOf(1000 + i), String.valueOf(clueButton.getId()));
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mappingMap.add(mappingMapGuessed);
                        }
                    });
                    movieData.setMappingMap(mappingMap);
                    movieData.setMappingMap(mappingMap);
                    break;
                }
            }
            for (int i = 0; i < movieName.length(); i++) {
                Button answerButton = rootView.findViewById(1000 + i);
                if (null != answerButton) {
                    if (guessedName.charAt(i) != '*') {
                        answerButton.setText(String.valueOf(guessedName.charAt(i)));
                    }
                }
            }

            pureGuessedName = guessedName.replace("*", "");

            if (pureGuessedName.equals(pureMovieName)) {
                MediaPlayer successSound = MediaPlayer.create(context, R.raw.success);
                Toast.makeText(context, "Awesome", Toast.LENGTH_LONG).show();
                movieData.setStatus("done");
                flexboxLayoutClue.setVisibility(View.INVISIBLE);
                SpannableString ss;

                if (wikiContentString.length() > spannableLength) {
                    ss = new SpannableString(wikiContentString.substring(0, spannableLength-1) + "...");

                } else {
                    ss = new SpannableString(wikiContentString);
                }
                ss.setSpan(new MyLeadingMarginSpan2(3, spannableMargin), 0, ss.length(), 0);

                TextView wikiContentTextView = rootView.findViewById(R.id.wikiContent);
                RelativeLayout wikiRelativeLayout = rootView.findViewById(R.id.wikiLayout);

                wikiRelativeLayout.getLayoutParams().height = 100;

                ScrollView scrollView = rootView.findViewById(R.id.scrollViewWiki);
                scrollView.getLayoutParams().height = scrollViewHeight;
                scrollView.setVisibility(View.VISIBLE);

                wikiRelativeLayout.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    wikiContentTextView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                }

                wikiContentTextView.setText(ss);
                ImageView wikiGlobe = rootView.findViewById(R.id.wiki_image_image_view);
                wikiGlobe.getLayoutParams().height = wikiImageSize;
                wikiGlobe.getLayoutParams().width = wikiImageSize;

                RelativeLayout winMessageLayout = rootView.findViewById(R.id.winMessageLayout);
                winMessageLayout.getLayoutParams().height = winMessageHeight;

                winMessageLayout.setVisibility(View.VISIBLE);

                TextView winMessage = rootView.findViewById(R.id.winMessage);
                winMessage.setText(Constants.getRandomWinMessage());
                winMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP, winMessageFontSize);
                winMessage.setTypeface(Typeface.createFromAsset(context.getAssets(), "win_font.ttf"));

                winMessage.setTextColor(Constants.getRandomColor());
                successSound.start();

                Bitmap myBitmap = BitmapFactory.decodeFile(cachePath + File.separator + "SilmaPeru" + File.separator + imageName);

                myBitmap = ImageUtils.applyOverlay(context, myBitmap, R.drawable.done_bit);

                File directory = new File(cachePath, "SilmaPeru" + File.separator + "done");
                File file = new File(directory, imageName);
                boolean createDir = true;
                if (!directory.exists()) {
                    createDir = directory.mkdirs();
                }
                if (!file.exists() && createDir) {
                    Log.d("path", file.toString());
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }

                ((MainActivity) context).finish();
                context.startActivity(((MainActivity) context).getIntent());

            } else if (pureGuessedName.length() == pureMovieName.length()) {
                Toast.makeText(context, "Wrong Answer!", Toast.LENGTH_LONG).show();
                SoundUtil.playWrongSound(context);
            }

        }
    }


    public boolean checkNotWhiteSpace(String movieName, int pos) {
        try {
            if (movieName.charAt(pos) != ' ') {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

}
