package com.peterwitt.spotyfm;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.peterwitt.spotyfm.RadioAPI.Adapters.RadioAPIAdapter;
import com.peterwitt.spotyfm.RadioAPI.Callbacks.RadioAPIButtonCallback;
import com.peterwitt.spotyfm.RadioAPI.RadioAPI;
import com.peterwitt.spotyfm.RadioAPI.RadioAPIManager;

public class RadioAPIFragment extends Fragment implements RadioAPIButtonCallback {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RadioAPIAdapter adapter;
    private View fragmentView;

    @Override
    public void onStart() {
        //Build the query for firestore
        Query query = db.collection("apis").orderBy("name");
        FirestoreRecyclerOptions<RadioAPI> options = new FirestoreRecyclerOptions.Builder<RadioAPI>()
                .setQuery(query, RadioAPI.class)
                .build();

        //Attach adapter to recyclerView
        adapter = new RadioAPIAdapter(options, this);
        RecyclerView recyclerView = fragmentView.findViewById(R.id.recyclerViewStations);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(fragmentView.getContext()));
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.radio_station_list_fragment, container, false);
        return fragmentView;
    }

    @Override
    public void onRadioAPIClicked(RadioAPI selected) {
        //Update API and load new fragment
        RadioAPIManager.getInstance().setCurrentAPI(selected);
        FragmentHandler.getInstance().MakeFragment(R.id.main_activity_frame_layout, new SongListFragment());
    }

    @Override
    public void onResume() {
        adapter.startListening();
        super.onResume();
    }

    @Override
    public void onPause() {
        adapter.stopListening();
        super.onPause();
    }
}
