package com.alesk.translation.Adapters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alesk.translation.MainActivity;
import com.alesk.translation.Models.Favorites;
import com.alesk.translation.Models.History;
import com.alesk.translation.Models.Translator;
import com.alesk.translation.R;
import com.alesk.translation.TranslateApplication;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

/**
 * Created by Acer on 05-Oct-17.
 */

public class TranslatesAdapter extends RecyclerView.Adapter<TranslatesAdapter.ViewHolder>{
    final MainActivity context;
    final ArrayList<String> translate_text;
    final ArrayList<String> translated_text;
    final ArrayList<String> lang_lang;
    private ArrayList<Boolean> fav;

    public TranslatesAdapter(MainActivity context, ArrayList<String> translate_text, ArrayList<String> translated_text,
                          ArrayList<String> lang_lang, ArrayList<Boolean> fav) {
        this.context = context;
        this.translate_text = translate_text;
        this.translated_text = translated_text;
        this.lang_lang = lang_lang;
        this.fav = fav;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView translate_txt;
        TextView translated_txt;
        TextView lng_from;
        TextView lng_to;
        LikeButton likeButton;

        ViewHolder(View view){
            super(view);
            this.mView = view;
            this.translate_txt = view.findViewById(R.id.translate_text);
            this.translated_txt = view.findViewById(R.id.translated_text);
            this.lng_from = view.findViewById(R.id.from_lng);
            this.lng_to = view.findViewById(R.id.to_lng);
            this.likeButton = view.findViewById(R.id.like_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.translate_txt.setText(translate_text.get(position));
        holder.translated_txt.setText(translated_text.get(position));
        holder.lng_from.setText(lang_lang.get(position).substring(0,2));
        holder.lng_to.setText(lang_lang.get(position).substring(3,5));
        holder.likeButton.setLiked(fav.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = context.getBundle();
                bundle.putInt(TranslateApplication.S_LANG_FROM, Translator.getCodeLangIndex(History.lang_lang.get(position).substring(0, 2)) + 1);
                bundle.putInt(TranslateApplication.S_LANG_TO, Translator.getCodeLangIndex(History.lang_lang.get(position).substring(3, 5)));
                bundle.putString(TranslateApplication.S_TEXT, History.translate_text.get(position));
                MainActivity.navigation.setSelectedItemId(R.id.navigation_home);
            }
        });

        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Favorites.addToFavorites(translate_text.get(position), translated_text.get(position),
                        lang_lang.get(position));
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Favorites.removeFromFavorites(translate_text.get(position),
                        lang_lang.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return translate_text.size();
    }
}
