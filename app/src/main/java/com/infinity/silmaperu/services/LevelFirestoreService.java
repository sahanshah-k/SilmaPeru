package com.infinity.silmaperu.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.infinity.silmaperu.MainActivity;
import com.infinity.silmaperu.R;
import com.infinity.silmaperu.config.GlideApp;
import com.infinity.silmaperu.utilities.ImageTransformation;
import com.infinity.silmaperu.utilities.ImageUtils;

import java.util.HashMap;
import java.util.Map;

public class LevelFirestoreService {

    private FirebaseFirestore db;
    private View rootView;
    private Context context;
    private StorageReference mStorageRef;


    public LevelFirestoreService(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();

        rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    public void getLevelData(final String level) {
        DocumentReference docRef = db.collection("user1").document(level);
        final FlexboxLayout flexboxLayout = rootView.findViewById(R.id.movie_tile);
        final Intent intent = new Intent(context, MainActivity.class);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot document, @Nullable FirebaseFirestoreException e) {
                if (document != null && document.exists()) {
                    flexboxLayout.removeAllViews();
                    for (final Map.Entry<String, Object> entry : document.getData().entrySet()) {
                        HashMap<String, Object> entryValue = (HashMap<String, Object>) entry.getValue();
                        String movieName = (String) entryValue.get("movieName");
                        String imageUrl = "gs://movie-guess-c1b59.appspot.com/" + level + "/" + movieName.toLowerCase().replace(" ", "_") + ".jpg";
                        final String status = (String) entryValue.get("status");
                        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

                        final ImageView movieTile = new ImageView(context);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(240, 240);
                        lp.setMargins(15, 15, 15, 15);
                        movieTile.setLayoutParams(lp);

                        movieTile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                intent.putExtra("level", level);
                                intent.putExtra("movieId", entry.getKey());
                                context.startActivity(intent);
                            }
                        });

                        /*GlideApp.with(context.getApplicationContext())
                                .asBitmap()
                                .load(mStorageRef)
                                .thumbnail()
                                .into(ImageUtils.getRoundedImageTarget(context, movieTile, (float) 25.00));*/

                        GlideApp.with(context.getApplicationContext())
                                .asBitmap()
                                .load(mStorageRef)
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
        });
    }
}
