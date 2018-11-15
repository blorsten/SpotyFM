package com.peterwitt.spotyfm.RadioAPI;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.peterwitt.spotyfm.R;

public class RadioAPIHolder extends RecyclerView.ViewHolder {
    private TextView itemName;

    public TextView getItemName() {
        return itemName;
    }

    public void setItemName(TextView itemName) {
        this.itemName = itemName;
    }

    public RadioAPIHolder(View itemView) {
        super(itemView);
        itemName = itemView.findViewById(R.id.radio_station_list_item_name);
    }
}
