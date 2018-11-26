package com.peterwitt.spotyfm.RadioAPI;

import android.util.Log;

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
                url = url.replace("{DAY}", calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()));
                url = url.replace("{HOUR}", calendar.getDisplayName(Calendar.HOUR_OF_DAY,Calendar.LONG, Locale.getDefault()));
                break;

            case RadioPlay:
                Date date = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                url = url.replace("{YYYY:MM:DD", sdf.format(date));
                url = url.replace("HH:MM", "23:59");
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

    private void parseRadioPlay(String response) {
    }

    private void parseJDMedier(String response) {
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

            //If a track is playing right now, add it first in the array
            if(root.getJSONObject("now").getString("status").equals("music")){
                songs = new Song[RESULT_COUNT + 1];
                songs[index++] = new Song(now.getString("track_title"), now.getString("display_artist"));
            }else
                songs = new Song[RESULT_COUNT];

            //Get json ref for previous
            JSONArray previous = root.getJSONArray("previous");
            //Add all previous songs to the array
            for (int i = 0; i < previous.length(); i++) {
                JSONObject jsonObject = previous.getJSONObject(i);
                songs[index++] = new Song(
                        jsonObject.getString("track_title"),
                        jsonObject.getString("display_artist"));
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

    @Override
    public void onWebResponseFailure(String reason) {
        Log.d("DEBUG", "onWebResponseFailure: " + reason);
        apiDataCallback.onRadioAPIDataError();
    }
}
