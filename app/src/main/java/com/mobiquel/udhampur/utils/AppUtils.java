package com.mobiquel.udhampur.utils;

import android.content.res.Resources;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Navjot Singh
 * on 2/3/19.
 */

public class AppUtils {

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static String formatDateTime(String dateTime, String inputFormat, String outputFormat) {
        SimpleDateFormat inputSDF = new SimpleDateFormat(inputFormat, Locale.getDefault());
        SimpleDateFormat outputSDF = new SimpleDateFormat(outputFormat, Locale.getDefault());
        Date date;
        try {
            date = inputSDF.parse(dateTime);
            return outputSDF.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
