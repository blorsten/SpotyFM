package com.peterwitt.spotyfm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.peterwitt.spotyfm.RadioAPI.Adapters.SongAdapter;
import com.peterwitt.spotyfm.RadioAPI.Callbacks.SongDataCallback;
import com.peterwitt.spotyfm.RadioAPI.Callbacks.SongListItemCallback;
import com.peterwitt.spotyfm.RadioAPI.RadioAPIManager;
import com.peterwitt.spotyfm.RadioAPI.Song;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SongListFragment extends Fragment implements SongListItemCallback {

    private View fragmentView;
    private SongAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //Inflate son list and add this to current son list fragment
        fragmentView = inflater.inflate(R.layout.song_list_fragment, container, false);
        FragmentHandler.getInstance().setActiveSongFragment(this);
        return fragmentView;
    }

    @Override
    public void onStart() {
        //Setup recyclerView
        RecyclerView rv = fragmentView.findViewById(R.id.song_list_fragment_recycleView);
        adapter = new SongAdapter(this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(fragmentView.getContext()));

        //Setup Title
        TextView t = fragmentView.findViewById(R.id.song_list_fragment_title);
        t.setText(RadioAPIManager.getInstance().getCurrentAPI().getName());
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
    }
}
