package com.alesk.translation.Presenters;

/**
 * Created by Acer on 11-May-17.
 */

public abstract class Presenter<M, V> {
    protected M model;
    protected V view;

    public void bindView(V view){
        this.view = view;
    }

    public void initializeModel(M model){
        this.model = model;
    }
}
