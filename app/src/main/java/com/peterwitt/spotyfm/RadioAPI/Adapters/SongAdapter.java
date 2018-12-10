package com.peterwitt.spotyfm.RadioAPI.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.peterwitt.spotyfm.R;
import com.peterwitt.spotyfm.RadioAPI.Callbacks.SongListItemCallback;
import com.peterwitt.spotyfm.RadioAPI.Holders.SongHolder;
import com.peterwitt.spotyfm.RadioAPI.RadioAPIManager;
import com.peterwitt.spotyfm.RadioAPI.Song;
import com.squareup.picasso.Picasso;

public class SongAdapter extends RecyclerView.Adapter<SongHolder>{

    private SongListItemCallback callback;

    public SongAdapter(SongListItemCallback callback){
        this.callback = callback;
    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_fragment_item, parent, false);
        return new SongHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SongHolder holder, int position) {
        //get model
        final Song model = RadioAPIManager.getInstance().getRecentSongs()[position];

        //fill data to model
        holder.artistName.setText(model.getArtist());
        holder.songTitle.setText(model.getTitle());
        holder.timeStamp.setText(model.getTimeStamp());
        if(!model.getAlbumConverURL().equals(""))
            Picasso.get().load(model.getAlbumConverURL()).into(holder.coverArt);

        //set on click for view
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSongListItemClicked(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return RadioAPIManager.getInstance().getRecentSongs().length;
    }
}
