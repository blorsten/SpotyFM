package com.peterwitt.spotyfm.RadioAPI;

import com.peterwitt.spotyfm.RadioAPI.Callbacks.RadioAPIDataCallback;
import com.peterwitt.spotyfm.RadioAPI.Callbacks.SongDataCallback;

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
        if(getCurrentAPI().getRecentSongs() == null)
            return  new Song[]{};
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
