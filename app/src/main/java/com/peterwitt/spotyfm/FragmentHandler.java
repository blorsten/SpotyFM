package com.peterwitt.spotyfm;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class FragmentHandler {
    private static final FragmentHandler ourInstance = new FragmentHandler();
    public AppCompatActivity activity;

    public static FragmentHandler getInstance() {
        return ourInstance;
    }

    public FragmentHandler() {
    }

    public void MakeFragment(int resId, Fragment fragment){
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(resId, fragment);
        ft.commit();
    }
}
