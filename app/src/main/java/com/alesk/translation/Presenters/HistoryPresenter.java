package com.alesk.translation.Presenters;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alesk.translation.DBHelper;
import com.alesk.translation.MainActivity;
import com.alesk.translation.Models.History;
import com.alesk.translation.Models.Translator;
import com.alesk.translation.TranslateApplication;
import com.alesk.translation.Views.HistoryView;

/**
 * Created by Acer on 11-May-17.
 */

public class HistoryPresenter extends Presenter<History, HistoryView> {
    public void initialize(){
        loadHistory();
    }

    private void loadHistory(){
        SQLiteDatabase database = MainActivity.dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_HISTORY,null,null,null,null,null,null);
        int to_index = cursor.getColumnIndex(DBHelper.KEY_TO_TRANSLATE);
        int translated_index = cursor.getColumnIndex(DBHelper.KEY_TRANSLATED);
        int lang_index = cursor.getColumnIndex(DBHelper.KEY_LANG);
        int fav_index = cursor.getColumnIndex(DBHelper.KEY_IS_FAVORITE);

        model.clearData();
        if(cursor.moveToFirst()){
            do{
                model.getTranslate_text().add(0, cursor.getString(to_index));
                model.getTranslated_text().add(0, cursor.getString(translated_index));
                model.getLang_lang().add(0,cursor.getString(lang_index));
                History.fav.add(0, cursor.getString(fav_index).equals("true"));
            }while(cursor.moveToNext());
        }

        cursor.close();
        MainActivity.dbHelper.close();
    }

    public void saveState(SharedPreferences sPref, int position){
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(TranslateApplication.S_TEXT, model.getTranslate_text().get(position));
        ed.putInt(TranslateApplication.S_LANG_FROM, Translator.code_langs.indexOf(model.getLang_lang().get(position).substring(0,2))+1);
        ed.putInt(TranslateApplication.S_LANG_TO, Translator.code_langs.indexOf(model.getLang_lang().get(position).substring(3,5)));
        ed.apply();
    }
}
