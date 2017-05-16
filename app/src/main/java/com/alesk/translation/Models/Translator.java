package com.alesk.translation.Models;

import com.alesk.translation.Presenters.TranslationPresenter;

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
    public static ArrayList<String> langs_from = new ArrayList<>();
    public static ArrayList<String> langs = new ArrayList<>();
    public static ArrayList<String> code_langs = new ArrayList<>();
    private static String translated_text;
    private static String text_to_translate;
    private static String code;
    private static String lang;

    public String getTextToTranslate(){
        return text_to_translate;
    }

    public String getTranslatedText(){
        return translated_text;
    }

    public void setTranslatedText(String text){
        this.translated_text = text;
    }

    public void setLang(String lang) {
        Translator.lang = lang;
    }

    public String getCode() {
        return Translator.code;
    }

    public String getLang() {
        return Translator.lang;
    }

    public int getCodeLangIndex(String s){
        return code_langs.indexOf(s);
    }

    public void getLangs(TranslationPresenter p){
        GetLangs getLangs = new GetLangs();
        getLangs.start();
        try {
            getLangs.join();
        }catch (InterruptedException ie){}

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

            p.getLangsCallBack();
        }catch(ParseException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static class GetLangs extends Thread{
        String result;

        @Override
        public void run() {
            result = requestLangs();
        }
    }

    public void translate(String text, int lang_from_index, int target_lang_index){
        Translate translate = new Translate(text, lang_from_index, target_lang_index);
        translate.start();
        try {
            translate.join();
        }catch (InterruptedException ie){}

        parseJSON_translate(translate.result);
    }

    private class Translate extends Thread{
        String result;
        int lang_from_index, target_lang_index;

        public Translate(String text, int lang_from_index, int target_lang_index){
            text_to_translate = text;
            this.lang_from_index = lang_from_index;
            this.target_lang_index = target_lang_index;
        }

        @Override
        public void run() {
            try {
                String baseURL = "https://translate.yandex.net/api/v1.5/tr.json/translate?";
                String API_key = "trnsl.1.1.20170421T155302Z.72626cd8e3e77068.a727cb302d6818222e7afcfa360f4d51efe2f25d";
                String to_translate = URLEncoder.encode(text_to_translate, "UTF-8");
                String lang;
                if(lang_from_index == 0) {
                    lang = code_langs.get(target_lang_index);
                }else{
                    lang = code_langs.get(lang_from_index-1)+"-"+code_langs.get(target_lang_index);
                }
                result = request(baseURL + "key=" + API_key + "&text=" + to_translate + "&lang=" + lang);
            }catch(UnsupportedEncodingException e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }catch(Exception e){
                System.out.println("An error was occurred");
                e.printStackTrace();
            }
        }
    }

    private void parseJSON_translate(String s){
        try {
            JSONParser parser = new JSONParser();
            JSONObject jobj = (JSONObject) parser.parse(s);
            translated_text = ((JSONArray)jobj.get("text")).get(0).toString();
            code = jobj.get("code").toString();
            lang = jobj.get("lang").toString();
        }catch(ParseException e){
            e.printStackTrace();
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

            //System.out.println(sb);
            return sb.toString();
        }catch(IOException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
