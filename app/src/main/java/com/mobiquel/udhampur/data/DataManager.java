package com.mobiquel.udhampur.data;

import android.content.Context;

import com.mobiquel.udhampur.data.api.ApiManager;
import com.mobiquel.udhampur.data.preferences.PreferenceManager;


import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;


/**
 * Created by Navjot Singh
 * on 2/3/19.
 */

public class DataManager {

    private static DataManager instance;
    private ApiManager apiManager;
    private PreferenceManager preferenceManager;


    public DataManager(Context context) {
        apiManager = ApiManager.init();
        preferenceManager = PreferenceManager.init(context);
    }


    /**
     * method to initialize Data Manager
     */
    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
        }
    }


    /**
     * @return instance of {@link DataManager}
     */
    public static DataManager getInstance() {
        return instance;
    }


    /**
     * method to save value in preferences
     */
    public void saveStringInPreference(String key, String value) {
        preferenceManager.setString(key, value);
    }

    /**
     * method to save value in preferences
     */
    public void saveIntInPreference(String key, int value) {
        preferenceManager.setInt(key, value);
    }

    /**
     * method to save value in preferences
     */
    public void saveBooleanInPreference(String key, boolean value) {
        preferenceManager.setBoolean(key, value);
    }

    /**
     * method to get value from preferences
     */
    public String getStringFromPreference(String key) {
        return preferenceManager.getString(key);
    }

    /**
     * method to get value from preferences
     */
    public int getIntFromPreference(String key) {
        return preferenceManager.getInt(key);
    }

    /**
     * method to get value from preferences
     */
    public boolean getBooleanFromPreference(String key) {
        return preferenceManager.getBoolean(key);
    }


    // API





    public Call<ResponseBody> registerAppUser(Map<String, String> data) {
        return apiManager.registerAppUser(data);
    }




}

