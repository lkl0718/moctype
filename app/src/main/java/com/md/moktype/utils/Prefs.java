package com.md.moktype.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.md.moktype.common.Constants;
import com.md.moktype.common.security.DeCryptor;
import com.md.moktype.common.security.EnCryptor;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public enum Prefs {

    INSTANCE;

    private static SharedPreferences mPref;
    private static EnCryptor encryptor;
    private static DeCryptor decryptor;

    public static void init(Context context, String prefsName, int mode) {
        mPref = context.getSharedPreferences(prefsName, mode);
        encryptor = new EnCryptor();

        try {
            decryptor = new DeCryptor();
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
            e.printStackTrace();
        }
    }

    public static SharedPreferences getPreferences() {
        if (mPref != null) {
            return mPref;
        } else {
            throw new RuntimeException("Initialize this class in Application.create() first.");
        }
    }

    public static SharedPreferences.Editor clear() {
        SharedPreferences.Editor editor = null;
        if (getPreferences() != null) {
            editor = getPreferences().edit().clear();
            editor.apply();
        }
        return editor;
    }

    public static SharedPreferences.Editor delete(String key) {
        SharedPreferences.Editor editor = null;
        if (getPreferences() != null) {
            editor = getPreferences().edit().remove(key);
            editor.apply();
        }
        return editor;
    }

    public static boolean hasKey(final String key) {
        return getPreferences().contains(key);
    }

    ///////////////////////////////////////////
    // 출력
    //////////////////////////////////////////

    public static String getString(final String key) {
        return getPreferences().getString(key, "");
    }

    public static int getInt(final String key) {
        return getPreferences().getInt(key, -1);
    }

    public static long getLong(final String key) {
        return getPreferences().getLong(key, -1L);
    }

    public static float getFloat(final String key) {
        return getPreferences().getFloat(key, -1F);
    }

    public static boolean getBoolean(final String key) {
        return getPreferences().getBoolean(key, false);
    }

    public static boolean getBoolean(final String key, final boolean defBoolean) {
        return getPreferences().getBoolean(key, defBoolean);
    }

    public static ArrayList<String> getStringArray(String key) {
        String json = getPreferences().getString(key, null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    ///////////////////////////////////////////
    // 입력
    //////////////////////////////////////////

    public static void putString(final String key, final String defValue) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, defValue);
        editor.apply();
    }

    public static void putInt(final String key, final int defValue) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(key, defValue);
        editor.apply();
    }

    public static void putLong(final String key, final long defValue) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putLong(key, defValue);
        editor.apply();
    }

    public static void putFloat(final String key, final float defValue) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putFloat(key, defValue);
        editor.apply();
    }

    public static void putBoolean(final String key, final boolean defValue) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(key, defValue);
        editor.apply();
    }

    public static void putStringArray(String key, ArrayList<String> values) {
        SharedPreferences.Editor editor = getPreferences().edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }

    ///////////////////////////////////////////
    // 암호화
    //////////////////////////////////////////

    /**
     * 암호화
     * @param key - 저장 KEY
     * @param value - 저장하려는 String
     */
    public static void putEncryptedString(String key, String value) {
        doEncryptText(key, value);
    }

    /**
     * 복호화
     * @param key - 저장 KEY
     * @return - 저장했던 String
     */
    public static String getEncryptedString(String key) {
        return doDecryptText(key);
    }

    /**
     * 암호화
     * @param key - 저장 KEY
     * @param value - 저장하려는 String
     */
    private static void doEncryptText(String key, String value) {
        try {

            //암호화된 byte[] 를 base64 String 으로 저장
            putString(key, Base64.encodeToString(encryptor.encryptText(String.valueOf(Constants.SECURITYKEY), value), Base64.DEFAULT));

            //iv 가 있어야만 복호화 가능하기 떄문에
            //암호화시 발생되는 iv 를 base64 String 으로 저장
            putString(key + "_iv", Base64.encodeToString(encryptor.getIv(), Base64.DEFAULT));

        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | NoSuchProviderException |
                KeyStoreException | IOException | NoSuchPaddingException | InvalidKeyException e) {
            LogUtil.e("TEST", "onClick() called with: " + e.getMessage());
        } catch (InvalidAlgorithmParameterException | SignatureException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 복호화
     * @param key - 저장 KEY
     * @return - 저장했던 String
     */
    private static String doDecryptText(String key) {
        try {

            //iv, 암호화 String 가져옴
            String iv = getPreferences().getString(key + "_iv", "");
            String value = getPreferences().getString(key, "");

            //값이 없을때 null 반환
            if(TextUtils.isEmpty(iv) || TextUtils.isEmpty(value))
                return null;

            //복호화
            return decryptor.decryptData(String.valueOf(Constants.SECURITYKEY)
                    , Base64.decode(value, Base64.DEFAULT)
                    , Base64.decode(iv, Base64.DEFAULT));

        } catch (UnrecoverableEntryException | NoSuchAlgorithmException |
                KeyStoreException | NoSuchPaddingException | NoSuchProviderException | IOException | InvalidKeyException e) {
            LogUtil.e("TEST", "decryptData() called with: " + e.getMessage());
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
