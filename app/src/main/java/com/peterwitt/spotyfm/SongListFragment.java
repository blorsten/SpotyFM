package com.peterwitt.spotyfm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.peterwitt.spotyfm.RadioAPI.Adapters.SongAdapter;
import com.peterwitt.spotyfm.RadioAPI.Callbacks.SongListItemCallback;
import com.peterwitt.spotyfm.RadioAPI.RadioAPIManager;
import com.peterwitt.spotyfm.RadioAPI.Song;

public class SongListFragment extends Fragment implements SongListItemCallback {

    private View fragmentView;
    private SongAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //Inflate son list and add this to current son list fragment
        fragmentView = inflater.inflate(R.layout.song_list_fragment, container, false);
        FragmentHandler.getInstance().setActiveSongFragment(this);

        swipeRefreshLayout = fragmentView.findViewById(R.id.song_list_fragment_swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RadioAPIManager.getInstance().refreshCurrentAPI();
            }
        });

        return fragmentView;
    }

    @Override
    public void onStart() {
        //Setup recyclerView
        SpotifyManager.getInstance().checkToken();
        RecyclerView rv = fragmentView.findViewById(R.id.song_list_fragment_recycleView);
        adapter = new SongAdapter(this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(fragmentView.getContext()));

        MainActivity.instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.instance.setTitle(RadioAPIManager.getInstance().getCurrentAPI().getName());
            }
        });
        super.onStart();
    }

    @Override
    public void onSongListItemClicked(Song selected) {
        //Add song to users library
        RadioAPIManager.getInstance().setLastSelectedSong(selected);
        FragmentHandler.getInstance().MakeFragment(R.id.main_activity_frame_layout, new SongInfoFragment());
    }

    public void SongUpdated() {
        if(getActivity() != null)
            swipeRefreshLayout.setRefreshing(false);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
    }
}