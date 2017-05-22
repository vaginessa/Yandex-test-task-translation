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

    public void loadFavorites(){
        SQLiteDatabase database = MainActivity.dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_FAVORITES,null,null,null,null,null,null);
        int to_index = cursor.getColumnIndex(DBHelper.KEY_TO_TRANSLATE);
        int translated_index = cursor.getColumnIndex(DBHelper.KEY_TRANSLATED);
        int lang_index = cursor.getColumnIndex(DBHelper.KEY_LANG);

        translate_text.clear();
        translated_text.clear();
        lang_lang.clear();

        if (cursor.moveToFirst()) {
            do {
                translate_text.add(0, cursor.getString(to_index));
                translated_text.add(0, cursor.getString(translated_index));
                lang_lang.add(0, cursor.getString(lang_index));
            } while (cursor.moveToNext());
        }

        cursor.close();
        MainActivity.dbHelper.close();
    }

    private static class RemoveFromFavorites extends Thread{
        String to_translate, lang;

        private RemoveFromFavorites(String to_translate, String lang){
            this.to_translate = to_translate;
            this.lang = lang;
        }

        @Override
        public void run() {
            try {
                SQLiteDatabase database = MainActivity.dbHelper.getWritableDatabase();
                Cursor cursor = database.query(DBHelper.TABLE_FAVORITES,null,null,null,null,null,null);
                int to_index = cursor.getColumnIndex(DBHelper.KEY_TO_TRANSLATE);
                int lang_index = cursor.getColumnIndex(DBHelper.KEY_LANG);
                int id = cursor.getColumnIndex(DBHelper.KEY_ID);

                if(cursor.moveToFirst()){
                    do{
                        if(cursor.getString(to_index).equals(to_translate) &&
                                cursor.getString(lang_index).equals(lang)){

                            update(database, to_translate, lang, "false");
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
    }

    public static void removeFromFavorites(String to_translate, String lang){
        RemoveFromFavorites removeFromFavorites = new RemoveFromFavorites(to_translate, lang);
        removeFromFavorites.start();
    }

    private static class AddToFavorites extends Thread{
        String to_translate, translated, lang;

        private AddToFavorites(String to_translate, String translated, String lang){
            this.to_translate = to_translate;
            this.translated = translated;
            this.lang = lang;
        }

        @Override
        public void run() {
            try {
                if ((!to_translate.isEmpty() && !translated.isEmpty()) && !translated.equals(TranslateApplication.getAppContext().getString(R.string.no_connection))) {

                    SQLiteDatabase database = MainActivity.dbHelper.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(DBHelper.KEY_TO_TRANSLATE, to_translate);
                    contentValues.put(DBHelper.KEY_TRANSLATED, translated);
                    contentValues.put(DBHelper.KEY_LANG, lang);

                    database.insert(DBHelper.TABLE_FAVORITES, null, contentValues);
                    update(database, to_translate, lang, "true");

                    MainActivity.dbHelper.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void addToFavorites(String to_translate, String translated, String lang){
        AddToFavorites addToFavorites = new AddToFavorites(to_translate, translated, lang);
        addToFavorites.start();
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

        for(int i = 0; i < History.translate_text.size(); i++){
            if(History.translate_text.get(i).equals(to_trnslt) && History.lang_lang.get(i).equals(lng)){
                History.fav.set(i, fav.equals("true"));
            }
        }
    }

    public static boolean isFavorite(String to_translate, String lang){
        CheckFavoritesThread checkFavoritesThread = new CheckFavoritesThread(to_translate, lang);
        checkFavoritesThread.start();
        try {
            checkFavoritesThread.join();
        }catch(Exception e){}
        return checkFavoritesThread.is_favorite;
    }

    private static class CheckFavoritesThread extends Thread{
        String to_translate;
        String lang;
        boolean is_favorite;

        CheckFavoritesThread(String to_translate, String lang){
            this.to_translate = to_translate;
            this.lang = lang;
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
