package com.chris.mordic_pure.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.chris.mordic_pure.conf.Constants;

/**
 * Created by chris on 16-4-23.
 */
public class SpTools {
    public static void putString(Context context,String key,String value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SPFILE, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }
    public static String getString(Context context,String key,String defValue) {
        SharedPreferences SP = context.getSharedPreferences(Constants.SPFILE, Context.MODE_PRIVATE);
        return SP.getString(key,defValue);
    }

    public static void putBoolean(Context context,String key,boolean value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SPFILE, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key,value).commit();
    }
    public static boolean getBoolean(Context context,String key,boolean defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SPFILE, context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key,defValue);
    }
}
