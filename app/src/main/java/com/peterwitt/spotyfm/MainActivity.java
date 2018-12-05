package com.peterwitt.spotyfm;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.internal.bind.MapTypeAdapterFactory;
import com.peterwitt.spotyfm.RadioAPI.RadioAPIManager;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        SpotifyManager.getInstance().setup(this);

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

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Spoify authentication
        if (requestCode == SpotifyManager.REQUEST_CODE) {
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

            switch (response.getType()) {
                case TOKEN:
                    String token = response.getAccessToken();
                    SpotifyManager.getInstance().updateToken(token, response.getExpiresIn());

                    //Map<String, Object> tset = new HashMap<>();
                    //tset.put("token", token);
                    //db.collection("spotify").add(tset);

                    Toast.makeText(MainActivity.this, "Token expires in: " + response.getExpiresIn(), Toast.LENGTH_SHORT).show();
                    break;

                case ERROR:
                    Toast.makeText(MainActivity.this, response.getError(), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    }
}
