package com.peterwitt.spotyfm;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class FragmentHandler {
    private static final FragmentHandler ourInstance = new FragmentHandler();
    public AppCompatActivity activity;

    private SongListFragment activeSongFragment;

    public SongListFragment getActiveSongFragment() {
        return activeSongFragment;
    }

    public void setActiveSongFragment(SongListFragment activeSongFragment) {
        this.activeSongFragment = activeSongFragment;
    }

    public static FragmentHandler getInstance() {
        return ourInstance;
    }

    public FragmentHandler() {
    }

    public void MakeFragment(int resId, Fragment fragment){
        MakeFragment(resId, fragment, false);
    }

    public void MakeFragment(int resId, Fragment fragment, Boolean clean){
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(resId, fragment);
        ft.commit();
    }
}
