package com.alesk.translation;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class TranslationFragment extends Fragment {
    private static TextView translated_text;
    private static EditText to_translate;
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

        translated_text = (TextView) view.findViewById(R.id.translated_text);
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
            String lang = "en";
            String text = URLEncoder.encode(to_translate.getText().toString(), "UTF-8");
            URL url = new URL(baseURL+"key="+API_key+"&text="+text+"&lang="+lang);
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
        }catch(MalformedURLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }catch(IOException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private class TranslateTask extends AsyncTask<Void, Void, Void> {
        String result;
        boolean is_empty;

        protected void onPreExecute(){is_empty = to_translate.getText().toString().isEmpty();}

        @Override
        protected Void doInBackground(Void... params) {
            if(!is_empty)
            result = translate();
            return null;
        }

        protected void onPostExecute(Void r){
            if(!is_empty) {
                parseJSON(result);
                translated_text.setText(translated_string);
            }else{
                translated_text.setText("");
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
}
