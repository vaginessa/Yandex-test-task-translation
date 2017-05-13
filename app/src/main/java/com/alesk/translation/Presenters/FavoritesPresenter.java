package com.alesk.translation.Presenters;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alesk.translation.DBHelper;
import com.alesk.translation.MainActivity;
import com.alesk.translation.Models.Favorites;
import com.alesk.translation.Models.Translator;
import com.alesk.translation.TranslateApplication;
import com.alesk.translation.Views.FavoritesView;

/**
 * Created by Acer on 13-May-17.
 */

public class FavoritesPresenter extends Presenter<Favorites, FavoritesView> {
    public void initialize(){
        SQLiteDatabase database = MainActivity.dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_FAVORITES,null,null,null,null,null,null);
        int to_index = cursor.getColumnIndex(DBHelper.KEY_TO_TRANSLATE);
        int translated_index = cursor.getColumnIndex(DBHelper.KEY_TRANSLATED);
        int lang_index = cursor.getColumnIndex(DBHelper.KEY_LANG);

        Favorites.translate_text.clear();
        Favorites.translated_text.clear();
        Favorites.lang_lang.clear();
        if(cursor.moveToFirst()){
            do{
                Favorites.translate_text.add(0, cursor.getString(to_index));
                Favorites.translated_text.add(0, cursor.getString(translated_index));
                Favorites.lang_lang.add(0,cursor.getString(lang_index));
            }while(cursor.moveToNext());
        }

        cursor.close();
        MainActivity.dbHelper.close();
    }

    public void onItemClickListener(SharedPreferences sPref, int position){
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(TranslateApplication.S_TEXT, Favorites.translate_text.get(position));
        ed.putInt(TranslateApplication.S_LANG_FROM, Translator.code_langs.indexOf(Favorites.lang_lang.get(position).substring(0, 2)) + 1);
        ed.putInt(TranslateApplication.S_LANG_TO, Translator.code_langs.indexOf(Favorites.lang_lang.get(position).substring(3, 5)));
        ed.apply();

    }
}
