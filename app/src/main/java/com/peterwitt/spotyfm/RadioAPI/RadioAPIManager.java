package com.peterwitt.spotyfm.RadioAPI;

import android.widget.Toast;

public class RadioAPIManager implements RadioAPIDataCallback, SongDataCallback {
    private static final RadioAPIManager ourInstance = new RadioAPIManager();
    private RadioAPI currentAPI;

    public static RadioAPIManager getInstance() {
        return ourInstance;
    }

    public RadioAPI getCurrentAPI() {
        return currentAPI;
    }

    public void setCurrentAPI(RadioAPI currentAPI) {
        this.currentAPI = currentAPI;
        currentAPI.setup(this, this);
        currentAPI.getRecentlyPlayed();
    }

    public void refreshCurrentAPI(){
        currentAPI.getRecentlyPlayed();
    }

    public Song[] getRecentSongs(){
        return getCurrentAPI().getRecentSongs();
    }

    private RadioAPIManager() {
    }

    @Override
    public void onRadioAPIDataFetched() {

    }

    @Override
    public void onRadioAPIDataError() {

    }

    @Override
    public void SongUpdated(Song song) {

    }
}
