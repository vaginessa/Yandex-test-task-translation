package com.alesk.translation;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Acer on 11-May-17.
 */

public final class TranslateApplication extends Application {
    private static Context context;
    public static final String S_LANG_TO = "Last_lang_to";
    public static final String S_LANG_FROM = "Last_lang_from";
    public static final String S_TEXT = "Last_to_translate";
    public static final String S_TEXT_TRANSLATED = "Last_translated";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getAppContext(){
        return context;
    }

    public static boolean hasConnection() {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                    TranslateApplication.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiInfo != null && wifiInfo.isConnected()) {
                return true;
            }
            wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiInfo != null && wifiInfo.isConnected()) {
                return true;
            }
            wifiInfo = cm.getActiveNetworkInfo();
            if (wifiInfo != null && wifiInfo.isConnected()) {
                return true;
            }
            return false;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
