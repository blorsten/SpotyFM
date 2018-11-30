package com.peterwitt.spotyfm.RadioAPI;

import android.util.Log;

import com.peterwitt.spotyfm.RadioAPI.Callbacks.SongDataCallback;
import com.peterwitt.spotyfm.SpotifyManager;
import java.util.List;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Song {

    private String title = "";
    private String artist = "";
    private String album = "";
    private String timeStamp = "";
    private String albumCoverURL = "";
    private String spotifyID = "";

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    private SongDataCallback callback;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getSpotifyID() {
        return spotifyID;
    }

    public void setSpotifyID(String spotifyID) {
        this.spotifyID = spotifyID;
    }

    public Song(String name, String artist){
        this.title = name;
        this.artist = artist;
    }

    public void getData(SongDataCallback callback){
        this.callback = callback;
        loadInfo();
    }

    public void songUpdated(){
        callback.SongUpdated(this);
    }

    public String getAlbumConverURL(){
        return albumCoverURL;
    }

    public void setAlbumCoverURL(String albumCoverURL) {
        this.albumCoverURL = albumCoverURL;
    }

    private void loadInfo() {

        //remove characters that do not work with spotify
        String q = getTitle() + " " + getArtist();
        String regex = "feat.|&|\\(([^)]+)\\)";
        q = q.replaceAll(regex, "");

        Log.d("TESTING", q);

        SpotifyManager.getInstance().getService().searchTracks(q, new Callback<TracksPager>() {
            @Override
            public void success(TracksPager tracksPager, Response response) {
                List<Track> tracks = tracksPager.tracks.items;
                if(tracks.size() > 0){
                    Track track = tracks.get(0);
                    if(albumCoverURL == "")
                        albumCoverURL = track.album.images.get(0).url;
                    setSpotifyID(track.id);
                    songUpdated();
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }
}
