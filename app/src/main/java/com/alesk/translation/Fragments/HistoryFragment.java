package com.alesk.translation.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alesk.translation.Adapters.TranslatesAdapter;
import com.alesk.translation.MainActivity;
import com.alesk.translation.Models.History;
import com.alesk.translation.Presenters.HistoryPresenter;
import com.alesk.translation.R;
import com.alesk.translation.Views.HistoryView;

public class HistoryFragment extends Fragment implements HistoryView {
    private HistoryPresenter mHistoryPresenter;
    private RecyclerView recyclerView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        Toolbar toolbar = view.findViewById(R.id.history_toolbar);
        toolbar.inflateMenu(R.menu.history_menu);

        recyclerView = view.findViewById(R.id.history_list);
        final TranslatesAdapter historyAdapter = new TranslatesAdapter(getMainActivity(), History.translate_text, History.translated_text, History.lang_lang, History.fav);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(historyAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                ((LinearLayoutManager)recyclerView.getLayoutManager()).getOrientation()));
        recyclerView.getLayoutManager().onRestoreInstanceState(state);

        toolbar.getMenu().getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                mHistoryPresenter.clearHistory();
                historyAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "История очищена", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        state = recyclerView.getLayoutManager().onSaveInstanceState();
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
