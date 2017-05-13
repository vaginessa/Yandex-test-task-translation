package com.alesk.translation;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alesk.translation.Models.Favorites;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

/**
 * Created by Acer on 13-May-17.
 */

public class FavoritesAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> translate_text;
    private final ArrayList<String> translated_text;
    private final ArrayList<String> lang_lang;

    public FavoritesAdapter(Activity context, ArrayList<String> translate_text, ArrayList<String> translated_text,
                          ArrayList<String> lang_lang) {
        super(context, R.layout.list_item, translate_text);
        this.context = context;
        this.translate_text = translate_text;
        this.translated_text = translated_text;
        this.lang_lang = lang_lang;
    }

    static class ViewHolder{
        TextView translate_txt;
        TextView translated_txt;
        TextView lng_lng;
        LikeButton likeButton;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;

        if(view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.list_item, null, true);
            final int index = position;
            viewHolder = new ViewHolder();
            viewHolder.translate_txt = (TextView) view.findViewById(R.id.translate_text);
            viewHolder.translated_txt = (TextView) view.findViewById(R.id.translated_text);
            viewHolder.lng_lng = (TextView) view.findViewById(R.id.lang_lang);
            viewHolder.likeButton = (LikeButton) view.findViewById(R.id.like_button);

            viewHolder.likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    Favorites.addToFavorites(translate_text.get(index), translated_text.get(index),
                            lang_lang.get(index));
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    Favorites.removeFromFavorites(translate_text.get(index),
                            lang_lang.get(index));
                }
            });
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.translate_txt.setText(translate_text.get(position));
        viewHolder.translated_txt.setText(translated_text.get(position));
        viewHolder.lng_lng.setText(lang_lang.get(position));
        viewHolder.likeButton.setLiked(true);

        return view;
    }
}
