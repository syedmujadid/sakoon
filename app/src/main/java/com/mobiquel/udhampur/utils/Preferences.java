package com.mobiquel.udhampur.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static Preferences instance;
    private String preferenceName = "com.mobiquel.udhampur";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private String KEY_RANDOM_NUMBER = "RANDOM_NUMBER";
    private String KEY_OFFICIAL_ID = "OFFICIAL_ID";
    private String KEY_TOKEN = "TOKEN";

    private String KEY_NAME = "NAME";
    private String KEY_MOBILE = "MOBILE";
    private String KEY_DESIGNATION = "DESIGNATION";
    private String KEY_DISTRICT_ID = "DISTRICT_ID";
    private String KEY_DISTRICT_NAME = "DISTRICT_NAME";
    private String KEY_TEHSIL_ID = "TEHSIL_ID";
    private String KEY_TEHSIL_NAME = "TEHSIL_NAME";
    private String KEY_VILLAGE_ID = "VILLAGE_ID";
    private String KEY_VILLAGE_NAME = "VILLAGE_NAME";
    private String KEY_AREA = "AREA";
    private String KEY_LEVEL = "LEVEL";
    private String DAMAGE_LIST = "DAMAGE_LIST";
    private String OLD_DAMAGE_LIST = "OLD_DAMAGE_LIST";
    private String DAMAGE_CATEG_LIST = "DAMAGE_CATEG_LIST";
    private String OLD_DAMAGE_CATEG_LIST = "OLD_DAMAGE_CATEG_LIST";
    public String randomNumber, officialId, token,name,mobile,designation,districtId,tehsilId,villageId,area,level,tehsilName,villageName,districtName,damageList,oldDamageList,damageCategList,oldDamageCategList;

    private Preferences() {

    }

    public synchronized static Preferences getInstance() {
        if (instance == null)
            instance = new Preferences();
        return instance;
    }

    public void loadPreferences(Context c) {
        preferences = c.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE);
        randomNumber = preferences.getString(KEY_RANDOM_NUMBER, "6543");
        officialId = preferences.getString(KEY_OFFICIAL_ID, "");
        token = preferences.getString(KEY_TOKEN, "");

        name = preferences.getString(KEY_NAME, "");
        mobile = preferences.getString(KEY_MOBILE, "");
        designation = preferences.getString(KEY_DESIGNATION, "");
        tehsilId = preferences.getString(KEY_TEHSIL_ID, "");
        tehsilName = preferences.getString(KEY_TEHSIL_NAME, "");
        villageName = preferences.getString(KEY_VILLAGE_NAME, "");
        villageId = preferences.getString(KEY_VILLAGE_ID, "");
        districtId = preferences.getString(KEY_DISTRICT_ID, "");
        districtName = preferences.getString(KEY_DISTRICT_NAME, "");
        area = preferences.getString(KEY_AREA, "");
        level = preferences.getString(KEY_LEVEL, "");
        damageList = preferences.getString(DAMAGE_LIST, "");
        oldDamageList = preferences.getString(OLD_DAMAGE_LIST, "");
        damageCategList = preferences.getString(DAMAGE_CATEG_LIST, "");
        oldDamageCategList = preferences.getString(OLD_DAMAGE_CATEG_LIST, "");

    }

    public void savePreferences(Context c) {
        preferences = c.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(KEY_RANDOM_NUMBER, randomNumber);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_OFFICIAL_ID, officialId);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_DESIGNATION, designation);
        editor.putString(KEY_TEHSIL_ID,tehsilId);
        editor.putString(KEY_TEHSIL_NAME, tehsilName);
        editor.putString(KEY_VILLAGE_NAME, villageName);
        editor.putString(KEY_VILLAGE_ID, villageId);
        editor.putString(KEY_DISTRICT_ID, districtId);
        editor.putString(KEY_DISTRICT_NAME, districtName);
        editor.putString(KEY_AREA, area);
        editor.putString(KEY_LEVEL, level);
        editor.putString(DAMAGE_LIST, damageList);
        editor.putString(OLD_DAMAGE_LIST, oldDamageList);
        editor.putString(DAMAGE_CATEG_LIST, damageCategList);
        editor.putString(OLD_DAMAGE_CATEG_LIST, oldDamageCategList);
        editor.commit();
    }
}