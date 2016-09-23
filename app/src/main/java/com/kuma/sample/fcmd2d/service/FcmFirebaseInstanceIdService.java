package com.kuma.sample.fcmd2d.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.kuma.sample.fcmd2d.manager.FcmAccountManager;

/**
 * Created by TakumaLee on 2016/5/24.
 */
public class FcmFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = FcmFirebaseInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        FcmAccountManager.getInstance().setPushToken(refreshedToken);
    }
}
