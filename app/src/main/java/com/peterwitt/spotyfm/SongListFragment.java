package com.peterwitt.spotyfm;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.peterwitt.spotyfm.RadioAPI.RadioAPIManager;

public class SongListFragment extends Fragment {

    public SongListFragment(){
        //Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        inflater.inflate(R.layout.song_list_fragment, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = view.findViewById(R.id.song_list_fragment_recycleView);


        //Setup list thing

        TextView t = view.findViewById(R.id.song_list_fragment_title);
        t.setText(RadioAPIManager.getInstance().getCurrentAPI().getName());

        super.onViewCreated(view, savedInstanceState);
    }
}
