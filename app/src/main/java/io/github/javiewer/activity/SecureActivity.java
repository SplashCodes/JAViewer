package io.github.javiewer.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;

public class SecureActivity extends AppCompatActivity {

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }
}
