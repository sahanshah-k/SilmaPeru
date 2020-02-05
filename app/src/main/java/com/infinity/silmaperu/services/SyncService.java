package com.infinity.silmaperu.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.infinity.silmaperu.activities.LevelActivity;
import com.infinity.silmaperu.domain.MovieData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

import static com.firebase.ui.auth.ui.phone.SubmitConfirmationCodeFragment.TAG;
import static com.infinity.silmaperu.config.Constants.TOTAL_LEVELS;

public class SyncService {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    final private String MAPPING_MAP = "mappingMap";
    final private String GUESSED_NAME = "guessedName";
    final private String MOVIE_NAME = "movieName";
    final private String SHUFFLED_NAME = "shuffledName";
    final private String STATUS = "status";
    final private String WIKI_CONTENT = "wikiContent";
    Realm realm;
    private FirebaseFirestore db;
    private View rootView;
    private Context context;
    private StorageReference mStorageRef;


    public SyncService(Context context) {
        verifyStoragePermissions((Activity) context);
        this.context = context;
        db = FirebaseFirestore.getInstance();
        realm = Realm.getDefaultInstance();
        rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    public void syncAllData() {

        final Intent intent = new Intent(context, LevelActivity.class);
        for (int i = 1; i <= TOTAL_LEVELS; i++) {

            final String level = "level-" + i;

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
                            System.out.println("done");
                            context.startActivity(intent);

                        } else {
                            System.out.println("no doc");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

    }

    private List<MovieData> convertor(Map<String, Object> hashMap, String level) {

        List<MovieData> list = new ArrayList<>();

        for (Map.Entry<String, Object> entry : hashMap.entrySet()) {

            Map<String, Object> children = (Map<String, Object>) entry.getValue();
            //RealmList<String> mappingMap = new RealmList<>();

            MovieData movieData = new MovieData();
            movieData.setLevelId(level);
            movieData.setMovieId(entry.getKey());
            //movieData.setGuessedName((String) children.get(GUESSED_NAME));
            movieData.setMovieName((String) children.get(MOVIE_NAME));
            //movieData.setShuffledName((String) children.get(SHUFFLED_NAME));
            //movieData.setStatus((String) children.get(STATUS));
            movieData.setWikiContent((String) children.get(WIKI_CONTENT));
            //movieData.setMappingMap(mappingMap);
            list.add(movieData);
            final String movieImageName =  movieData.getMovieName().toLowerCase().replace(" ", "_");

            mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://movie-guess-c1b59.appspot.com/" + level + "/" + movieImageName + ".jpg");

            mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    downloadFile(uri, movieImageName);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }

        return list;
    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    private void downloadFile(Uri uri, String fileName) {
        replaceOrCreate(fileName);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "SilmaPeru" + File.separator + fileName + ".jpg");
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    public void replaceOrCreate(String fname){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + "SilmaPeru" + File.separator + fname + ".jpg";
        File file = new File(path);
        if(file.exists()) {
            file.delete();
        }
    }



}
