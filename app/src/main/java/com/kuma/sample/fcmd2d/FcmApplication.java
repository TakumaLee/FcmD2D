package com.kuma.sample.fcmd2d;

import android.app.Application;

import com.kuma.sample.fcmd2d.manager.FcmAccountManager;

/**
 * Created by TakumaLee on 2016/9/24.
 */

public class FcmApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FcmAccountManager.initialize(getApplicationContext());
    }

}
