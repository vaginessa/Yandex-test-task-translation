package com.alesk.translation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setFragment(new TranslationFragment(), true);
                    return true;
                case R.id.navigation_favorites:
                    setFragment(new FavoritesFragment(), true);
                    return true;
                case R.id.navigation_history:
                    setFragment(new HistoryFragment(), true);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setFragment(new TranslationFragment(), false);
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
}
