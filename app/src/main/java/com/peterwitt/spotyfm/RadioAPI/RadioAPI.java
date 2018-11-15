package com.peterwitt.spotyfm.RadioAPI;

import android.util.Log;

import com.peterwitt.spotyfm.Utilites.WebResponse;
import com.peterwitt.spotyfm.Utilites.WebUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import javax.annotation.WillCloseWhenClosed;

public class RadioAPI implements WebResponse {
    private String mode;
    private String name;
    private String url;
    private String cid;
    private APIType type;

    public String getMode() {
        return mode;
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

    public RadioAPI(){
        Log.d("RadioAPI", String.format("RadioAPI: %s, %s, %s, %s", name, mode, cid, url));
    }

    public void Setup(){
        type = APIType.valueOf(mode);
    }

    public void getRecentyPlayed(){
        WebUtils.GetURL(url, this);
    }

    @Override
    public void onWebResponse(String response) {
        try {
            JSONObject root = new JSONObject(response);
            JSONObject now = root.getJSONObject("now");

            String title;
            String artist;

            if(root.getJSONObject("now").getString("status").equals("music")){
                title = now.getString("track_title");
                artist =now.getString("display_artist");
            }
            else{
                JSONArray previous = root.getJSONArray("previous");
                JSONObject[] previousTracks = new JSONObject[previous.length()];

                for (int i = 0; i < previous.length(); i++) {
                    previousTracks[i] = previous.optJSONObject(i);
                }

                artist = previousTracks[0].getString("display_artist");
                title = previousTracks[0].getString("track_title");
            }

            title = new String(title.getBytes("Windows-1252"), "UTF-8");
            artist = new String(artist.getBytes("Windows-1252"), "UTF-8");

            final String testTitle = title;
            final String testArtist = artist;

            Song song = new Song(title, artist);
            song.getData(new SongDataCallback() {
                @Override
                public void SongUpdated(Song song) {

                }
            });
            //Update list or something

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebResponseFailure(String reason) {

    }
}
