package com.infinity.silmaperu.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.infinity.silmaperu.MainActivity;
import com.infinity.silmaperu.R;
import com.infinity.silmaperu.config.GlideApp;
import com.infinity.silmaperu.domain.MovieData;
import com.infinity.silmaperu.utilities.ImageUtils;

import java.io.File;
import java.util.List;

import io.realm.Realm;

public class LevelFirestoreService {

    Realm realm;
    private FirebaseFirestore db;
    private View rootView;
    private Context context;

    public LevelFirestoreService(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        realm = Realm.getDefaultInstance();

        rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    public void getLevelData(final String level) {
        final FlexboxLayout flexboxLayout = rootView.findViewById(R.id.movie_tile);
        final Intent intent = new Intent(context, MainActivity.class);
        final List<MovieData> movieDataList = realm.where(MovieData.class).equalTo("levelId", level).findAll();
        flexboxLayout.removeAllViews();
        for (final MovieData movieData : movieDataList) {
            String movieName = movieData.getMovieName();
            String imageName = movieName.toLowerCase().replace(" ", "_") + ".jpg";
            final String status = movieData.getStatus();
            final ImageView movieTile = new ImageView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(240, 240);
            lp.setMargins(15, 15, 15, 15);
            movieTile.setLayoutParams(lp);

            movieTile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent.putExtra("level", level);
                    intent.putExtra("movieId", movieData.getMovieId());
                    context.startActivity(intent);
                }
            });

            GlideApp.with(context.getApplicationContext())
                    .asBitmap()
                    .load(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "SilmaPeru" + File.separator + imageName)
                    .thumbnail()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            if (status != null && status.equals("done")) {
                                resource = ImageUtils.applyOverlay(context, resource, R.drawable.done);
                            }
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            circularBitmapDrawable.setCornerRadius((float) 50.00);

                            movieTile.setImageDrawable(circularBitmapDrawable);
                        }
                    });

            flexboxLayout.addView(movieTile);
        }

    }
}
