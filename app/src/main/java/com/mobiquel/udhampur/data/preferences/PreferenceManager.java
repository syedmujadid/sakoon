package com.mobiquel.udhampur.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Navjot Singh
 * on 14/5/18.
 */

public class PreferenceManager {

    private static PreferenceManager instance;
    private SharedPreferences preference;
    private Context context;


    private PreferenceManager(Context context) {
        this.context = context;
        initPreference();
    }


    public static PreferenceManager init(Context context) {
        if (instance == null) {
            synchronized (PreferenceManager.class) {
                if (instance == null)
                    instance = new PreferenceManager(context);
            }
        }
        return instance;
    }


    public static PreferenceManager getInstance() {
        return instance;
    }


    private void initPreference() {
        if (preference == null) {
            preference = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        }
    }


    public String getString(String key) {
        return preference.getString(key, "");
    }


    public void setString(String key, String value) {
        SharedPreferences.Editor editor = preference.edit();
        editor.putString(key, value);
        editor.apply();
    }


    public int getInt(String key) {
        return preference.getInt(key, 0);
    }


    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = preference.edit();
        editor.putInt(key, value);
        editor.apply();
    }


    public boolean getBoolean(String key) {
        return preference.getBoolean(key, false);
    }


    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = preference.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public void clearAllPrefs() {
        SharedPreferences.Editor editor = preference.edit();
        editor.clear();
        editor.apply();
    }

}