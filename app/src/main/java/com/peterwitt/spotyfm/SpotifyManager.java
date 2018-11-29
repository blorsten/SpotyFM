package com.peterwitt.spotyfm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

public class SpotifyManager {
    private static final SpotifyManager ourInstance = new SpotifyManager();

    public static SpotifyManager getInstance() {
        return ourInstance;
    }

    public static final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID = "cb0dc3e4eb424c238e8dec78309e37ae";
    private static final String REDIRECT_URI = "com.peterwitt.spotyfm://callback";

    private String accessToken = "";
    private SpotifyApi api;
    private SpotifyService service;
    private Activity context;

    private SpotifyManager() {

    }

    public String getAccessToken() {
        return accessToken;
    }

    public SpotifyService getService() {
        return service;
    }

    public void setup(Activity context){

        this.context = context;
        SharedPreferences prefs = context.getSharedPreferences("SpotyFM", Context.MODE_PRIVATE);
        String storedToken = prefs.getString("apiToken", "");

        if(accessToken.equals("")) {
            //Setup and get the token
            AuthenticationRequest request = new AuthenticationRequest
                    .Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                    .setShowDialog(false)
                    .setScopes(new String[]{"user-read-email", "user-library-modify"})
                    .setCampaign("your-campaign-token")
                    .build();
            AuthenticationClient.openLoginActivity(context, REQUEST_CODE, request);
        }
        else {
            api = new SpotifyApi();
            api.setAccessToken(storedToken);
            service = api.getService();
        }
    }

    public void updateToken(String token){

        accessToken =token;
        SharedPreferences prefs = context.getSharedPreferences("SpotyFM", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("apiToken", accessToken);
        editor.apply();

        setup(context);
    }
}
