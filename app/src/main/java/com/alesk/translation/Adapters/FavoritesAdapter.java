package com.alesk.translation.Adapters;

import android.os.Bundle;
import android.view.View;

import com.alesk.translation.MainActivity;
import com.alesk.translation.Models.Favorites;
import com.alesk.translation.Models.Translator;
import com.alesk.translation.R;
import com.alesk.translation.TranslateApplication;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

/**
 * Created by Acer on 13-May-17.
 */

public class FavoritesAdapter extends TranslatesAdapter {
    private ArrayList<Integer> is_unliked = new ArrayList<>();

    public FavoritesAdapter(MainActivity context, ArrayList<String> translate_text, ArrayList<String> translated_text,
                             ArrayList<String> lang_lang) {
        super(context, translate_text, translated_text, lang_lang, null);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.translate_txt.setText(translate_text.get(position));
        holder.translated_txt.setText(translated_text.get(position));
        holder.lng_from.setText(lang_lang.get(position).substring(0,2));
        holder.lng_to.setText(lang_lang.get(position).substring(3,5));
        if(is_unliked.contains(position)) holder.likeButton.setLiked(false);
        else holder.likeButton.setLiked(true);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = context.getBundle();
                bundle.putInt(TranslateApplication.S_LANG_FROM, Translator.getCodeLangIndex(Favorites.lang_lang.get(position).substring(0, 2)) + 1);
                bundle.putInt(TranslateApplication.S_LANG_TO, Translator.getCodeLangIndex(Favorites.lang_lang.get(position).substring(3, 5)));
                bundle.putString(TranslateApplication.S_TEXT, Favorites.translate_text.get(position));
                MainActivity.navigation.setSelectedItemId(R.id.navigation_home);
            }
        });

        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Favorites.addToFavorites(translate_text.get(position), translated_text.get(position),
                        lang_lang.get(position));
                is_unliked.remove(is_unliked.indexOf(position));
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Favorites.removeFromFavorites(translate_text.get(position),
                        lang_lang.get(position));
                is_unliked.add(position);
            }
        });
    }

}
