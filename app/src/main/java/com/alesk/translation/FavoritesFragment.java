package com.alesk.translation;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class FavoritesFragment extends Fragment {
    private static ListView listView;
    private static ListAdapter adapter;
    public static ArrayList<String> translate_text = new ArrayList<>();
    public static ArrayList<String> translated_text = new ArrayList<>();
    public static ArrayList<String> lang_lang = new ArrayList<>();

    public FavoritesFragment() {}

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadFavorites();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        listView = (ListView) view.findViewById(R.id.favorites_list);
        adapter = new ListAdapter(getActivity(), translate_text, translated_text, lang_lang);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sPref = getActivity().getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("Last_to_translate", translate_text.get(position));
                ed.putInt("Last_lang_from", TranslationFragment.code_langs.indexOf(lang_lang.get(position).substring(0,2))+1);
                ed.putInt("Last_lang_to", TranslationFragment.code_langs.indexOf(lang_lang.get(position).substring(3,5)));
                ed.commit();

                FragmentTransaction f_transaction = getActivity().getSupportFragmentManager().beginTransaction();
                f_transaction.replace(R.id.content, MainActivity.translationFragment);
                f_transaction.commit();

                Menu menu = MainActivity.navigation.getMenu();
                menu.getItem(0).setChecked(true);
                menu.getItem(2).setChecked(false);
            }
        });

        return view;
    }

    private static void loadFavorites(){
        SQLiteDatabase database = MainActivity.dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_FAVORITES,null,null,null,null,null,null);
        int to_index = cursor.getColumnIndex(DBHelper.KEY_TO_TRANSLATE);
        int translated_index = cursor.getColumnIndex(DBHelper.KEY_TRANSLATED);
        int lang_index = cursor.getColumnIndex(DBHelper.KEY_LANG);

        translate_text.clear();
        translated_text.clear();
        lang_lang.clear();
        if(cursor.moveToFirst()){
            do{
                translate_text.add(0, cursor.getString(to_index));
                translated_text.add(0, cursor.getString(translated_index));
                lang_lang.add(0,cursor.getString(lang_index));
            }while(cursor.moveToNext());
        }

        cursor.close();
        MainActivity.dbHelper.close();
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
