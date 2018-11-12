package com.peterwitt.spotyfm.RadioAPI;

import com.squareup.picasso.Picasso;

public class RadioAPI {
    private RadioAPI instance;

    public RadioAPI getInstance() {
        if(instance == null)
            instance = this;

        return instance;
    }

    public void FetchRadioData(String url, ParseMode mode){

    }
}
