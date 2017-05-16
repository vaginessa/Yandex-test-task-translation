package com.alesk.translation.Models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alesk.translation.DBHelper;
import com.alesk.translation.MainActivity;

import java.util.ArrayList;

/**
 * Created by Acer on 11-May-17.
 */

public class History {
    public static ArrayList<String> translate_text = new ArrayList<>();
    public static ArrayList<String> translated_text = new ArrayList<>();
    public static ArrayList<String> lang_lang = new ArrayList<>();
    public static ArrayList<Boolean> fav = new ArrayList<>();

    public ArrayList<String> getTranslate_text() {
        return translate_text;
    }

    public ArrayList<String> getTranslated_text() {
        return translated_text;
    }

    public ArrayList<String> getLang_lang() {
        return lang_lang;
    }

    public void clearData(){
        translate_text.clear();
        translated_text.clear();
        lang_lang.clear();
        fav.clear();
    }

    public static void addToHistory(String text_to_translate, String translated_text, String lang, boolean is_favorite) {
        try {
            if (((!translated_text.isEmpty() && !lang.isEmpty()) && ((translate_text.size() > 0 &&
               (!translate_text.get(0).equals(text_to_translate) || !lang_lang.get(0).equals(lang))) ||
               translate_text.size() == 0)) && MainActivity.hasConnection()) {

                SQLiteDatabase database = MainActivity.dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();

                contentValues.put(DBHelper.KEY_TO_TRANSLATE, text_to_translate);
                contentValues.put(DBHelper.KEY_TRANSLATED, translated_text);
                contentValues.put(DBHelper.KEY_LANG, lang);
                contentValues.put(DBHelper.KEY_IS_FAVORITE, is_favorite ? "true" : "false");

                database.insert(DBHelper.TABLE_HISTORY, null, contentValues);

                History.translate_text.add(0, text_to_translate);
                History.translated_text.add(0, translated_text);
                History.lang_lang.add(0, lang);
                History.fav.add(0, is_favorite);

                MainActivity.dbHelper.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadHistory(){
        SQLiteDatabase database = MainActivity.dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_HISTORY,null,null,null,null,null,null);
        int to_index = cursor.getColumnIndex(DBHelper.KEY_TO_TRANSLATE);
        int translated_index = cursor.getColumnIndex(DBHelper.KEY_TRANSLATED);
        int lang_index = cursor.getColumnIndex(DBHelper.KEY_LANG);
        int fav_index = cursor.getColumnIndex(DBHelper.KEY_IS_FAVORITE);

        clearData();
        if(cursor.moveToFirst()){
            do{
                translate_text.add(0, cursor.getString(to_index));
                translated_text.add(0, cursor.getString(translated_index));
                lang_lang.add(0,cursor.getString(lang_index));
                fav.add(0, cursor.getString(fav_index).equals("true"));
            }while(cursor.moveToNext());
        }

        cursor.close();
        MainActivity.dbHelper.close();
    }
}
