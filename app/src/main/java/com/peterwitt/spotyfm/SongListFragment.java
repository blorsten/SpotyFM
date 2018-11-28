package com.peterwitt.spotyfm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class SongListFragment extends Fragment implements SongListItemCallback {

    private View fragmentView;
    private SongAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.song_list_fragment, container, false);
        FragmentHandler.getInstance().setActiveSongFragment(this);
        return fragmentView;
    }

    @Override
    public void onStart() {
        RecyclerView rv = fragmentView.findViewById(R.id.song_list_fragment_recycleView);
        adapter = new SongAdapter(this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(fragmentView.getContext()));

        TextView t = fragmentView.findViewById(R.id.song_list_fragment_title);
        t.setText(RadioAPIManager.getInstance().getCurrentAPI().getName());
        super.onStart();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSongListItemClicked(Song selected) {
        Toast.makeText(fragmentView.getContext(), selected.getTitle(), Toast.LENGTH_SHORT).show();
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
