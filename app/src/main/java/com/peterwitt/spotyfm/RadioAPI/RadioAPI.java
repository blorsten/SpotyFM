package com.peterwitt.spotyfm.RadioAPI;

import android.util.Log;

import com.peterwitt.spotyfm.RadioAPI.Callbacks.RadioAPIDataCallback;
import com.peterwitt.spotyfm.RadioAPI.Callbacks.SongDataCallback;
import com.peterwitt.spotyfm.SpotifyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RadioAPI {
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

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
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

    /***
     * Gets the recently played depending on the type
     */
    void getRecentlyPlayed(){

        //Start replacing the URL
        String tempUrl = url.replace("{CID}", cid);
        DateTime dateTime = SpotifyManager.getInstance().getDateTime();

        switch (apiType){
            //Day and hour type
            case JFMedier:
                String day = dateTime.getNameOfDay();
                int hour = dateTime.hour;

                tempUrl = tempUrl.replace("{DAY}", day);
                tempUrl = tempUrl.replace("{HOUR}", hour + "");
                break;

            //Timestamp type
            case RadioPlay:
                tempUrl = tempUrl.replace("{YYYY-MM-DD}", dateTime.getCurrentDate());
                tempUrl = tempUrl.replace("{HH:MM}", dateTime.getCurrentTime());

            //Count sould be set
            case DR:
                tempUrl = tempUrl.replace("{COUNT}", String.valueOf(RESULT_COUNT));
        }

        //Create OkHttp client and build request
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(tempUrl)
                .build();

        //Set callback to return the response as a string when done
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                Log.d("TESTING", "onWebResponseFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                switch (apiType){
                    case DR:
                        parseDR(response.body().string());
                        break;
                    case RadioPlay:
                        parseRadioPlay(response.body().string());
                        break;
                    case JFMedier:
                        parseJFMedier(response.body().string());
                        break;
                }
            }
        });
    }

    private void parseRadioPlay(String response) {

        try {
            //Get json root ref
            JSONArray root = new JSONArray(response);

            //Make song array for the songs
            int size = root.length();
            Song[] songs = new Song[size];
            int index = 0;

            //Add all songs to the array
            for (int i = 0; i < root.length(); i++) {
                JSONObject jsonObject = root.getJSONObject(i);
                Song song = new Song(jsonObject.getString("nowPlayingTrack"),jsonObject.getString("nowPlayingArtist"));
                song.setTimeStamp(jsonObject.getString("nowPlayingTime").substring(11,16));
                songs[index++] = song;
                song.fetchData(songDataCallback);
            }

            recentSongs = songs;
            apiDataCallback.onRadioAPIDataFetched();

        } catch (JSONException e){
            e.printStackTrace();
            apiDataCallback.onRadioAPIDataError();
        }
    }

    private void parseJFMedier(String response) {

        try {
            //Get json root ref
            JSONArray root = new JSONArray(response);

            //Make song array for the songs
            int size = root.length();
            Song[] songs = new Song[size];
            int index = 0;

            //Add all songs to the array
            for (int i = 0; i < root.length(); i++) {
                JSONObject jsonObject = root.getJSONObject(i);
                Song song = new Song(jsonObject.getString("title"),jsonObject.getString("artist"));
                song.setTimeStamp(jsonObject.getString("time"));
                songs[index++] = song;
            }

            //reverse the order of the songs
            for(int i = 0; i < songs.length / 2; i++)
            {
                Song temp = songs[i];
                songs[i] = songs[songs.length - i - 1];
                songs[songs.length - i - 1] = temp;
            }

            //Re encode the strings to utf-8 to support all characters
            for (Song song : songs) {
                if(cid.equals("classic")){
                    song.setTitle(new String(song.getTitle().getBytes("Windows-1252"), "UTF-8"));
                    song.setArtist(new String(song.getArtist().getBytes("Windows-1252"), "UTF-8"));
                }
                //Set callback to be RadioAPIManager
                song.fetchData(songDataCallback);
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

            //Get json ref for previous
            JSONArray previous = root.getJSONArray("previous");
            //get size of previous songs
            int size = previous.length();

            //If a track is playing right now, add it first in the array
            if(root.getJSONObject("now").getString("status").equals("music")){
                //Create array with previous size plus one
                songs = new Song[size + 1];
                Song song = new Song(now.getString("track_title"), now.getString("display_artist"));
                song.setTimeStamp(now.getString("start_time").substring(11,16));
                songs[index++] = song;
            }else {
                //Create array with previous size
                songs = new Song[size];
            }

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
                song.fetchData(songDataCallback);
            }

            recentSongs = songs;
            apiDataCallback.onRadioAPIDataFetched();

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            apiDataCallback.onRadioAPIDataError();
        }
    }
}
