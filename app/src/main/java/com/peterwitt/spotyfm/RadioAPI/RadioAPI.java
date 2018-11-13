package com.peterwitt.spotyfm.RadioAPI;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

public class RadioAPI {
    public ParseMode mode;
    public UUID uuid;
    public String screenName;
    private String url;
    private Song[] currentPlaying;

    public Song[] getCurrentPlaying(){
        //Do some stuff to get current playing
        return currentPlaying;
    }
}
