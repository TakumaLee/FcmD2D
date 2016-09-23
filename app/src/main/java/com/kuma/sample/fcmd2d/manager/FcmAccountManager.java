package com.kuma.sample.fcmd2d.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by TakumaLee on 2016/6/2.
 */
public class FcmAccountManager {
    private static final String TAG = FcmAccountManager.class.getSimpleName();

    private static final String PUSH_TOKEN = "push_token";

    private String pushToken;

    private SharedPreferences noClearSharedPreferences;
    private SharedPreferences.Editor noClearEditor;

    private static FcmAccountManager INSTANCE = null;

    public synchronized static FcmAccountManager getInstance() {
        return INSTANCE;
    }

    public FcmAccountManager(Context context) {
        initNoClearSharedPreferences(context);
    }

    public static void initialize(Context context) {
        INSTANCE = new FcmAccountManager(context);
    }

    private void initNoClearSharedPreferences(Context context) {
        noClearSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        noClearEditor = noClearSharedPreferences.edit();
        pushToken = noClearSharedPreferences.getString(PUSH_TOKEN, null);
    }

    public void setPushToken(String pushToken) {
        Log.d(TAG, "commit token");
        this.pushToken = pushToken;
        noClearEditor.putString(PUSH_TOKEN, pushToken);
        noClearEditor.commit();
    }

    public String getPushToken() {
        return pushToken;
    }

}
