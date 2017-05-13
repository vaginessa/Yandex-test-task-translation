package com.alesk.translation.Presenters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.alesk.translation.MainActivity;
import com.alesk.translation.Models.Favorites;
import com.alesk.translation.Models.History;
import com.alesk.translation.Models.Translator;
import com.alesk.translation.R;
import com.alesk.translation.TranslateApplication;
import com.alesk.translation.Views.TranslationView;

/**
 * Created by Acer on 10-May-17.
 */

public class TranslationPresenter extends Presenter<Translator, TranslationView> {
    private boolean need_update;

    public void initialize(){
        if(!MainActivity.hasConnection()) {
            view.setTranslatedText(TranslateApplication.getAppContext().getString(R.string.no_connection));
        }else{
            model.getLangs(view);
        }
    }

    public void translate(){
        boolean has_connection = false;
        String result = null;
        boolean is_empty = view.getTextToTranslate().isEmpty();

        try {
            has_connection = MainActivity.hasConnection();
            if (need_update && has_connection) {
                model.getLangs(view);
                need_update = false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        if(!is_empty && has_connection) result = model.getTranslate(view.getTextToTranslate(),
                view.getLangFromItemPosition(), view.getTargetLangItemPosition());

        try {
            if (!is_empty && has_connection) {
                model.parseJSON_translate(result);
                view.setLike(Favorites.isFavorite(view.getTextToTranslate(), model.getLang()));
                view.setTranslatedText(model.getTranslatedText());
                view.setRightsVisible(View.VISIBLE);
                saveState(view.getPreferences());
            } else if (!has_connection) {
                view.setRightsVisible(View.INVISIBLE);
                need_update = true;
                view.setTranslatedText(TranslateApplication.getAppContext().getString(R.string.no_connection));
                view.setLike(false);
            } else {
                view.setTranslatedText("");
                view.setLike(false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveState(SharedPreferences sPref){
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(TranslateApplication.S_LANG_TO, view.getTargetLangItemPosition());
        ed.putInt(TranslateApplication.S_LANG_FROM, view.getLangFromItemPosition());
        ed.putString(TranslateApplication.S_TEXT, view.getTextToTranslate());
        ed.apply();
    }

    public void copyToBuffer(String text){
        ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", text);
        clipboard.setPrimaryClip(clip);
    }

    public void onSwitchButtonClick(){
        int tmp = view.getLangFromItemPosition();
        if (tmp > 0) {
            view.setLangFromItemPosition(view.getTargetLangItemPosition() + 1);
            view.setTargetLangItemPosition(tmp - 1);
        }else{
            view.setLangFromItemPosition(view.getTargetLangItemPosition()+1);
            view.setTargetLangItemPosition(model.getCodeLangIndex(model.getLang().substring(0,2)));
        }

        view.setTextToTranslate(view.getTranslatedText());
    }

    public void onDeleteButtonClick(){
        History.addToHistory(model.getTextToTranslate(), model.getTranslatedText(), model.getLang(), view.isLiked());
        view.setTextToTranslate("");
        saveState(view.getPreferences());
    }

    public void onLiked(){
        Favorites.addToFavorites(view.getTextToTranslate(), view.getTranslatedText(),
                model.getLang());
        view.setLike(true);
    }

    public void onUnliked(){
        Favorites.removeFromFavorites(view.getTextToTranslate(), model.getLang());
        view.setLike(false);
    }

    public void onResume(){
        SharedPreferences sPref = view.getPreferences();
        view.setLangFromItemPosition(sPref.getInt(TranslateApplication.S_LANG_FROM, 0));
        view.setTargetLangItemPosition(sPref.getInt(TranslateApplication.S_LANG_TO, 0));
        view.setTextToTranslate(sPref.getString(TranslateApplication.S_TEXT, ""));
    }

    public void onPause(){
        if(MainActivity.hasConnection()) History.addToHistory(model.getTextToTranslate(),
                model.getTranslatedText(), model.getLang(), view.isLiked());
    }

    public void onListItemSelected(){
        translate();
        saveState(view.getPreferences());
    }
}
