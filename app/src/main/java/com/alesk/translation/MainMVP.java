package com.alesk.translation;

/**
 * Created by Acer on 18-May-17.
 */

public interface MainMVP {
    interface TranslateCallBack{
        void onSuccessTranslate();
    }

    interface LangsCallBack{
        void onLangsLoaded();
    }
}
