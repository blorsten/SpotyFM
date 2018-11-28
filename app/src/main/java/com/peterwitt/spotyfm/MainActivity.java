package com.peterwitt.spotyfm;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.peterwitt.spotyfm.RadioAPI.Callbacks.RadioAPIButtonCallback;
import com.peterwitt.spotyfm.RadioAPI.RadioAPI;
import com.peterwitt.spotyfm.RadioAPI.RadioAPIManager;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentHandler.getInstance().activity = this;
        FragmentHandler.getInstance().MakeFragment(R.id.main_activity_frame_layout, new RadioAPIFragment(), true, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        menu.findItem(R.id.options_refresh).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(RadioAPIManager.getInstance().getCurrentAPI() != null)
                    RadioAPIManager.getInstance().refreshCurrentAPI();
                return true;
            }
        });
        return true;
    }
}
