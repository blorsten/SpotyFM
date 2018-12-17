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
    private Song song;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Save all the references to views in the layout
        fragmentView = inflater.inflate(R.layout.song_info_fragment, container, false );
        trackTitle = fragmentView.findViewById(R.id.textView_song_info_title);
        artistName = fragmentView.findViewById(R.id.textView_song_info_artist);
        albumImage = fragmentView.findViewById(R.id.imageView_song_info_image);
        Button addToSpotify = fragmentView.findViewById(R.id.button_song_info_add_to_spotify);
        Button selectPlaylist = fragmentView.findViewById(R.id.button_select_playlist);
        Button tweakSearch = fragmentView.findViewById(R.id.button_tweak_search);
        song = RadioAPIManager.getInstance().getLastSelectedSong();
        setupFragment();

        //Add to spotify button
        addToSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add to spotify and save output
                boolean success = SpotifyManager.getInstance().addSongToLibrary(song);

                //Make message for the Toast
                String message;
                if(success)
                    message = song.getTitle() + " added to " + SpotifyManager.getInstance().getSelectedPlaylist();
                else
                    message = "Error adding " + song.getTitle() + " to " + SpotifyManager.getInstance().getSelectedPlaylist();

                Toast.makeText(fragmentView.getContext(),message,Toast.LENGTH_SHORT).show();
            }
        });

        //select Playlist dialog
        selectPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the playlist names
                final String[] playlistNames = SpotifyManager.getInstance().getPlaylistNames().toArray(new String[0]);

                //Find out which playlist is the current selected one
                int currentIntem = 0;
                for (int i = 0; i < playlistNames.length; i++) {
                    if(playlistNames[i].equals(SpotifyManager.getInstance().getSelectedPlaylist())){
                        currentIntem = i;
                        break;
                    }
                }

                //Build the dialog with a title and list with a radial showing the current selected
                AlertDialog.Builder builder = new AlertDialog.Builder(fragmentView.getContext());
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

        //tweakSearch dialog
        tweakSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create the editText for the dialog
                final EditText input = new EditText(fragmentView.getContext());
                input.setText(song.getSearchQuery());

                //Build the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(fragmentView.getContext());
                builder.setTitle("Change spotify search");
                builder.setView(input);

                //Set OK button
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Trim the input
                        String value = input.getText().toString().trim();

                        //update the song
                        song.setSearchQuery(value);
                        song.setAlbumCoverURL("");
                        song.fetchData(new SongDataCallback() {
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

                //Set cancel button
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

    /***
     * Sets up the fragment to display info about the song
     */
    private void setupFragment(){
        trackTitle.setText(song.getTitle());
        artistName.setText(song.getArtist());

        if(song.getIsUpdated() && song.getAlbumConverURL() != null && !song.getAlbumConverURL().equals(""))
            Picasso.get().load(song.getAlbumConverURL()).into(albumImage);
        else
            song.subscribe(this);
    }

    @Override
    public void SongUpdated(Song song) {
        if(getActivity() != null && this.song == song)
            setupFragment();
    }
}
