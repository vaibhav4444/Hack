package com.hackathon.connect.myapplication.common.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.hackathon.connect.myapplication.common.prefs.Prefs;

/**
 * Created by vaibhav on 17/1/17.
 */

public class ApplicationClass extends Application {
    private Prefs mPref;
    private static ApplicationClass mInstance;
    //> for multidexing in API levels prior to lollipop
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mPref = new Prefs(this);
    }
    public static ApplicationClass getAppInstance() {
        return mInstance;
    }
    public Prefs getPrefs(){
        return mPref;
    }
}
