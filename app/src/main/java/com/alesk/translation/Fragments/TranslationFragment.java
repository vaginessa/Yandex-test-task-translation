package com.alesk.translation.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alesk.translation.Models.Translator;
import com.alesk.translation.Presenters.TranslationPresenter;
import com.alesk.translation.R;
import com.alesk.translation.Views.TranslationView;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

public class TranslationFragment extends Fragment implements TranslationView {
    private TextView translated_text;
    private EditText to_translate;
    private Spinner lang_from;
    private Spinner target_lang;
    private ArrayAdapter lang_from_adapter;
    private ArrayAdapter target_lang_adapter;
    private LikeButton likeButton;
    private TextView res;
    private TextView rights;
    private static boolean is_liked;

    private static TranslationPresenter mTranslationPresenter;

    public void setLike(boolean like){
        likeButton.setLiked(like);
        is_liked = like;
    }

    public boolean isLiked(){
        return is_liked;
    }

    public void notifySpinnerAdapters(){
        if(lang_from_adapter != null && target_lang_adapter != null) {
            lang_from_adapter.notifyDataSetChanged();
            target_lang_adapter.notifyDataSetChanged();
        }
    }

    public int getLangFromItemPosition() {
        return lang_from.getSelectedItemPosition();
    }

    public int getTargetLangItemPosition() {
        return target_lang.getSelectedItemPosition();
    }

    public void setLangFromItemPosition(int position){
        lang_from.setSelection(position);
    }

    public void setTargetLangItemPosition(int position){
        target_lang.setSelection(position);
    }

    public String getTextToTranslate(){
        return to_translate.getText().toString().trim();
    }

    public void setTextToTranslate(String text){
        to_translate.setText(text);
        to_translate.setSelection(text.length());
    }

    public void setTranslatedText(String text){
        translated_text.setText(text);
    }

    public String getTranslatedText(){
        return translated_text.getText().toString();
    }

    public void setRightsVisible(int c){
        res.setVisibility(c);
        rights.setVisibility(c);
    }

    public void setLangsFromAdapter(ArrayList langs){
        lang_from_adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, langs);
        lang_from_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lang_from.setAdapter(lang_from_adapter);
    }

    public void setTargetLangsAdapter(ArrayList langs){
        target_lang_adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, langs);
        target_lang_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        target_lang.setAdapter(target_lang_adapter);
    }

    public void makeToast(String text){
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mTranslationPresenter = new TranslationPresenter();
        mTranslationPresenter.initializeModel(new Translator());
        mTranslationPresenter.bindView(this);
        mTranslationPresenter.initialize();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_translation, container, false);

        ((AppCompatActivity) getActivity()).setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        lang_from = (Spinner) view.findViewById(R.id.lang_from);
        target_lang = (Spinner) view.findViewById(R.id.lang_to);
        translated_text = (TextView) view.findViewById(R.id.translated_text);
        to_translate = ((EditText) view.findViewById(R.id.to_translate));
        res = (TextView) view.findViewById(R.id.resource);
        rights = (TextView) view.findViewById(R.id.rights);
        likeButton = (LikeButton) view.findViewById(R.id.like_button);

        mTranslationPresenter.onCreateView();

        lang_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTranslationPresenter.onListItemSelected();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        target_lang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTranslationPresenter.onListItemSelected();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        to_translate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mTranslationPresenter.afterTextChanged();
            }
        });

        res.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("http://translate.yandex.ru/"));
                    getContext().startActivity(i);
                }
        });

        view.findViewById(R.id.switch_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTranslationPresenter.onSwitchButtonClick();
                }
        });

        view.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTranslationPresenter.onDeleteButtonClick();
            }
        });

        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                mTranslationPresenter.onLiked();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                mTranslationPresenter.onUnliked();
            }
        });

        view.findViewById(R.id.copy_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!getTranslatedText().isEmpty()) {
                    mTranslationPresenter.copyToBuffer(getTranslatedText());
                    makeToast("Перевод скопирован в буфер обмена");
                }
            }
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        mTranslationPresenter.onResume(getArguments());
    }

    @Override
    public void onPause(){
        super.onPause();
        mTranslationPresenter.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
