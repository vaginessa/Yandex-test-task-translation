package com.alesk.translation.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alesk.translation.Adapters.HistoryAdapter;
import com.alesk.translation.MainActivity;
import com.alesk.translation.Models.History;
import com.alesk.translation.Presenters.HistoryPresenter;
import com.alesk.translation.R;
import com.alesk.translation.Views.HistoryView;

public class HistoryFragment extends Fragment implements HistoryView {
    private HistoryPresenter mHistoryPresenter;
    private ListView listView;
    private static Parcelable state;

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
    public void onViewCreated(final View view, Bundle bundle){
        super.onViewCreated(view, bundle);
        if(state != null)
        listView.onRestoreInstanceState(state);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        listView = (ListView) view.findViewById(R.id.history_list);
        HistoryAdapter historyAdapter = new HistoryAdapter(getActivity(), History.translate_text, History.translated_text, History.lang_lang, History.fav);
        listView.setAdapter(historyAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mHistoryPresenter.onItemClick(position);
            }
        });

        return view;
    }

    public void onPause(){
        state = listView.onSaveInstanceState();
        super.onPause();
    }

    public MainActivity getMainActivity(){
        return (MainActivity) getActivity();
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
