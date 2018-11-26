package com.peterwitt.spotyfm;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

    private RadioAPI[] availableStations;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    RadioAPIAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        inflater.inflate(R.layout.radio_station_list_fragment, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Query query = db.collection("apis").orderBy("name");
        FirestoreRecyclerOptions<RadioAPI> options = new FirestoreRecyclerOptions.Builder<RadioAPI>()
                .setQuery(query, RadioAPI.class)
                .build();

        adapter = new RadioAPIAdapter(options, this);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewStations);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onRadioAPIClicked(RadioAPI selected) {
        RadioAPIManager.getInstance().setCurrentAPI(selected);
        //Start new fragment
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
