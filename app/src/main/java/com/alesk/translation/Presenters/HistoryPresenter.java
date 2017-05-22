package com.alesk.translation.Presenters;

import android.os.Bundle;

import com.alesk.translation.MainActivity;
import com.alesk.translation.Models.History;
import com.alesk.translation.Models.Translator;
import com.alesk.translation.TranslateApplication;
import com.alesk.translation.Views.HistoryView;

/**
 * Created by Acer on 11-May-17.
 */

public class HistoryPresenter extends Presenter<History, HistoryView>{
    public void initialize(){
        model.loadHistory();
    }

    private void putTranslate(int position){
        Bundle bundle = view.getMainActivity().getBundle();
        bundle.putInt(TranslateApplication.S_LANG_FROM, Translator.getCodeLangIndex(History.lang_lang.get(position).substring(0, 2)) + 1);
        bundle.putInt(TranslateApplication.S_LANG_TO, Translator.getCodeLangIndex(History.lang_lang.get(position).substring(3, 5)));
        bundle.putString(TranslateApplication.S_TEXT, History.translate_text.get(position));
    }

    public void onItemClick(int position){
        putTranslate(position);
        MainActivity.navigation.setSelectedItem(0);
    }
}
