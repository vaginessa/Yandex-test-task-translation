package com.alesk.translation.Models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.alesk.translation.DBHelper;
import com.alesk.translation.MainActivity;
import com.alesk.translation.MainMVP;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Acer on 10-May-17.
 */

public class Translator {
    public ArrayList<String> langs_from = new ArrayList<>();
    public ArrayList<String> langs = new ArrayList<>();
    private static ArrayList<String> code_langs = new ArrayList<>();
    private String translated_text;
    private String code;
    private String lang;
    private TranslateTask translateTask;

    public void setLangs(String langs){
    }

    public String getTranslatedText(){
        return translated_text;
    }

    public String getCode() {
        return this.code;
    }

    public String getLang() {
        return this.lang;
    }

    public static int getCodeLangIndex(String s){
        return Translator.code_langs.indexOf(s);
    }

    public void requestLangs(MainMVP.LangsCallBack callBack){
        GetLangs getLangs = new GetLangs();
        getLangs.start();
        try {
            getLangs.join();
        }catch (InterruptedException ie){ return; }

        try {
            JSONParser parser = new JSONParser();
            JSONObject jobj = (JSONObject)((JSONObject) parser.parse(getLangs.result)).get("langs");

            langs.clear();
            langs_from.clear();

            HashMap<String, String> lngs = new HashMap<>();

            ArrayList<String> keys = new ArrayList<>(); keys.addAll(jobj.keySet());
            langs.addAll(jobj.values());
            for(int i = 0; i < keys.size(); i++){
                lngs.put(langs.get(i), keys.get(i));
            }

            Collections.sort(langs);
            for(int i = 0; i < langs.size(); i++){
                code_langs.add(lngs.get(langs.get(i)));
            }

            langs_from.addAll(langs);
            langs_from.add(0, "Автоматически");

            cacheLangs();
            callBack.onLangsLoaded();
        }catch(ParseException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void cacheLangs(){
        SQLiteDatabase database = MainActivity.dbHelper.getWritableDatabase();
        Cursor c = database.rawQuery("SELECT  * FROM " + DBHelper.TABLE_LANGS, null);

        if(c.getCount() < langs.size()) {
            database.delete(DBHelper.TABLE_LANGS, null, null);
            ContentValues contentValues = new ContentValues();

            for (int i = 0; i < code_langs.size(); i++) {
                contentValues.put(DBHelper.KEY_LANG, code_langs.get(i));
                contentValues.put(DBHelper.KEY_VALUE, langs.get(i));
                database.insert(DBHelper.TABLE_LANGS, null, contentValues);
            }
        }

        c.close();
        MainActivity.dbHelper.close();
    }

    private class GetLangs extends Thread{
        String result;

        @Override
        public void run() {
            result = requestLangs();
        }
    }

    public void loadLangsFromCache(){
        SQLiteDatabase database = MainActivity.dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_LANGS,null,null,null,null,null,null);
        int langs_index = cursor.getColumnIndex(DBHelper.KEY_LANG);
        int value_index = cursor.getColumnIndex(DBHelper.KEY_VALUE);

        if (cursor.moveToFirst()) {
            do {
                code_langs.add(cursor.getString(langs_index));
                langs.add(cursor.getString(value_index));
            } while (cursor.moveToNext());
        }

        langs_from.addAll(langs);
        langs_from.add(0, "Автоматически");

        cursor.close();
        MainActivity.dbHelper.close();
    }

    public void translate(MainMVP.TranslateCallBack callBack, String text, int lang_from_index, int target_lang_index){
        if(translateTask != null && translateTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            translateTask.cancel(true);
        }

        translateTask = new TranslateTask(callBack, lang_from_index, target_lang_index);
        translateTask.execute(text);
    }

    private class TranslateTask extends AsyncTask<String, Void, Void>{
        MainMVP.TranslateCallBack callBack;
        int lang_from_index;
        int target_lang_index;

        private TranslateTask(MainMVP.TranslateCallBack callBack, int lang_from_index, int target_lang_index){
            this.callBack = callBack;
            this.lang_from_index = lang_from_index;
            this.target_lang_index = target_lang_index;
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected Void doInBackground(String... text) {
            try {
                if(text[0].isEmpty()) {
                    translated_text = "";
                    callBack.onSuccessTranslate();
                    return null;
                }
                String baseURL = "https://translate.yandex.net/api/v1.5/tr.json/translate?";
                String API_key = "trnsl.1.1.20170421T155302Z.72626cd8e3e77068.a727cb302d6818222e7afcfa360f4d51efe2f25d";
                String to_translate = URLEncoder.encode(text[0], "UTF-8");
                String lang;
                if(lang_from_index == 0) {
                    lang = code_langs.get(target_lang_index);
                }else{
                    lang = code_langs.get(lang_from_index-1)+"-"+code_langs.get(target_lang_index);
                }
                if(isCancelled()) return null;
                String result = request(baseURL + "key=" + API_key + "&text=" + to_translate + "&lang=" + lang);
                parseJSON_translate(result);
            }catch(UnsupportedEncodingException e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            callBack.onSuccessTranslate();
        }
    }

    private void parseJSON_translate(String s){
        try {
            JSONParser parser = new JSONParser();
            JSONObject jobj = (JSONObject) parser.parse(s);
            translated_text = ((JSONArray)jobj.get("text")).get(0).toString();
            code = jobj.get("code").toString();
            lang = jobj.get("lang").toString();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static String requestLangs(){
        String baseURL = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?";
        String API_key = "trnsl.1.1.20170421T155302Z.72626cd8e3e77068.a727cb302d6818222e7afcfa360f4d51efe2f25d";
        String ui = "ru";
        return request(baseURL+"key="+API_key+"&ui="+ui);
    }

    private static String request(String url_string){
        try {
            URL url = new URL(url_string);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            InputStream response = con.getInputStream();
            InputStreamReader reader = new InputStreamReader(response);
            char[] buffer = new char[256];
            int rc;
            StringBuilder sb = new StringBuilder();
            while ((rc = reader.read(buffer)) != -1)
                sb.append(buffer, 0, rc);
            reader.close();

            return sb.toString();
        }catch(IOException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
