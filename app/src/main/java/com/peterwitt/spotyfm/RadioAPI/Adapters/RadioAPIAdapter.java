package com.peterwitt.spotyfm.RadioAPI.Adapters;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.peterwitt.spotyfm.R;
import com.peterwitt.spotyfm.RadioAPI.Holders.RadioAPIHolder;
import com.peterwitt.spotyfm.RadioAPI.RadioAPI;
import com.peterwitt.spotyfm.RadioAPI.Callbacks.RadioAPIButtonCallback;

public class RadioAPIAdapter extends FirestoreRecyclerAdapter<RadioAPI, RadioAPIHolder> {

    private RadioAPIButtonCallback callback;

    public RadioAPIAdapter(@NonNull FirestoreRecyclerOptions<RadioAPI> options, RadioAPIButtonCallback callback) {
        super(options);
        this.callback = callback;
    }

    @Override
    protected void onBindViewHolder(@NonNull RadioAPIHolder holder, int position, final @NonNull RadioAPI model) {
        holder.getItemButton().setText(model.getName());

        holder.getItemButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onRadioAPIClicked(model);
            }
        });
    }

    @NonNull
    @Override
    public RadioAPIHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_station_list_item, parent, false);
        return new RadioAPIHolder(v);
    }
}
