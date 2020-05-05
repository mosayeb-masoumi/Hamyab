package com.rahbarbazaar.hamyab.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class Cache {

    private static final String APP_SETTINGS = "UserNameAcrossApplication";

    private Cache() {}

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    // cache string
    public static String getString(Context context,String key) {
        return getSharedPreferences(context).getString(key , null);
    }
    public static void setString(Context context,String key,String newValue) {
        getSharedPreferences(context).edit().putString(key , newValue).apply();
    }

    // cache integer
    public static Integer getInt(Context context, String key) {
        return getSharedPreferences(context).getInt(key , 0);
    }
    public static void setInt(Context context,String key,Integer newValue) {
        getSharedPreferences(context).edit().putInt(key , newValue).apply();
    }




//    public static void saveArray(Context context, String key, ArrayList arrayList) {
//        Gson gson = new Gson();
//        String json = gson.toJson(arrayList);
//        getSharedPreferences(context).edit().putString(key , json).apply();
//    }
//
//    public static void saveObject(Context context, String key, Object object) {
//        Gson gson = new Gson();
//        String json = gson.toJson(object);
//        getSharedPreferences(context).edit().putString(key , json).apply();
//    }
//
//
//    //  get json
//    public static String getJson(Context context, String key) {
//        return getSharedPreferences(context).getString(key , "");
//    }
}
