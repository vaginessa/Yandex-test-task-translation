package com.alesk.translation.Views;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Acer on 10-May-17.
 */

public interface TranslationView {
    void notifySpinnerAdapters();
    Context getContext();
    String getTextToTranslate();
    void setTextToTranslate(String text);
    void setTranslatedText(String text);
    String getTranslatedText();
    int getLangFromItemPosition();
    int getTargetLangItemPosition();
    void setLangFromItemPosition(int position);
    void setTargetLangItemPosition(int position);
    void setRightsVisible(int c);
    void setLike(boolean like);
    boolean isLiked();
    void makeToast(String text);
    void setLangsFromAdapter(ArrayList langs);
    void setTargetLangsAdapter(ArrayList langs);
}
