package com.peterwitt.spotyfm;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.peterwitt.spotyfm.RadioAPI.Callbacks.RadioAPIButtonCallback;
import com.peterwitt.spotyfm.RadioAPI.RadioAPI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentHandler.getInstance().activity = this;
        FragmentHandler.getInstance().MakeFragment(R.id.main_activity_frame_layout, new RadioAPIFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        menu.findItem(R.id.options_refresh).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Update Current API
                return true;
            }
        });
        return true;
    }
}
