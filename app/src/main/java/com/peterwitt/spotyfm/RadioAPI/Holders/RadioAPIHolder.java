package com.peterwitt.spotyfm.RadioAPI.Holders;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.peterwitt.spotyfm.R;

public class RadioAPIHolder extends RecyclerView.ViewHolder {
    private Button itemButton;

    public Button getItemButton() {
        return itemButton;
    }

    public RadioAPIHolder(View itemView) {
        super(itemView);
        itemButton = itemView.findViewById(R.id.radio_station_list_item_name);
    }
}
