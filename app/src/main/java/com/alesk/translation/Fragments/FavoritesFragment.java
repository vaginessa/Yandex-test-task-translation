package com.alesk.translation.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alesk.translation.Adapters.FavoritesAdapter;
import com.alesk.translation.MainActivity;
import com.alesk.translation.Models.Favorites;
import com.alesk.translation.Presenters.FavoritesPresenter;
import com.alesk.translation.R;
import com.alesk.translation.Views.FavoritesView;

public class FavoritesFragment extends Fragment implements FavoritesView {
    private FavoritesPresenter mFavoritesPresenter;
    private RecyclerView recyclerView;
    private static Parcelable state;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mFavoritesPresenter = new FavoritesPresenter();
        mFavoritesPresenter.initializeModel(new Favorites());
        mFavoritesPresenter.bindView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        mFavoritesPresenter.initialize();
        recyclerView = view.findViewById(R.id.favorites_list);
        FavoritesAdapter adapter = new FavoritesAdapter(getMainActivity(), Favorites.translate_text, Favorites.translated_text, Favorites.lang_lang);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                ((LinearLayoutManager)recyclerView.getLayoutManager()).getOrientation()));
        recyclerView.getLayoutManager().onRestoreInstanceState(state);

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
