package com.kuma.sample.fcmd2d;

import android.app.Application;

import com.kuma.sample.fcmd2d.context.ApplicationContextSingleton;
import com.kuma.sample.fcmd2d.manager.FcmAccountManager;

/**
 * Created by TakumaLee on 2016/9/24.
 */

public class FcmApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationContextSingleton.initialize(getApplicationContext());
        FcmAccountManager.initialize(getApplicationContext());
    }

}
