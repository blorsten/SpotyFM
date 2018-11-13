package com.peterwitt.spotyfm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.peterwitt.spotyfm.RadioAPI.Song;
import com.peterwitt.spotyfm.RadioAPI.SongDataCallback;
import com.peterwitt.spotyfm.Utilites.WebResponse;
import com.peterwitt.spotyfm.Utilites.WebUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements WebResponse, SongDataCallback {
    TextView txtString;
    public String url= "https://www.dr.dk/playlister/feeds/nowNext/nowPrev.drxml?items=10&cid=P3";
    private Song mostRecentSong;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtString= (TextView)findViewById(R.id.MainText);
        WebUtils.GetURL(url, this);

        findViewById(R.id.buttonIndex).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText t =  findViewById(R.id.editTextIndex);
                index = Integer.parseInt(t.getText().toString());
                WebUtils.GetURL(url, MainActivity.this);
            }
        });
    }

    @Override
    public void onWebResponse(final String response) {
        try {
            JSONObject root = new JSONObject(response);
            JSONObject now = root.getJSONObject("now");

             String title;
             String artist;

            if(root.getJSONObject("now").getString("status").equals("music") && index == 0){
                title = now.getString("track_title");
                artist =now.getString("display_artist");
            }
            else{
                JSONArray previous = root.getJSONArray("previous");
                JSONObject[] previousTracks = new JSONObject[previous.length()];

                for (int i = 0; i < previous.length(); i++) {
                    previousTracks[i] = previous.optJSONObject(i);
                }

                artist = previousTracks[index].getString("display_artist");
                title = previousTracks[index].getString("track_title");
            }

            title = new String(title.getBytes("Windows-1252"), "UTF-8");
            artist = new String(artist.getBytes("Windows-1252"), "UTF-8");

            final String testTitle = title;
            final String testArtist = artist;

            mostRecentSong = new Song(title, artist);
            mostRecentSong.getData(this);

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtString.setText(String.format("%s - %s", testTitle, testArtist));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebResponseFailue(String reason) {
        Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_LONG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        menu.findItem(R.id.options_refresh).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                WebUtils.GetURL(url, MainActivity.this);
                return true;
            }
        });
        return true;
    }

    @Override
    public void SongUpdated(final Song song) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Picasso.get().load(song.getAlbumConverURL(Song.IMAGE_MEDIUM)).into((ImageView) findViewById(R.id.imageViewAlbum));
            }
        });
    }
}
