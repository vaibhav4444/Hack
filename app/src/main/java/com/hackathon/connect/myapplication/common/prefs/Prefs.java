package com.hackathon.connect.myapplication.common.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vaibhav on 17/1/17.
 */

public class Prefs {

    /**
     * The Constant sharedPrefsName.
     */
    private static final String sharedPrefsName = "AppPrefs";

    private static final String IS_APP_INSTALLED_FIRST_TIME = "isAppInstalledFirstTime";

    /**
     * The m_context.
     */
    private Context context;

    /**
     * Instantiates a new prefs.
     *
     * @param context the context
     */
    public Prefs(Context context) {
        this.context = context;
    }

    /**
     * Gets the.
     *+
     * @return the shared preferences
     */
    private SharedPreferences get() {
        return context.getSharedPreferences(sharedPrefsName,
                Context.MODE_PRIVATE);
    }
    public boolean isAppInstalledFirstTime() {
        return get().getBoolean(IS_APP_INSTALLED_FIRST_TIME, true);
    }

    public void setIsAppInstalledFirstTime(boolean isAppInstalledFirstTime) {
        get().edit().putBoolean(IS_APP_INSTALLED_FIRST_TIME, isAppInstalledFirstTime).commit();
    }
}
