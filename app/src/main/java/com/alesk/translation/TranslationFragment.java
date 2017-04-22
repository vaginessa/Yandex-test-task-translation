package com.alesk.translation;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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

import javax.net.ssl.HttpsURLConnection;

public class TranslationFragment extends Fragment {
    private static TextView translated_text;
    private static EditText to_translate;
    private static Spinner lang_from;
    private static Spinner lang_to;
    private static ArrayList<String> langs = new ArrayList<>();
    private static String[] code_langs;
    private static String translated_string;
    private static String code;
    private static String lang;

    public TranslationFragment() {}

    public static TranslationFragment newInstance() {
        TranslationFragment fragment = new TranslationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_translation, container, false);

        ((AppCompatActivity)getActivity()).setSupportActionBar((Toolbar)view.findViewById(R.id.toolbar));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        set_lang_from_adapter(view);
        set_lang_to_adapter(view);

        new GetLangsTask().execute();

        translated_text = (TextView) view.findViewById(R.id.translated_text);
        if(!MainActivity.is_connect) translated_text.setText(getString(R.string.no_connection));
        to_translate = ((EditText)view.findViewById(R.id.to_translate));

        to_translate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                new TranslateTask().execute();
            }
        });

        return view;
    }

    private static String translate(){
        try {
            String baseURL = "https://translate.yandex.net/api/v1.5/tr.json/translate?";
            String API_key = "trnsl.1.1.20170421T155302Z.72626cd8e3e77068.a727cb302d6818222e7afcfa360f4d51efe2f25d";
            String lang;
            if(lang_from.getSelectedItem().toString().equals("Автоматически")) {
                lang = code_langs[lang_to.getSelectedItemPosition()];
            }else{
                lang = code_langs[lang_from.getSelectedItemPosition()-1]+"-"+code_langs[lang_to.getSelectedItemPosition()];
            }
            String text = URLEncoder.encode(to_translate.getText().toString(), "UTF-8");
            return request(baseURL + "key=" + API_key + "&text=" + text + "&lang=" + lang);
        }catch(UnsupportedEncodingException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private class TranslateTask extends AsyncTask<Void, Void, Void> {
        String result;
        boolean is_empty;

        protected void onPreExecute(){
            MainActivity.hasConnection(getActivity());
            is_empty = to_translate.getText().toString().isEmpty();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(!is_empty && MainActivity.is_connect)
            result = translate();
            return null;
        }

        protected void onPostExecute(Void r){
            if(!is_empty && MainActivity.is_connect) {
                parseJSON(result);
                translated_text.setText(translated_string);
            }else if(is_empty){
                translated_text.setText("");
            }else{
                translated_text.setText(getString(R.string.no_connection));
            }
        }
    }

    private static void parseJSON(String s){
        try {
            JSONParser parser = new JSONParser();

            JSONObject jobj = (JSONObject) parser.parse(s);

            translated_string = ((JSONArray)jobj.get("text")).get(0).toString();
            code = jobj.get("code").toString();
            lang = jobj.get("lang").toString();
        }catch(ParseException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }catch(Exception e){}
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void set_lang_from_adapter(View v){
        lang_from = (Spinner) v.findViewById(R.id.lang_from);

        ArrayList<String> langs_from = new ArrayList<>();
        langs_from.addAll(langs);
        langs_from.add(0, "Автоматически");

        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, langs_from);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        lang_from.setAdapter(adapter);
    }

    private void set_lang_to_adapter(View v){
        lang_to = (Spinner) v.findViewById(R.id.lang_to);

        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, langs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        lang_to.setAdapter(adapter);
    }

    private static void getLangs(){
        String baseURL = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?";
        String API_key = "trnsl.1.1.20170421T155302Z.72626cd8e3e77068.a727cb302d6818222e7afcfa360f4d51efe2f25d";
        String ui = lang_from.getSelectedItemPosition()-1 < 0 ? "ru" : code_langs[lang_from.getSelectedItemPosition()-1];
        System.out.println(request(baseURL+"key="+API_key+"&ui="+ui));
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














    private class GetLangsTask extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute(){}

        @Override
        protected Void doInBackground(Void... params) {
            if(MainActivity.is_connect)
                getLangs();
            return null;
        }

        protected void onPostExecute(Void r){

        }
    }




}
