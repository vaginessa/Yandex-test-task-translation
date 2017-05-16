package com.alesk.translation.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alesk.translation.FavoritesAdapter;
import com.alesk.translation.MainActivity;
import com.alesk.translation.Models.Favorites;
import com.alesk.translation.Presenters.FavoritesPresenter;
import com.alesk.translation.R;
import com.alesk.translation.Views.FavoritesView;

public class FavoritesFragment extends Fragment implements FavoritesView {
    private static FavoritesPresenter mFavoritesPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mFavoritesPresenter = new FavoritesPresenter();
        mFavoritesPresenter.initializeModel(new Favorites());
        mFavoritesPresenter.bindView(this);
        mFavoritesPresenter.initialize();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        ListView listView = (ListView) view.findViewById(R.id.favorites_list);
        FavoritesAdapter adapter = new FavoritesAdapter(getActivity(), Favorites.translate_text, Favorites.translated_text, Favorites.lang_lang);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mFavoritesPresenter.onItemClick(position);
                }
            });

        return view;
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
