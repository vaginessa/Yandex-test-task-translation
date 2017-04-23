package com.alesk.translation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    public static TranslationFragment translationFragment;
    private static FavoritesFragment favoritesFragment;
    private static HistoryFragment historyFragment;
    public static boolean is_connect;
    public static DBHelper dbHelper;
    public static BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setFragment(translationFragment, true);
                    return true;
                case R.id.navigation_favorites:
                    if(favoritesFragment == null) favoritesFragment = new FavoritesFragment();
                    setFragment(favoritesFragment, true);
                    return true;
                case R.id.navigation_history:
                    if(historyFragment == null) historyFragment = new HistoryFragment();
                    setFragment(historyFragment, true);
                    return true;
            }

            updateNavigationBarState(item.getItemId());

            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hasConnection(this);
        dbHelper = new DBHelper(this);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        translationFragment = new TranslationFragment();
        setFragment(translationFragment, false);
    }

    private void setFragment(Fragment fragment, boolean is_replace){
        FragmentTransaction f_transaction = getSupportFragmentManager().beginTransaction();
        if(is_replace) {
            f_transaction.replace(R.id.content, fragment);
        }else {
            f_transaction.add(R.id.content, fragment);
        }
        f_transaction.commit();
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    public static void hasConnection(final Context context)
    {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiInfo != null && wifiInfo.isConnected()) {
                is_connect = true;
                return;
            }
            wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiInfo != null && wifiInfo.isConnected()) {
                is_connect = true;
                return;
            }
            wifiInfo = cm.getActiveNetworkInfo();
            if (wifiInfo != null && wifiInfo.isConnected()) {
                is_connect = true;
                return;
            }
            is_connect = false;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        is_connect = false;
    }

    private void updateNavigationBarState(int actionId){
        Menu menu = navigation.getMenu();

        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(item.getItemId() == actionId);
        }
    }
}
