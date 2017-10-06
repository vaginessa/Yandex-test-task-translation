package com.alesk.translation.Presenters;

import com.alesk.translation.Models.History;
import com.alesk.translation.Views.HistoryView;

/**
 * Created by Acer on 11-May-17.
 */

public class HistoryPresenter extends Presenter<History, HistoryView>{
    public void initialize(){
        model.loadHistory();
    }

    public void clearHistory(){
        model.deleteHistory();
    }
}
