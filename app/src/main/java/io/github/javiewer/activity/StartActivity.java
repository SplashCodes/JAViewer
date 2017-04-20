package io.github.javiewer.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;

import io.github.javiewer.Configurations;
import io.github.javiewer.JAViewer;
import io.github.javiewer.R;

public class StartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        checkPermissions();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            checkPermissions();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            new AlertDialog.Builder(StartActivity.this)
                                    .setTitle("权限申请")
                                    .setCancelable(false)
                                    .setMessage("JAViewer 需要储存空间权限，储存用户配置。请您允许。")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            checkPermissions();
                                        }
                                    })
                                    .show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .onSameThread()
                    .check();
            return;
        }

        File oldConfig = new File(StartActivity.this.getExternalFilesDir(null), "configurations.json");
        File config = new File(JAViewer.getStorageDir(), "configurations.json");
        if (oldConfig.exists()) {
            oldConfig.renameTo(config);
        }

        File noMedia = new File(JAViewer.getStorageDir(), ".nomedia");
        try {
            noMedia.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JAViewer.CONFIGURATIONS = Configurations.load(config);

        startActivity(new Intent(StartActivity.this, MainActivity.class));
        finish();
    }

}
