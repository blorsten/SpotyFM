package com.peterwitt.spotyfm;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;
import com.peterwitt.spotyfm.RadioAPI.DateTime;
import com.peterwitt.spotyfm.RadioAPI.RadioAPIManager;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Save instance
        instance = this;

        //Setup Spotify Manager
        SpotifyManager.getInstance().setup(this);

        //Set default fragment
        FragmentHandler.getInstance().activity = this;
        if(savedInstanceState == null)
            FragmentHandler.getInstance().MakeFragment(R.id.main_activity_frame_layout, new RadioAPIFragment(), true, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        //Time picker dialog
        menu.findItem(R.id.options_pick_time).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //Find time from spotifyManagers time
                DateTime dateTime = SpotifyManager.getInstance().getDateTime();
                int mHour = dateTime.hour;
                int mMinute = dateTime.minute;

                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                DateTime dateTime = SpotifyManager.getInstance().getDateTime();
                                dateTime.hour = hourOfDay;
                                dateTime.minute= minute;
                                dateTime.setSetToCustom(true);
                                RadioAPIManager.getInstance().refreshCurrentAPI();
                            }
                        }, mHour, mMinute, true);

                timePickerDialog.show();

                return true;
            }
        });

        //Reset time
        menu.findItem(R.id.options_reset_time).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SpotifyManager.getInstance().getDateTime().resetTime();
                RadioAPIManager.getInstance().refreshCurrentAPI();
                return true;
            }
        });

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Spoify authentication
        if (requestCode == SpotifyManager.REQUEST_CODE) {

            //if this response is ours
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

            switch (response.getType()) {

                //If we got our token
                case TOKEN:
                    String token = response.getAccessToken();
                    SpotifyManager.getInstance().updateToken(token, response.getExpiresIn());
                    break;

                // if an error happened
                case ERROR:
                    Toast.makeText(MainActivity.this, response.getError(), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    }
}
