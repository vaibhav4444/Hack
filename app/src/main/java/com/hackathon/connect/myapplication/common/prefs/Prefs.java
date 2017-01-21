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

    private static final String USERNAME_MOBILE = "uMobile";

    private static final String PASSWORD = "password";

    private static final String VENDOR_ID = "vendorID";

    private static final String VENDOR_NAME = "vendorName";



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
    public String getPassword() {
        return get().getString(PASSWORD, "");
    }

    public void setPassword(String password) {
        get().edit().putString(PASSWORD, password).commit();
    }
    public String getUsername() {
        return get().getString(USERNAME_MOBILE, "");
    }

    public void setUsername(String username) {
        get().edit().putString(USERNAME_MOBILE, username).commit();
    }
    public int getVendorId() {
        return get().getInt(VENDOR_ID, 0);
    }

    public void setVendorId(int vendorId) {
        get().edit().putInt(VENDOR_ID, vendorId).commit();
    }

    public void setVendorName(String vendorName) {
        get().edit().putString(VENDOR_NAME, vendorName).commit();
    }

    public String getVendorName() {
        return get().getString(VENDOR_NAME, "");
    }
}
