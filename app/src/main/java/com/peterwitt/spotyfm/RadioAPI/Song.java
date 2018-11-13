package com.peterwitt.spotyfm.RadioAPI;

import com.peterwitt.spotyfm.Utilites.WebResponse;
import com.peterwitt.spotyfm.Utilites.WebUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Song {
    public static int IMAGE_SMALL = 250;
    public static int IMAGE_MEDIUM = 500;
    public static int IMAGE_LARGE = 1000;

    private String name = "";
    private String artist = "";
    private String album = "";
    private String albumID = "";

    private SongDataCallback callback;

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getAlbumID() {
        return albumID;
    }

    public Song(String name, String artist){
        this.name = name;
        this.artist = artist;
    }

    public void getData(SongDataCallback callback){
        this.callback = callback;
        loadAlbumID();
    }

    public void ready(){
        callback.SongUpdated(this);
    }

    public String getAlbumConverURL(int imageSize){
        return String.format("https://coverartarchive.org/release-group/%s/front-%s", albumID, imageSize);
    }

    private void loadAlbumID() {
        //get artist id if not already fetched
        if(albumID == "")
            WebUtils.GetURL(String.format("https://musicbrainz.org/ws/2/recording/?query=\"%s\" AND artist:\"%s\" AND status:official AND type:album &fmt=json", name, artist), new WebResponse() {
                @Override
                public void onWebResponse(String response) {
                    try {
                        JSONObject release = new JSONObject(response).getJSONArray("recordings").getJSONObject(0).getJSONArray("releases").getJSONObject(0);
                        album = release.getString("title");
                        albumID = release.getJSONObject("release-group").getString("id");
                        ready();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onWebResponseFailue(String reason) {

                }
            });
    }
}
