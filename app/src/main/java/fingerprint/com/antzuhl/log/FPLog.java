package fingerprint.com.antzuhl.log;

import android.util.Log;

import fingerprint.com.antzuhl.BuildConfig;

/**
 * Created by 77423 on 2016/11/7.
 */

public class FPLog {

    public static void log(String message) {
        if (BuildConfig.DEBUG) {
            Log.i("FPLog", message);
        }
    }
}
