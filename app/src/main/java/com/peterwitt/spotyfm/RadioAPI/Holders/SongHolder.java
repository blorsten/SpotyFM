package com.peterwitt.spotyfm.RadioAPI.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.peterwitt.spotyfm.R;

public class SongHolder extends RecyclerView.ViewHolder {
    public ImageView coverArt;
    public TextView songTitle;
    public TextView artistName;
    public TextView timeStamp;
    public View view;

    public SongHolder(View itemView) {
        super(itemView);

        coverArt = itemView.findViewById(R.id.imageViewSongListItemCoverArt);
        songTitle = itemView.findViewById(R.id.textViewSongListItemTitle);
        artistName = itemView.findViewById(R.id.textViewSongListItemArtist);
        timeStamp = itemView.findViewById(R.id.textViewSongListItemTime);
        view = itemView;
    }
}
