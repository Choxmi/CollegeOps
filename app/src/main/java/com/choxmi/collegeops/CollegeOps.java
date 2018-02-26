package com.choxmi.collegeops;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.onesignal.OneSignal;

/**
 * Created by Choxmi on 1/28/2018.
 */

public class CollegeOps extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }

    public static Context getAppContext(){
        return CollegeOps.context;
    }
}
