package io.github.javiewer.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import io.github.javiewer.Configurations;
import io.github.javiewer.JAViewer;
import io.github.javiewer.Properties;
import io.github.javiewer.R;
import io.github.javiewer.adapter.item.DataSource;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class StartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        checkPermissions(); //检查权限，创建配置
    }

    public void updateBtsoUrl() {
        Request request = new Request.Builder()
                .url("https://btso-url.zcong.workers.dev")
                .build();
        JAViewer.HTTP_CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String url =  response.body().string();
                if (url != "") {
                    JAViewer.CONFIGURATIONS.setBtsoUrl(url);
                }
            }
        });
    }

    public void readProperties() {
        Request request = new Request.Builder()
                .url("https://btso-url.zcong.workers.dev/config?t=" + System.currentTimeMillis() / 1000)
                .build();
        JAViewer.HTTP_CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final Properties properties = JAViewer.parseJson(Properties.class, response.body().string());
                if (properties != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            handleProperties(properties);
                        }
                    });
                }
            }
        });
    }

    public void handleProperties(Properties properties) {
        JAViewer.DATA_SOURCES.clear();
        JAViewer.DATA_SOURCES.addAll(properties.getDataSources());

        JAViewer.hostReplacements.clear();
        for (DataSource source : JAViewer.DATA_SOURCES) {
            try {
                String host = new URI(source.getLink()).getHost();
                for (String h : source.legacies) {
                    JAViewer.hostReplacements.put(h, host);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        start();

        // int currentVersion;
        // try {
        //     currentVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
        // } catch (PackageManager.NameNotFoundException e) {
        //     throw new RuntimeException("Hacked???");
        // }

        // if (properties.getLatestVersionCode() > 0 && currentVersion < properties.getLatestVersionCode()) {

        //     String message = "新版本：" + properties.getLatestVersion();
        //     if (properties.getChangelog() != null) {
        //         message += "\n\n更新日志：\n\n" + properties.getChangelog() + "\n";
        //     }

        //     final AlertDialog dialog = new AlertDialog.Builder(this)
        //             .setTitle("发现更新")
        //             .setMessage(message)
        //             .setNegativeButton("忽略更新", new DialogInterface.OnClickListener() {
        //                 @Override
        //                 public void onClick(DialogInterface dialogInterface, int i) {
        //                     start();
        //                 }
        //             })
        //             .setPositiveButton("更新", new DialogInterface.OnClickListener() {
        //                 @Override
        //                 public void onClick(DialogInterface dialog, int which) {
        //                     start();
        //                     startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/SplashCodes/JAViewer/releases")));
        //                 }
        //             })
        //             .create();
        //     dialog.show();
        // } else {
        //     start();
        // }

    }

    public void start() {
        startActivity(new Intent(StartActivity.this, MainActivity.class));
        finish();
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

        readProperties();
        updateBtsoUrl();
    }

}
