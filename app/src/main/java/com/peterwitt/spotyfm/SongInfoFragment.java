package com.peterwitt.spotyfm;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.peterwitt.spotyfm.RadioAPI.Callbacks.SongDataCallback;
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
                String message = null;

                if(success)
                    message = song.getTitle() + " added to " + SpotifyManager.getInstance().getSelectedPlaylist();
                else
                    message = "Error adding " + song.getTitle() + " to " + SpotifyManager.getInstance().getSelectedPlaylist();

                Toast.makeText(fragmentView.getContext(),message,Toast.LENGTH_SHORT).show();
            }
        });

        //select Playlist
        selectPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] playlistNames = SpotifyManager.getInstance().getPlaylistNames().toArray(new String[0]);
                int currentIntem = 0;

                for (int i = 0; i < playlistNames.length; i++) {
                    if(playlistNames[i].equals(SpotifyManager.getInstance().getSelectedPlaylist())){
                        currentIntem = i;
                        break;
                    }
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(fragmentView.getContext());
                builder.setTitle("Pick playlist")
                        .setSingleChoiceItems(playlistNames, currentIntem, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SpotifyManager.getInstance().setSelectedPlaylist(playlistNames[which]);
                                dialog.dismiss();
                            }
                        });

                builder.show();
            }
        });

        //tweakSearch
        tweakSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(fragmentView.getContext());
                final EditText input = new EditText(fragmentView.getContext());
                input.setText(song.getSearchQuery());
                builder.setTitle("Change spotify search");
                builder.setView(input);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString().trim();
                        song.setSearchQuery(value);
                        song.setAlbumCoverURL("");
                        song.getData(new SongDataCallback() {
                            @Override
                            public void SongUpdated(Song song) {
                                if(!song.getSpotifyID().equals(""))
                                    setupFragment();
                                else
                                    Toast.makeText(fragmentView.getContext(), "Error searching for given song", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });

                builder.show();
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
