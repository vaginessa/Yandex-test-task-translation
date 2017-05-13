package com.alesk.translation.Models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alesk.translation.DBHelper;
import com.alesk.translation.MainActivity;
import com.alesk.translation.R;
import com.alesk.translation.TranslateApplication;

import java.util.ArrayList;

/**
 * Created by Acer on 13-May-17.
 */

public class Favorites {
    public static ArrayList<String> translate_text = new ArrayList<>();
    public static ArrayList<String> translated_text = new ArrayList<>();
    public static ArrayList<String> lang_lang = new ArrayList<>();

    public static void removeFromFavorites(String to_trnslt, String lng){
        try {
            SQLiteDatabase database = MainActivity.dbHelper.getWritableDatabase();
            Cursor cursor = database.query(DBHelper.TABLE_FAVORITES,null,null,null,null,null,null);
            int to_index = cursor.getColumnIndex(DBHelper.KEY_TO_TRANSLATE);
            int lang_index = cursor.getColumnIndex(DBHelper.KEY_LANG);
            int id = cursor.getColumnIndex(DBHelper.KEY_ID);

            if(cursor.moveToFirst()){
                do{
                    if(cursor.getString(to_index).equals(to_trnslt) &&
                            cursor.getString(lang_index).equals(lng)){

                        update(database, to_trnslt, lng, "false");
                        database.delete(DBHelper.TABLE_FAVORITES, DBHelper.KEY_ID+" = " + cursor.getInt(id), null);
                    }
                }while(cursor.moveToNext());
            }

            cursor.close();
            MainActivity.dbHelper.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void addToFavorites(String to_trnslt, String trnsltd, String lng){
        try {
            if ((!to_trnslt.isEmpty() && !trnsltd.isEmpty()) && !trnsltd.equals(TranslateApplication.getAppContext().getString(R.string.no_connection))) {

                SQLiteDatabase database = MainActivity.dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();

                contentValues.put(DBHelper.KEY_TO_TRANSLATE, to_trnslt);
                contentValues.put(DBHelper.KEY_TRANSLATED, trnsltd);
                contentValues.put(DBHelper.KEY_LANG, lng);

                database.insert(DBHelper.TABLE_FAVORITES, null, contentValues);
                update(database, to_trnslt, lng, "true");

                MainActivity.dbHelper.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void update(SQLiteDatabase db, String to_trnslt, String lng, String fav){
        Cursor cursor = db.query(DBHelper.TABLE_HISTORY,null,null,null,null,null,null);
        int to_index = cursor.getColumnIndex(DBHelper.KEY_TO_TRANSLATE);
        int translated_index = cursor.getColumnIndex(DBHelper.KEY_TRANSLATED);
        int lang_index = cursor.getColumnIndex(DBHelper.KEY_LANG);
        int id_index = cursor.getColumnIndex(DBHelper.KEY_ID);
        ContentValues cv = new ContentValues();

        if(cursor.moveToFirst()){
            do{
                if(cursor.getString(to_index).equals(to_trnslt) && cursor.getString(lang_index).equals(lng)){
                    cv.put(DBHelper.KEY_TO_TRANSLATE, cursor.getString(to_index));
                    cv.put(DBHelper.KEY_TRANSLATED, cursor.getString(translated_index));
                    cv.put(DBHelper.KEY_LANG, cursor.getString(lang_index));
                    cv.put(DBHelper.KEY_IS_FAVORITE, fav);
                    int id = cursor.getInt(id_index);
                    db.update(DBHelper.TABLE_HISTORY, cv, DBHelper.KEY_ID+" = " + id, null);
                }
            }while(cursor.moveToNext());
        }
        cursor.close();
    }

    public static boolean isFavorite(String to_translate, String lang){
        CheckFavoritesThread checkFavoritesThread = new CheckFavoritesThread(to_translate, lang);
        checkFavoritesThread.start();
        try{ checkFavoritesThread.join(); }catch(InterruptedException ie){ie.printStackTrace();}
        return checkFavoritesThread.getResponse();
    }

    private static class CheckFavoritesThread extends Thread{
        String to_translate;
        String lang;
        boolean is_favorite;

        public CheckFavoritesThread(String to_translate, String lang){
            this.to_translate = to_translate;
            this.lang = lang;
        }

        public boolean getResponse(){
            return this.is_favorite;
        }

        @Override
        public void run() {
            try {
                SQLiteDatabase database = MainActivity.dbHelper.getWritableDatabase();
                Cursor cursor = database.query(DBHelper.TABLE_FAVORITES,null,null,null,null,null,null);
                int to_index = cursor.getColumnIndex(DBHelper.KEY_TO_TRANSLATE);
                int lang_index = cursor.getColumnIndex(DBHelper.KEY_LANG);

                if(cursor.moveToFirst()){
                    do{
                        if(cursor.getString(to_index).equals(to_translate) && cursor.getString(lang_index).equals(lang)){
                            cursor.close();
                            MainActivity.dbHelper.close();
                            is_favorite = true;
                            return;
                        }
                    }while(cursor.moveToNext());
                }

                cursor.close();
                MainActivity.dbHelper.close();
                is_favorite = false;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
