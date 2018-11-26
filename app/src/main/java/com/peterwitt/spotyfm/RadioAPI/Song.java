package com.peterwitt.spotyfm.RadioAPI;

import android.util.Log;

import com.peterwitt.spotyfm.RadioAPI.Callbacks.SongDataCallback;
import com.peterwitt.spotyfm.Utilites.WebResponse;
import com.peterwitt.spotyfm.Utilites.WebUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Song {
    public static int IMAGE_SMALL = 250;
    public static int IMAGE_MEDIUM = 500;
    public static int IMAGE_LARGE = 1000;

    private String title = "";
    private String artist = "";
    private String album = "";
    private String albumID = "";
    private String timeStamp = "";

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

    public String getAlbumID() {
        return albumID;
    }

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }

    public Song(String name, String artist){
        this.title = name;
        this.artist = artist;
    }

    public void getData(SongDataCallback callback){
        this.callback = callback;
        loadAlbumID();
    }

    public void songUpdated(){
        callback.SongUpdated(this);
    }

    public String getAlbumConverURL(int imageSize){
        return String.format("https://coverartarchive.org/release-group/%s/front-%s", albumID, imageSize);
    }

    private void loadAlbumID() {
        //get artist id if not already fetched
        if(albumID == "")
            WebUtils.GetURL(String.format("https://musicbrainz.org/ws/2/recording/?query=\"%s\" AND artist:\"%s\" AND status:official AND type:album &fmt=json", title, artist), new WebResponse() {
                @Override
                public void onWebResponse(String response) {
                    try {
                        JSONArray releases = new JSONObject(response).getJSONArray("recordings");
                        if(releases.length() > 0){
                            JSONObject release = releases.getJSONObject(0).getJSONArray("releases").getJSONObject(0);
                            album = release.getString("title");
                            albumID = release.getJSONObject("release-group").getString("id");
                            songUpdated();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onWebResponseFailure(String reason) {
                    Log.d("DEBUG", "onWebResponseFailure: " + reason);
                }
            });
    }
}
