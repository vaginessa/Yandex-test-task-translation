package com.alesk.translation;

import android.app.Application;
import android.content.Context;

/**
 * Created by Acer on 11-May-17.
 */

public class TranslateApplication extends Application {
    private static Context context;
    public static final String S_LANG_TO = "Last_lang_to";
    public static final String S_LANG_FROM = "Last_lang_from";
    public static final String S_TEXT = "Last_to_translate";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getAppContext(){
        return context;
    }
}
