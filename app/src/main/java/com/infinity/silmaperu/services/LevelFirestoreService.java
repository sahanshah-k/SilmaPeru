package com.infinity.silmaperu.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.flexbox.FlexboxLayout;
import com.infinity.silmaperu.MainActivity;
import com.infinity.silmaperu.R;
import com.infinity.silmaperu.activities.LevelActivity;
import com.infinity.silmaperu.config.GlideApp;
import com.infinity.silmaperu.domain.MovieData;

import java.io.File;
import java.util.List;

import io.realm.Realm;

public class LevelFirestoreService {

    Realm realm;
    private View rootView;
    private Context context;
    String cachePath;

    public LevelFirestoreService(Context context) {
        this.context = context;
        realm = Realm.getDefaultInstance();
        cachePath = context.getExternalCacheDir().getPath();
        rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
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
                    ((LevelActivity) context).finish();
                }
            });

            RequestOptions requestOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL);
            String path = null;
            if (status != null && status.equals("done")) {
                path = cachePath + File.separator + "SilmaPeru" + File.separator + "done" + File.separator + imageName;
            } else {
                path = cachePath + File.separator + "SilmaPeru" + File.separator + imageName;
            }


            GlideApp.with(context.getApplicationContext())
                    .asBitmap()
                    .load(path)
                    .apply(requestOptions)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
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
