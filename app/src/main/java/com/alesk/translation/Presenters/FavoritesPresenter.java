package com.alesk.translation.Presenters;

import android.os.Bundle;

import com.alesk.translation.MainActivity;
import com.alesk.translation.Models.Favorites;
import com.alesk.translation.Models.Translator;
import com.alesk.translation.R;
import com.alesk.translation.TranslateApplication;
import com.alesk.translation.Views.FavoritesView;

/**
 * Created by Acer on 13-May-17.
 */

public class FavoritesPresenter extends Presenter<Favorites, FavoritesView> {
    public void initialize(){
        model.loadFavorites();
    }

    private void putTranslate(int position){
        Bundle bundle = view.getMainActivity().getBundle();
        bundle.putInt(TranslateApplication.S_LANG_FROM, Translator.getCodeLangIndex(Favorites.lang_lang.get(position).substring(0, 2)) + 1);
        bundle.putInt(TranslateApplication.S_LANG_TO, Translator.getCodeLangIndex(Favorites.lang_lang.get(position).substring(3, 5)));
        bundle.putString(TranslateApplication.S_TEXT, Favorites.translate_text.get(position));
    }

    public void onItemClick(int position){
        putTranslate(position);
        MainActivity.navigation.setSelectedItemId(R.id.navigation_home);
    }
}
