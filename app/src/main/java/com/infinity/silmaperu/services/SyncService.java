package com.infinity.silmaperu.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.infinity.silmaperu.R;
import com.infinity.silmaperu.activities.LaunchAppActivity;
import com.infinity.silmaperu.activities.LoadingActivity;
import com.infinity.silmaperu.domain.MovieData;
import com.infinity.silmaperu.domain.UpdateMetadata;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

import static com.firebase.ui.auth.ui.phone.SubmitConfirmationCodeFragment.TAG;
import static com.infinity.silmaperu.config.Constants.TOTAL_LEVELS;

public class SyncService {

    final private String MOVIE_NAME = "movieName";
    final private String WIKI_CONTENT = "wikiContent";
    private final Intent intent;
    Realm realm;
    private FirebaseFirestore db;
    private FirebaseStorage storageInstance;
    private Context context;
    private StorageReference storageReferenceDownload;
    private int downloadCount;
    private int monitorDownload;
    private View rootView;
    private TextView loadingPercentage;

    public SyncService(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        storageInstance = FirebaseStorage.getInstance();
        rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        storageReferenceDownload = storageInstance.getReferenceFromUrl("gs://movie-guess-c1b59.appspot.com");
        realm = Realm.getDefaultInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        intent = new Intent(context, LaunchAppActivity.class);
        loadingPercentage = rootView.findViewById(R.id.loading_percentage);
    }


    public void checkAndSync() {

        final DocumentReference docRef = db.collection("user1").document("updateMetadata");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        final String key = (String) document.getData().get("key");
                        final UpdateMetadata updateMetadataOffline = realm.where(UpdateMetadata.class).findFirst();
                        if (null == updateMetadataOffline || !updateMetadataOffline.getKey().equals(key)) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(new UpdateMetadata("uniqueKey", key));
                                    getCountAndThenSync();
                                }
                            });
                        } else {
                            startNextActivity();
                        }
                    } else {
                        startNextActivity();
                    }
                } else {
                    startNextActivity();
                }
            }
        });
    }

    public void startNextActivity() {
        //context.unregisterReceiver(onDownloadComplete);
        context.startActivity(intent);
        ((LoadingActivity) context).finish();
    }

    public void getCountAndThenSync() {
        for (int i = 1; i <= TOTAL_LEVELS; i++) {

            final String level = "level-" + i;
            final DocumentReference docRef = db.collection("user1").document(level);

            final int finalI = i;
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> hashMap = document.getData();
                            downloadCount += hashMap != null ? hashMap.size() : 0;
                            if (finalI == TOTAL_LEVELS) {
                                loadingPercentage.setVisibility(View.VISIBLE);
                                downloadCount += TOTAL_LEVELS;
                                syncAllData();
                            }
                        } else {
                            startNextActivity();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        startNextActivity();
                    }
                }
            });


        }
    }

    public void syncAllData() {

        for (int i = 1; i <= TOTAL_LEVELS; i++) {

            final String level = "level-" + i;

            final int levelInt = i;

            downloadFile2("level_" + i, ".png", "level-thumbs/level_" + i + ".png");

            final DocumentReference docRef = db.collection("user1").document(level);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            final Map<String, Object> hashMap = document.getData();

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(convertor(hashMap, level));
                                }
                            });
                        } else {
                            startNextActivity();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        startNextActivity();
                    }
                }
            });
        }
    }

    private List<MovieData> convertor(Map<String, Object> hashMap, String level) {

        List<MovieData> list = new ArrayList<>();

        for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
            String movieId = entry.getKey();
            MovieData movieDataTemp = realm.where(MovieData.class).equalTo("levelId", level).and().equalTo("movieId", movieId).findFirst();

            if (movieDataTemp != null) {
                continue;
            }

            Map<String, Object> children = (Map<String, Object>) entry.getValue();
            MovieData movieData = new MovieData();
            movieData.setLevelId(level);
            movieData.setMovieId(movieId);
            movieData.setMovieName((String) children.get(MOVIE_NAME));
            movieData.setWikiContent((String) children.get(WIKI_CONTENT));
            list.add(movieData);
            final String movieImageName = movieData.getMovieName().toLowerCase().replace(" ", "_");

            downloadFile2(movieImageName, ".jpg", level + "/" + movieImageName + ".jpg");
        }

        return list;
    }

    private void downloadFile2(String fileName, String ext, final String getPath) {
        replaceOrCreate(fileName, ext);
        StorageReference pathReference = storageReferenceDownload.child(getPath);
        File rootPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SilmaPeru");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath, fileName + ext);

        pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                monitorDownload++;
                int percetageText = Math.round(((float) monitorDownload / (float) downloadCount) * 100);
                loadingPercentage.setText( percetageText + "%");
                if (monitorDownload >= downloadCount) {
                    startNextActivity();
                }
            }
        });
    }

    public void replaceOrCreate(String fname, String ext) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + "SilmaPeru" + File.separator + fname + ext;
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }


}
