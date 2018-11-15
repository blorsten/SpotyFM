package com.peterwitt.spotyfm;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.peterwitt.spotyfm.RadioAPI.RadioAPI;
import com.peterwitt.spotyfm.RadioAPI.RadioAPIAdapter;
import com.peterwitt.spotyfm.RadioAPI.RadioAPIButtonCallback;
import com.peterwitt.spotyfm.RadioAPI.RadioAPIHolder;
import com.peterwitt.spotyfm.RadioAPI.RadioAPIManager;
import com.peterwitt.spotyfm.RadioAPI.Song;
import com.peterwitt.spotyfm.RadioAPI.SongDataCallback;
import com.peterwitt.spotyfm.Utilites.WebResponse;
import com.peterwitt.spotyfm.Utilites.WebUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements RadioAPIButtonCallback {

    private RadioAPI[] availableStations;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    RadioAPIAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Query query = db.collection("apis").orderBy("name");
        FirestoreRecyclerOptions<RadioAPI> options = new FirestoreRecyclerOptions.Builder<RadioAPI>()
                .setQuery(query, RadioAPI.class)
                .build();

        adapter = new RadioAPIAdapter(options, this);
        RecyclerView recyclerView = findViewById(R.id.recycleViewStations);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onRadioAPIClicked(RadioAPI selected) {
        RadioAPIManager.getInstance().setCurrentAPI(selected);
        //Start New Fragment
    }
}
