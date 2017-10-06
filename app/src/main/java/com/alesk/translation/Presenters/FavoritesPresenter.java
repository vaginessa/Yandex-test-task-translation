package com.alesk.translation.Presenters;

import com.alesk.translation.Models.Favorites;
import com.alesk.translation.Views.FavoritesView;

/**
 * Created by Acer on 13-May-17.
 */

public class FavoritesPresenter extends Presenter<Favorites, FavoritesView> {
    public void initialize(){
        model.loadFavorites();
    }
}
