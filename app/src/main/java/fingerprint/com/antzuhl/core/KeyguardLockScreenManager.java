package fingerprint.com.antzuhl.core;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import fingerprint.com.antzuhl.log.FPLog;


public class KeyguardLockScreenManager {
    public final static int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 0;
    private KeyguardManager mKeyManager;

    /**
     * 是否开启锁屏密码，注意：有Api版本限制
     * @return
     */
    public boolean isOpenLockScreenPwd() {
        try {
            if (Build.VERSION.SDK_INT < 16) {
                return false;
            }
            return mKeyManager != null && mKeyManager.isKeyguardSecure();
        } catch (Exception e) {
            return false;
        }
    }

    public KeyguardLockScreenManager(Context context) {
        mKeyManager = getKeyguardManager(context);
    }

    public static android.app.KeyguardManager getKeyguardManager(Context context) {
        KeyguardManager keyguardManager = null;
        try {
            keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        } catch (Throwable throwable) {
            FPLog.log("getKeyguardManager exception");
        }
        return keyguardManager;
    }

    /**
     * 锁屏密码，注意：有Api版本限制
     */
    public void showAuthenticationScreen(Activity activity) {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        Intent intent = mKeyManager.createConfirmDeviceCredentialIntent("锁屏密码", "测试锁屏密码");
        if (intent != null) {
            activity.startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
        }
    }

    public void onDestroy() {
        mKeyManager = null;
    }
}
