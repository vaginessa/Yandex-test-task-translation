package com.alesk.translation;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

/**
 * Created by Acer on 23-Apr-17.
 */

public class ListAdapter extends ArrayAdapter<String>{
    private final Activity context;
    private final ArrayList<String> translate_text;
    private final ArrayList<String> translated_text;
    private final ArrayList<String> lang_lang;

    public ListAdapter(Activity context, ArrayList<String> translate_text, ArrayList<String> translated_text,
                       ArrayList<String> lang_lang) {
        super(context, R.layout.list_item, translate_text);
        this.context = context;
        this.translate_text = translate_text;
        this.translated_text = translated_text;
        this.lang_lang = lang_lang;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_item, null, true);
        final int index = position;

        final TextView translate_txt = (TextView) rowView.findViewById(R.id.translate_text);
        TextView translated_txt = (TextView) rowView.findViewById(R.id.translated_text);
        TextView lng_lng = (TextView) rowView.findViewById(R.id.lang_lang);
        LikeButton likeButton = (LikeButton) rowView.findViewById(R.id.like_button);
        TranslationFragment.checkFavorites(translate_text.get(position), lang_lang.get(position), likeButton);
        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                TranslationFragment.addToFavorites(translate_text.get(index), translated_text.get(index),
                        lang_lang.get(index));
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                TranslationFragment.removeFromFavorites(translate_text.get(index), translated_text.get(index),
                        lang_lang.get(index));
            }
        });

        translate_txt.setText(translate_text.get(position));
        translated_txt.setText(translated_text.get(position));
        lng_lng.setText(lang_lang.get(position));

        return rowView;
    }
}
