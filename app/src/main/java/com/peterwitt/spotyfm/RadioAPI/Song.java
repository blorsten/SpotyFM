package com.peterwitt.spotyfm.RadioAPI;

import com.peterwitt.spotyfm.RadioAPI.Callbacks.SongDataCallback;
import com.peterwitt.spotyfm.SpotifyManager;

import java.util.ArrayList;
import java.util.List;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Song {

    private String title = "";
    private String artist = "";
    private String timeStamp = "";
    private String albumCoverURL = "";
    private String spotifyID = "";
    private String searchQuery = "";
    private boolean isUpdated = false;

    private ArrayList<SongDataCallback> callbacks = new ArrayList<>();

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

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

    public String getSpotifyID() {
        return spotifyID;
    }

    public void setAlbumCoverURL(String albumCoverURL) {
        this.albumCoverURL = albumCoverURL;
    }

    public String getAlbumConverURL(){
        return albumCoverURL;
    }

    public boolean getIsUpdated(){
        return isUpdated;
    }

    public Song(String name, String artist){
        this.title = name;
        this.artist = artist;
    }

    private void songUpdated(){
        for (SongDataCallback callback : callbacks) {
            callback.SongUpdated(this);
        }
    }

    public void fetchData(SongDataCallback callback){
        callbacks.add(callback);
        loadInfo();
    }

    public void subscribe(SongDataCallback callback){
        callbacks.add(callback);
    }

    private void loadInfo() {

        //remove characters that do not work with spotify
        if(searchQuery.equals(""))
            searchQuery = getTitle() + " " + getArtist();
        String regex = "feat\\.|&|ft\\.|\\(([^)]+)\\)|\\[([^)]+)\\]";
        searchQuery = searchQuery.replaceAll(regex, "");
        searchQuery = searchQuery.replaceAll(" and ", " ");
        searchQuery = searchQuery.replaceAll("S!vas", "Sivas");
        searchQuery = searchQuery.replaceAll("Stewie Wonder", "Stevie Wonder");

        SpotifyManager.getInstance().getService().searchTracks(searchQuery, new Callback<TracksPager>() {
            @Override
            public void success(TracksPager tracksPager, Response response) {
                List<Track> tracks = tracksPager.tracks.items;
                if(tracks.size() > 0){
                    Track track = tracks.get(0);
                    if(albumCoverURL.equals(""))
                        albumCoverURL = track.album.images.get(0).url;
                    spotifyID = track.id;
                    isUpdated = true;
                    songUpdated();
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }
}