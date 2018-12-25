package com.peterwitt.spotyfm.RadioAPI;

import android.util.Log;

import com.peterwitt.spotyfm.FragmentHandler;
import com.peterwitt.spotyfm.RadioAPI.Callbacks.RadioAPIDataCallback;
import com.peterwitt.spotyfm.RadioAPI.Callbacks.SongDataCallback;

public class RadioAPIManager implements RadioAPIDataCallback, SongDataCallback {

    private static final RadioAPIManager ourInstance = new RadioAPIManager();
    private RadioAPI currentAPI;
    private Song lastSelectedSong;

    public static RadioAPIManager getInstance() {
        return ourInstance;
    }

    public RadioAPI getCurrentAPI() {
        return currentAPI;
    }

    public Song getLastSelectedSong() {
        return lastSelectedSong;
    }

    public void setLastSelectedSong(Song lastSelectedSong) {
        this.lastSelectedSong = lastSelectedSong;
    }

    public Song[] getRecentSongs(){
        if(getCurrentAPI() == null)
            return new Song[]{};

        if(getCurrentAPI().getRecentSongs() == null)
            return  new Song[]{};
        return getCurrentAPI().getRecentSongs();
    }

    public void setCurrentAPI(RadioAPI currentAPI) {
        this.currentAPI = currentAPI;
        currentAPI.setup(this, this);
        currentAPI.getRecentlyPlayed();
    }

    private RadioAPIManager() {
    }

    public void refreshCurrentAPI(){
        if(currentAPI != null)
            currentAPI.getRecentlyPlayed();
    }

    @Override
    public void onRadioAPIDataFetched() {
        FragmentHandler.getInstance().getActiveSongFragment().SongUpdated();
    }

    @Override
    public void onRadioAPIDataError() {
        Log.d("TESTING", "onRadioAPIDataError");
    }

    @Override
    public void SongUpdated(Song song) {
        if(FragmentHandler.getInstance().getActiveSongFragment() != null){
            FragmentHandler.getInstance().getActiveSongFragment().SongUpdated();
        }
    }
}
