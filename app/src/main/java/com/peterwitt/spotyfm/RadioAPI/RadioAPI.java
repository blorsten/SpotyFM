package com.peterwitt.spotyfm.RadioAPI;

import android.util.Log;

import com.google.gson.JsonArray;
import com.peterwitt.spotyfm.RadioAPI.Callbacks.RadioAPIDataCallback;
import com.peterwitt.spotyfm.RadioAPI.Callbacks.SongDataCallback;
import com.peterwitt.spotyfm.Utilites.WebResponse;
import com.peterwitt.spotyfm.Utilites.WebUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RadioAPI implements WebResponse {
    private final int RESULT_COUNT = 10;

    private String type;
    private String name;
    private String url;
    private String cid;
    private APIType apiType;

    private Song[] recentSongs;

    private RadioAPIDataCallback apiDataCallback;
    private SongDataCallback songDataCallback;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getCid() {
        return cid;
    }

    Song[] getRecentSongs() {
        return recentSongs;
    }

    public RadioAPI(){
    }

    void setup(RadioAPIDataCallback apiDataCallback, SongDataCallback songDataCallback){

        this.apiDataCallback = apiDataCallback;
        this.songDataCallback = songDataCallback;
        apiType = APIType.valueOf(type);
    }

    void getRecentlyPlayed(){

        url = url.replace("{CID}", cid);
        Calendar calendar = Calendar.getInstance();

        switch (apiType){
            case JFMedier:
                String day = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                url = url.replace("{DAY}", day);
                url = url.replace("{HOUR}", hour + "");
                break;

            case RadioPlay:
                Date date = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                url = url.replace("{YYYY-MM-DD}", sdf.format(date));
                url = url.replace("{HH:MM}", "23:59");
                //url = url.replace("HH:MM", DateFormat.getTimeInstance(DateFormat.SHORT).format(date));|

            case DR:
                url = url.replace("{COUNT}", String.valueOf(RESULT_COUNT));
        }

        WebUtils.GetURL(url, this);
    }

    @Override
    public void onWebResponse(String response) {

        switch (apiType){

            case DR:
                parseDR(response);
                break;
            case RadioPlay:
                parseRadioPlay(response);
                break;
            case JFMedier:
                parseJDMedier(response);
                break;
        }
    }

    @Override
    public void onWebResponseFailure(String reason) {
        Log.d("DEBUG", "onWebResponseFailure: " + reason);
        apiDataCallback.onRadioAPIDataError();
    }

    private void parseRadioPlay(String response) {

        try {
            //Get json root ref
            JSONArray root = new JSONArray(response);

            //Make song array for the songs
            int size = root.length();
            Song[] songs = new Song[size];
            int index = 0;

            //Add all previous songs to the array
            for (int i = 0; i < root.length(); i++) {
                JSONObject jsonObject = root.getJSONObject(i);
                Song song = new Song(jsonObject.getString("nowPlayingTrack"),jsonObject.getString("nowPlayingArtist"));
                song.setTimeStamp(jsonObject.getString("nowPlayingTime").substring(11,16));
                song.setAlbumCoverURL(jsonObject.getString("nowPlayingImage"));
                songs[index++] = song;
            }

            //Re encode the strings to utf-8 to support all characters
            for (Song song : songs) {
                song.setTitle(new String(song.getTitle().getBytes("Windows-1252"), "UTF-8"));
                song.setArtist(new String(song.getArtist().getBytes("Windows-1252"), "UTF-8"));
                //Set callback to be RadioAPIManager
                song.getData(songDataCallback);
            }

            recentSongs = songs;
            apiDataCallback.onRadioAPIDataFetched();

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            apiDataCallback.onRadioAPIDataError();
        }
    }

    private void parseJDMedier(String response) {

        try {
            //Get json root ref
            JSONArray root = new JSONArray(response);

            //Make song array for the songs
            int size = root.length();
            Song[] songs = new Song[size];
            int index = 0;

            //Add all previous songs to the array
            for (int i = 0; i < root.length(); i++) {
                JSONObject jsonObject = root.getJSONObject(i);
                Song song = new Song(jsonObject.getString("title"),jsonObject.getString("artist"));
                song.setTimeStamp(jsonObject.getString("time"));
                songs[index++] = song;
            }

            //Re encode the strings to utf-8 to support all characters
            for (Song song : songs) {
                song.setTitle(new String(song.getTitle().getBytes("Windows-1252"), "UTF-8"));
                song.setArtist(new String(song.getArtist().getBytes("Windows-1252"), "UTF-8"));
                //Set callback to be RadioAPIManager
                song.getData(songDataCallback);
            }

            recentSongs = songs;
            apiDataCallback.onRadioAPIDataFetched();

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            apiDataCallback.onRadioAPIDataError();
        }
    }

    private void parseDR(String response) {

        try {
            //Get json root ref
            JSONObject root = new JSONObject(response);
            //Get json ref for now object
            JSONObject now = root.getJSONObject("now");

            //Make song array for the songs
            Song[] songs;
            int index = 0;

            JSONArray previous = root.getJSONArray("previous");
            int size = previous.length();

            //If a track is playing right now, add it first in the array
            if(root.getJSONObject("now").getString("status").equals("music")){
                songs = new Song[size + 1];
                Song song = new Song(now.getString("track_title"), now.getString("display_artist"));
                song.setTimeStamp(now.getString("start_time").substring(11,16));
                songs[index++] = song;
            }else {
                songs = new Song[size];
            }

            //Get json ref for previous
            //Add all previous songs to the array
            for (int i = 0; i < previous.length(); i++) {
                JSONObject jsonObject = previous.getJSONObject(i);
                Song song = new Song(jsonObject.getString("track_title"),jsonObject.getString("display_artist"));
                song.setTimeStamp(jsonObject.getString("start_time").substring(11,16));
                songs[index++] = song;
            }

            //Re encode the strings to utf-8 to support all characters
            for (Song song : songs) {
                song.setTitle(new String(song.getTitle().getBytes("Windows-1252"), "UTF-8"));
                song.setArtist(new String(song.getArtist().getBytes("Windows-1252"), "UTF-8"));
                //Set callback to be RadioAPIManager
                song.getData(songDataCallback);
            }

            recentSongs = songs;
            apiDataCallback.onRadioAPIDataFetched();

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            apiDataCallback.onRadioAPIDataError();
        }
    }
}
