package fingerprint.com.antzuhl.core;

import android.annotation.TargetApi;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import fingerprint.com.antzuhl.log.FPLog;

@TargetApi(Build.VERSION_CODES.M)
public class CryptoObjectCreator {

    private static final String KEY_NAME = "crypto_object_fingerprint_key";

    private FingerprintManager.CryptoObject mCryptoObject;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private Cipher mCipher;

    public interface ICryptoObjectCreateListener {
        void onDataPrepared(FingerprintManager.CryptoObject cryptoObject);
    }

    public CryptoObjectCreator(ICryptoObjectCreateListener createListener) {
        mKeyStore = providesKeystore();
        mKeyGenerator = providesKeyGenerator();
        mCipher = providesCipher(mKeyStore);
        if (mKeyStore != null && mKeyGenerator != null && mCipher != null) {
            mCryptoObject = new FingerprintManager.CryptoObject(mCipher);
        }
        prepareData(createListener);
    }

    private void prepareData(final ICryptoObjectCreateListener createListener) {
        new Thread("FingerprintLogic:InitThread") {
            @Override
            public void run() {
                try {
                    if (mCryptoObject != null) {
                        createKey();
                        // Set up the crypto object for later. The object will be authenticated by use
                        // of the fingerprint.
                        if (!initCipher()) {
                            FPLog.log("Failed to init Cipher.");
                        }
                    }
                } catch (Exception e) {
                    FPLog.log(" Failed to init Cipher, e:" + Log.getStackTraceString(e));
                }
                if (createListener != null) {
                    createListener.onDataPrepared(mCryptoObject);
                }
            }
        }.start();
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     */
    private void createKey() {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder
            mKeyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            FPLog.log(" Failed to createKey, e:" + Log.getStackTraceString(e));
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialize the {@link Cipher} instance with the created key in the {@link #createKey()}
     * method.
     *
     * @return {@code true} if initialization is successful, {@code false} if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated.
     */
    private boolean initCipher() {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME, null);
            mCipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            FPLog.log(" Failed to initCipher, e:" + Log.getStackTraceString(e));
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            FPLog.log(" Failed to initCipher, e :" + Log.getStackTraceString(e));
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    public static KeyStore providesKeystore() {
        try {
            return KeyStore.getInstance("AndroidKeyStore");
        } catch (Throwable e) {
            return null;
        }
    }

    public static KeyGenerator providesKeyGenerator() {
        try {
            return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (Throwable e) {
            return null;
        }
    }

    public static Cipher providesCipher(KeyStore keyStore) {
        try {
            return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (Throwable e) {
            return null;
        }
    }

    public FingerprintManager.CryptoObject getCryptoObject() {
        return mCryptoObject;
    }

    public void onDestroy() {
        mCipher = null;
        mCryptoObject = null;
        mCipher = null;
        mKeyStore = null;
    }
}
