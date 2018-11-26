package com.peterwitt.spotyfm.RadioAPI.Callbacks;

import com.peterwitt.spotyfm.RadioAPI.Song;

public interface SongListItemCallback {
    void onSongListItemClicked(Song selected);
}
