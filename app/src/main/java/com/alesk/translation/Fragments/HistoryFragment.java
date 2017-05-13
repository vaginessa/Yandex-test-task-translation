package com.alesk.translation.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alesk.translation.HistoryAdapter;
import com.alesk.translation.MainActivity;
import com.alesk.translation.Models.History;
import com.alesk.translation.Presenters.HistoryPresenter;
import com.alesk.translation.R;
import com.alesk.translation.Views.HistoryView;

import static android.content.Context.MODE_PRIVATE;

public class HistoryFragment extends Fragment implements HistoryView {
    private static SharedPreferences sPref;
    private HistoryPresenter mHistoryPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mHistoryPresenter = new HistoryPresenter();
        mHistoryPresenter.initializeModel(new History());
        mHistoryPresenter.bindView(this);
        mHistoryPresenter.initialize();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ListView listView = (ListView) view.findViewById(R.id.history_list);

        HistoryAdapter listAdapter = new HistoryAdapter(getActivity(), History.translate_text, History.translated_text, History.lang_lang, History.fav);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mHistoryPresenter.saveState(getPreferences(), position);
                MainActivity.navigation.setSelectedItem(0);
            }
        });

        return view;
    }

    public SharedPreferences getPreferences(){
        sPref = getActivity().getPreferences(MODE_PRIVATE);
        return sPref;
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
