package com.infinity.silmaperu.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.infinity.silmaperu.R;
import com.infinity.silmaperu.domain.UpdateMetadata;
import com.infinity.silmaperu.utilities.NetworkUtils;
import com.infinity.silmaperu.utilities.PermissionUtils;

import io.realm.Realm;


public class PermissionActivity extends AppCompatActivity {

    private Realm realm;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission_activity);
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager
                .PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (PermissionUtils.neverAskAgainSelected(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    displayNeverAskAgainDialog();
                } else {
                    ActivityCompat.requestPermissions(
                            this,
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
                }
            }

        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager
                .PERMISSION_GRANTED) {
            verifyAndLaunch();
        }
    }

    private void displayNeverAskAgainDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We need to cache movie data. Please permit the Storage permission through "
                + "Settings screen.\n\nSelect Permissions -> Select Storage");
        builder.setCancelable(false);
        builder.setPositiveButton("Permit Manually", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (REQUEST_EXTERNAL_STORAGE == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                verifyAndLaunch();
            } else {
                PermissionUtils.setShouldShowStatus(this, Manifest.permission.SEND_SMS);
            }
        }
    }

    public void verifyAndLaunch() {
        UpdateMetadata updateMetadataOffline = realm.where(UpdateMetadata.class).findFirst();
        if (NetworkUtils.isNetworkAvailable(this)) {
            startLoadingActivity();
        } else {
            if (null == updateMetadataOffline) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Please Check your Internet Connection and Retry");
                builder.setCancelable(false);
                builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        verifyAndLaunch();
                    }
                });
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.show();
            } else {
                startLoadingActivity();
            }
        }
    }

    public void startLoadingActivity() {
        Intent loadIntent = new Intent(getApplicationContext(), LoadingActivity.class);
        startActivity(loadIntent);
        finish();
    }
}
