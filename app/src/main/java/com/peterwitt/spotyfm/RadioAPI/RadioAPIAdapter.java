package com.peterwitt.spotyfm.RadioAPI;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.peterwitt.spotyfm.R;

public class RadioAPIAdapter extends FirestoreRecyclerAdapter<RadioAPI, RadioAPIHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RadioAPIAdapter(@NonNull FirestoreRecyclerOptions<RadioAPI> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RadioAPIHolder holder, int position, @NonNull RadioAPI model) {
        holder.getItemName().setText(model.getName());
    }

    @NonNull
    @Override
    public RadioAPIHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_station_list_item, parent, false);
        return new RadioAPIHolder(v);
    }
}
