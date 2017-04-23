package com.alesk.translation;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;

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

import static android.content.Context.MODE_PRIVATE;

public class TranslationFragment extends Fragment {
    private static TextView translated_text;
    private static EditText to_translate;
    private static Spinner lang_from;
    private static Spinner lang_to;
    private static ArrayList<String> langs_from = new ArrayList<>();
    private static ArrayList<String> langs = new ArrayList<>();
    private static ArrayList<String> code_langs = new ArrayList<>();
    private static String translated_string;
    private static String code;
    private static String lang;
    private static SharedPreferences sPref;
    private static ArrayAdapter<String> lang_from_adapter;
    private static ArrayAdapter<String> lang_to_adapter;
    private static boolean need_update;
    private static TextView res;
    private static TextView rights;
    private static LikeButton likeButton;

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

        new GetLangsTask().execute();

        set_lang_from_adapter(view);
        lang_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new TranslateTask().execute();
                addToHistory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        set_lang_to_adapter(view);
        lang_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new TranslateTask().execute();
                sPref = getActivity().getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putInt("Last_lang", position);
                ed.commit();
                addToHistory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

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

        to_translate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(!to_translate.getText().toString().isEmpty()) {
                    try {
                        addToHistory();
                    } catch (Exception e) {
                    }
                }
                return false;
            }
        });

        res = (TextView) view.findViewById(R.id.resource);
        rights = (TextView) view.findViewById(R.id.rights);
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
                int tmp = lang_from.getSelectedItemPosition();
                if(tmp > 0) {
                    lang_from.setSelection(lang_to.getSelectedItemPosition()+1);
                    lang_to.setSelection(tmp-1);
                    addToHistory();
                }
            }
        });

        likeButton = (LikeButton) view.findViewById(R.id.like_button);
        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

            }

            @Override
            public void unLiked(LikeButton likeButton) {

            }
        });

        return view;
    }

    private void addToHistory(){
        SQLiteDatabase database = MainActivity.dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_TO_TRANSLATE, to_translate.getText().toString());
        contentValues.put(DBHelper.KEY_TRANSLATED, translated_text.getText().toString());

        int index_from = lang_from.getSelectedItemPosition();
        int index_to = lang_to.getSelectedItemPosition();

        String lng;
        if(index_from > 0) lng = code_langs.get(index_from-1)+"-"+code_langs.get(index_to);
        else lng = lang;

        contentValues.put(DBHelper.KEY_LANG, lng);

        database.insert(DBHelper.TABLE_HISTORY, null, contentValues);

        HistoryFragment.translate_text.add(0, to_translate.getText().toString());
        HistoryFragment.translated_text.add(0, translated_text.getText().toString());
        HistoryFragment.lang_lang.add(0, lng);

        MainActivity.dbHelper.close();
    }

    private static String translate(){
        try {
            String baseURL = "https://translate.yandex.net/api/v1.5/tr.json/translate?";
            String API_key = "trnsl.1.1.20170421T155302Z.72626cd8e3e77068.a727cb302d6818222e7afcfa360f4d51efe2f25d";
            String lang;
            if(lang_from.getSelectedItem().toString().equals("Автоматически")) {
                lang = code_langs.get(lang_to.getSelectedItemPosition());
            }else{
                lang = code_langs.get(lang_from.getSelectedItemPosition()-1)+"-"+code_langs.get(lang_to.getSelectedItemPosition());
            }
            String text = URLEncoder.encode(to_translate.getText().toString(), "UTF-8");
            return request(baseURL + "key=" + API_key + "&text=" + text + "&lang=" + lang);
        }catch(UnsupportedEncodingException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }catch(Exception e){}

        return null;
    }

    private class TranslateTask extends AsyncTask<Void, Void, Void> {
        String result;
        boolean is_empty;

        protected void onPreExecute(){
            if(need_update){ new GetLangsTask().execute(); need_update = false;}
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
                parseJSON_translate(result);
                translated_text.setText(translated_string);
                res.setVisibility(View.VISIBLE);
                rights.setVisibility(View.VISIBLE);
            }else if(!MainActivity.is_connect){
                res.setVisibility(View.INVISIBLE);
                rights.setVisibility(View.INVISIBLE);
                need_update = true;
                translated_text.setText(getString(R.string.no_connection));
            }else{
                translated_text.setText("");
            }
        }
    }

    private static void parseJSON_translate(String s){
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
    public void onResume(){
        super.onResume();
        Bundle bundle = getArguments();
        try {
            lang_from.setSelection(code_langs.indexOf(bundle.getString("Lang_from"))+1);
            lang_to.setSelection(code_langs.indexOf(bundle.getString("Lang_to")));
            to_translate.setText(bundle.getString("To_translate"));
            translated_text.setText(bundle.getString("Translated"));
        }catch (Exception e){}
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

        lang_from_adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, langs_from);
        lang_from_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        lang_from.setAdapter(lang_from_adapter);
    }

    private void set_lang_to_adapter(View v){
        lang_to = (Spinner) v.findViewById(R.id.lang_to);

        lang_to_adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, langs);
        lang_to_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        lang_to.setAdapter(lang_to_adapter);
    }

    private static String getLangs(){
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

    private class GetLangsTask extends AsyncTask<Void, Void, Void> {
        String result;

        protected void onPreExecute(){}

        @Override
        protected Void doInBackground(Void... params) {
            if(MainActivity.is_connect)
                result = getLangs();
            return null;
        }

        protected void onPostExecute(Void r){
            try {
                JSONParser parser = new JSONParser();
                JSONObject jobj = (JSONObject)((JSONObject) parser.parse(result)).get("langs");

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

                lang_from_adapter.notifyDataSetChanged();
                lang_to_adapter.notifyDataSetChanged();
                sPref = getActivity().getPreferences(MODE_PRIVATE);
                lang_to.setSelection(sPref.getInt("Last_lang", 0));
            }catch(ParseException e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }catch(Exception e){}
        }
    }
}
