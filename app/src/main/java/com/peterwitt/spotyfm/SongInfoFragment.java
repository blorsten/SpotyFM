package com.peterwitt.spotyfm;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.peterwitt.spotyfm.RadioAPI.Callbacks.SongDataCallback;
import com.peterwitt.spotyfm.RadioAPI.RadioAPI;
import com.peterwitt.spotyfm.RadioAPI.RadioAPIManager;
import com.peterwitt.spotyfm.RadioAPI.Song;
import com.squareup.picasso.Picasso;

public class SongInfoFragment extends Fragment implements SongDataCallback {

    private View fragmentView;
    private TextView trackTitle;
    private TextView artistName;
    private ImageView albumImage;
    private Button addToSpotify;
    private Button tweakSearch;
    private Button selectPlaylist;
    private Song song;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.song_info_fragment, container, false );
        trackTitle = fragmentView.findViewById(R.id.textView_song_info_title);
        artistName = fragmentView.findViewById(R.id.textView_song_info_artist);
        albumImage = fragmentView.findViewById(R.id.imageView_song_info_image);
        addToSpotify = fragmentView.findViewById(R.id.button_song_info_add_to_spotify);
        selectPlaylist = fragmentView.findViewById(R.id.button_select_playlist);
        tweakSearch = fragmentView.findViewById(R.id.button_tweak_search);
        song = RadioAPIManager.getInstance().getLastSelectedSong();
        setupFragment();

        //Add to spotify button
        addToSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success = SpotifyManager.getInstance().addSongToLibrary(song);
                Toast.makeText(fragmentView.getContext(),
                        song.getTitle() + (success ? " added " : " not added ")+ "to your library",
                        Toast.LENGTH_SHORT).show();
            }
        });

        //select Playlist
        selectPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(fragmentView.getContext(), "Select Playlist pressed", Toast.LENGTH_SHORT).show();
            }
        });

        //tweakSearch
        tweakSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(fragmentView.getContext(), "TweakSearch pressed", Toast.LENGTH_SHORT).show();
            }
        });

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void setupFragment(){
        trackTitle.setText(song.getTitle());
        artistName.setText(song.getArtist());
        if(song.isReady())
            Picasso.get().load(song.getAlbumConverURL()).into(albumImage);
        else
            song.subscribe(this);
    }

    @Override
    public void SongUpdated(Song song) {
        if(getActivity() != null && this.song == song)
        {
            setupFragment();
        }
    }
}
