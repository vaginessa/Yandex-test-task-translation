package com.alesk.translation.Views;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Acer on 10-May-17.
 */

public interface TranslationView {
    void updateSpinnerAdapters();
    Context getContext();
    String getTextToTranslate();
    void setTextToTranslate(String text);
    void setTranslatedText(String text);
    String getTranslatedText();
    int getLangFromItemPosition();
    int getTargetLangItemPosition();
    void setLangFromItemPosition(int position);
    void setTargetLangItemPosition(int position);
    SharedPreferences getPreferences();
    void setRightsVisible(int c);
    void setLike(boolean like);
    boolean isLiked();
}
