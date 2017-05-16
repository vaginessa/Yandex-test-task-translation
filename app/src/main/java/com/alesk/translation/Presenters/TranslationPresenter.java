package com.alesk.translation.Presenters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.alesk.translation.MainActivity;
import com.alesk.translation.Models.Favorites;
import com.alesk.translation.Models.History;
import com.alesk.translation.Models.Translator;
import com.alesk.translation.R;
import com.alesk.translation.TranslateApplication;
import com.alesk.translation.Views.TranslationView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Acer on 10-May-17.
 */

public class TranslationPresenter extends Presenter<Translator, TranslationView> {
    private boolean need_update;

    public void initialize(){
        if(!MainActivity.hasConnection()) {
            view.setTranslatedText(TranslateApplication.getAppContext().getString(R.string.no_connection));
            need_update = true;
        }else{
            model.getLangs(this);
            SharedPreferences sPref = TranslateApplication.getAppContext().getSharedPreferences(TranslateApplication.getAppContext().getString(R.string.Prefs_name), MODE_PRIVATE);
            view.setTargetLangItemPosition(sPref.getInt(TranslateApplication.S_LANG_TO, 0));
            view.setTextToTranslate(sPref.getString(TranslateApplication.S_TEXT, ""));
        }
    }

    private void saveText(){
        SharedPreferences sPref = TranslateApplication.getAppContext().getSharedPreferences(
                TranslateApplication.getAppContext().getString(R.string.Prefs_name), MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(TranslateApplication.S_TEXT, view.getTextToTranslate());
        ed.apply();
    }

    private void saveLangs(){
        SharedPreferences sPref = TranslateApplication.getAppContext().getSharedPreferences(
                TranslateApplication.getAppContext().getString(R.string.Prefs_name), MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(TranslateApplication.S_LANG_TO, view.getTargetLangItemPosition());
        ed.putInt(TranslateApplication.S_LANG_FROM, view.getLangFromItemPosition());
        ed.apply();
    }

    private void copyToBuffer(String text){
        ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", text);
        clipboard.setPrimaryClip(clip);
    }

    public void afterTextChanged(){
        boolean has_connection = false;
        boolean is_empty = view.getTextToTranslate().isEmpty();

        try {
            has_connection = MainActivity.hasConnection();
            if (need_update && has_connection) {
                model.getLangs(this);
                view.setTargetLangItemPosition(TranslateApplication.getAppContext().getSharedPreferences(
                        TranslateApplication.getAppContext().getString(R.string.Prefs_name), MODE_PRIVATE)
                        .getInt(TranslateApplication.S_LANG_TO, 0));
                need_update = false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            if (!is_empty && has_connection) {
                model.translate(view.getTextToTranslate(), view.getLangFromItemPosition(), view.getTargetLangItemPosition());
                view.setLike(Favorites.isFavorite(view.getTextToTranslate(), model.getLang()));
                view.setTranslatedText(model.getTranslatedText());
                view.setRightsVisible(View.VISIBLE);
                saveText();
            } else if (!has_connection) {
                view.setRightsVisible(View.INVISIBLE);
                need_update = true;
                view.setTranslatedText(TranslateApplication.getAppContext().getString(R.string.no_connection));
                view.setLike(false);
            } else {
                view.setTranslatedText("");
                view.setRightsVisible(View.INVISIBLE);
                view.setLike(false);
                saveText();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onSwitchButtonClick(){
        int tmp = view.getLangFromItemPosition();

        if (tmp > 0) {
            view.setLangFromItemPosition(view.getTargetLangItemPosition() + 1);
            view.setTargetLangItemPosition(tmp - 1);
        } else {
            view.setLangFromItemPosition(view.getTargetLangItemPosition() + 1);
            view.setTargetLangItemPosition(model.getCodeLangIndex(model.getLang().substring(0, 2)));
        }

        view.setTextToTranslate(view.getTranslatedText());
    }

    public void onDeleteButtonClick(){
        History.addToHistory(model.getTextToTranslate(), model.getTranslatedText(), model.getLang(), view.isLiked());
        view.setTextToTranslate("");
        saveText();
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

    public void onResume(Bundle bundle){
        try {
            view.setLangFromItemPosition(bundle.getInt(TranslateApplication.S_LANG_FROM));
            view.setTargetLangItemPosition(bundle.getInt(TranslateApplication.S_LANG_TO));
            view.setTextToTranslate(bundle.getString(TranslateApplication.S_TEXT));
        }catch(NullPointerException e){}
    }

    public void onPause(){
        if (MainActivity.hasConnection()) History.addToHistory(model.getTextToTranslate(),
                model.getTranslatedText(), model.getLang(), view.isLiked());
    }

    public void onListItemSelected(){
        model.translate(view.getTextToTranslate(), view.getLangFromItemPosition(), view.getTargetLangItemPosition());
        view.setTranslatedText(model.getTranslatedText());
        saveLangs();
    }

    public void onTranslatedTextClick(){
        copyToBuffer(view.getTranslatedText());
    }

    public void getLangsCallBack(){
        view.notifySpinnerAdapters();
    }
}
