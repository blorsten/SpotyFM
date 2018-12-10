package com.peterwitt.spotyfm.RadioAPI.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.peterwitt.spotyfm.R;

public class RadioAPIHolder extends RecyclerView.ViewHolder {
    public Button itemButton;

    public RadioAPIHolder(View itemView) {
        super(itemView);
        itemButton = itemView.findViewById(R.id.radio_station_list_item_name);
    }
}
