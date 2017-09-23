package com.alesk.translation.Presenters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.alesk.translation.MainMVP;
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

public class TranslationPresenter extends Presenter<Translator, TranslationView>
        implements MainMVP.TranslateCallBack, MainMVP.LangsCallBack {

    public void initialize(){
        if(TranslateApplication.hasConnection()) model.requestLangs(this);
        else model.loadLangsFromCache();
    }

    public void onCreateView(){
        view.setLangsFromAdapter(model.langs_from);
        view.setTargetLangsAdapter(model.langs);
        SharedPreferences sPref = TranslateApplication.getAppContext().getSharedPreferences(TranslateApplication.getAppContext().getString(R.string.Prefs_name), MODE_PRIVATE);
        view.setTextToTranslate(sPref.getString(TranslateApplication.S_TEXT, ""));
        view.setTargetLangItemPosition(sPref.getInt(TranslateApplication.S_LANG_TO, 0));
        view.setTranslatedText(sPref.getString(TranslateApplication.S_TEXT_TRANSLATED, ""));
    }

    private void saveText(){
        SharedPreferences sPref = TranslateApplication.getAppContext().getSharedPreferences(
                TranslateApplication.getAppContext().getString(R.string.Prefs_name), MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(TranslateApplication.S_TEXT, view.getTextToTranslate());
        ed.putString(TranslateApplication.S_TEXT_TRANSLATED, view.getTranslatedText());
        ed.apply();
    }

    private void saveLangs(){
        SharedPreferences sPref = TranslateApplication.getAppContext().getSharedPreferences(
                TranslateApplication.getAppContext().getString(R.string.Prefs_name), MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(TranslateApplication.S_LANG_TO, view.getTargetLangItemPosition());
        if(model.getLang() != null)
        ed.putInt(TranslateApplication.S_LANG_FROM, Translator.getCodeLangIndex(model.getLang().substring(0,2))+1);
        ed.apply();
    }

    public void copyToBuffer(String text){
        ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", text);
        clipboard.setPrimaryClip(clip);
    }

    public void afterTextChanged(){
        if (TranslateApplication.hasConnection()) {
            model.translate(this, view.getTextToTranslate(), view.getLangFromItemPosition(), view.getTargetLangItemPosition());
        } else {
            view.setRightsVisible(View.INVISIBLE);
            view.setTranslatedText(TranslateApplication.getAppContext().getString(R.string.no_connection));
            view.setLike(false);
        }
    }

    public void onSwitchButtonClick(){
        int tmp = view.getLangFromItemPosition();

        if (tmp > 0) {
            view.setLangFromItemPosition(view.getTargetLangItemPosition() + 1);
            view.setTargetLangItemPosition(tmp - 1);
        } else {
            view.setLangFromItemPosition(view.getTargetLangItemPosition() + 1);
            if(!view.getTextToTranslate().isEmpty() && TranslateApplication.hasConnection()) {
                view.setTargetLangItemPosition(Translator.getCodeLangIndex(model.getLang().substring(0, 2)));
            } else {
                view.setTargetLangItemPosition(TranslateApplication.getAppContext().getSharedPreferences(
                        TranslateApplication.getAppContext().getString(R.string.Prefs_name), MODE_PRIVATE)
                        .getInt(TranslateApplication.S_LANG_FROM, 1) - 1);
            }
        }

        if(!TranslateApplication.hasConnection()){
            String buff = view.getTextToTranslate();
            view.setTextToTranslate(view.getTranslatedText());
            view.setTranslatedText(buff);
        }else{
            view.setTextToTranslate(view.getTranslatedText());
        }
    }

    public void onDeleteButtonClick(){
        if(!view.getTextToTranslate().isEmpty()) {
            History.addToHistory(view.getTextToTranslate(), model.getTranslatedText(), model.getLang(), view.isLiked());
            view.setTextToTranslate("");
            saveText();
        }
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
            if (!bundle.getString(TranslateApplication.S_TEXT).isEmpty()) {
                view.setLangFromItemPosition(bundle.getInt(TranslateApplication.S_LANG_FROM));
                view.setTargetLangItemPosition(bundle.getInt(TranslateApplication.S_LANG_TO));
                view.setTextToTranslate(bundle.getString(TranslateApplication.S_TEXT));
                bundle.clear();
            }
        }catch(NullPointerException e){}
    }

    public void onPause(){
        if (!view.getTextToTranslate().isEmpty()) History.addToHistory(view.getTextToTranslate(),
                model.getTranslatedText(), model.getLang(), view.isLiked());
    }

    public void onListItemSelected(){
        if(TranslateApplication.hasConnection()) {
            model.translate(this, view.getTextToTranslate(), view.getLangFromItemPosition(), view.getTargetLangItemPosition());
            History.addToHistory(view.getTextToTranslate(), view.getTranslatedText(), model.getLang(), view.isLiked());
        }
        saveLangs();
    }

    public void onLangsLoaded(){
        view.notifySpinnerAdapters();
    }

    public void onSuccessTranslate(){
        view.setTranslatedText(model.getTranslatedText());
        view.setLike(Favorites.isFavorite(view.getTextToTranslate(), model.getLang()));
        view.setRightsVisible(model.getTranslatedText().isEmpty() ? View.INVISIBLE : View.VISIBLE);
        saveText();
        saveLangs();
    }
}
