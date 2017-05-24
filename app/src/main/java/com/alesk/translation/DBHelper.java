package com.alesk.translation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Acer on 23-Apr-17.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TranslateDB";
    private static final int DATABASE_VERSION = 3;
    public static final String TABLE_HISTORY = "History";
    public static final String KEY_ID = "_id";
    public static final String KEY_TO_TRANSLATE = "To_translate";
    public static final String KEY_TRANSLATED = "Translated";
    public static final String KEY_LANG = "Lang";
    public static final String KEY_IS_FAVORITE = "Is_favorite";
    public static final String TABLE_FAVORITES = "Favorites";
    public static final String TABLE_LANGS = "Langs";
    public static final String KEY_VALUE = "Value";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_HISTORY + "(" + KEY_ID + " integer primary key,"
                + KEY_TO_TRANSLATE + " text," + KEY_TRANSLATED + " text," + KEY_LANG + " text," +
                KEY_IS_FAVORITE + " text" + ")");

        db.execSQL("create table " + TABLE_FAVORITES + "(" + KEY_ID + " integer primary key,"
                + KEY_TO_TRANSLATE + " text," + KEY_TRANSLATED + " text," + KEY_LANG + " text" + ")");

        db.execSQL("create table " + TABLE_LANGS + "(" + KEY_ID + " integer primary key,"
                + KEY_LANG + " text," + KEY_VALUE + " text" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("create table " + TABLE_LANGS + "(" + KEY_ID + " integer primary key,"
                + KEY_LANG + " text," + KEY_VALUE + " text" + ")");
    }
}
