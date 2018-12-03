package com.peterwitt.spotyfm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.peterwitt.spotyfm.RadioAPI.Song;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.io.IOException;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SpotifyManager {

    public static final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID = "cb0dc3e4eb424c238e8dec78309e37ae";
    private static final String REDIRECT_URI = "com.peterwitt.spotyfm://callback";
    private static final SpotifyManager ourInstance = new SpotifyManager();
    private OkHttpClient _client;
    private String accessToken = "";
    private SpotifyApi api;
    private SpotifyService service;
    private Activity context;
    private long expirationTime;

    public static SpotifyManager getInstance() {
        return ourInstance;
    }

    private OkHttpClient getClient(){
        if(_client == null)
            _client = new OkHttpClient();

        return _client;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public SpotifyService getService() {
        return service;
    }

    private SpotifyManager() {
    }

    public void setup(Activity context){
        //Setup the manager
        this.context = context;

        //if no access token received request new one
        if(accessToken.equals("") ||(!accessToken.equals("") && System.currentTimeMillis()/1000 > expirationTime-60)) {
            //Build request
            AuthenticationRequest request = new AuthenticationRequest
                    .Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                    .setShowDialog(false)
                    .setScopes(new String[]{"user-read-email", "user-library-modify"})
                    .setCampaign("your-campaign-token")
                    .build();

            //prompt user for access
            AuthenticationClient.openLoginActivity(context, REQUEST_CODE, request);
        }
        else {
            //Setup API
            api = new SpotifyApi();
            api.setAccessToken(accessToken);
            service = api.getService();
        }
    }

    public void updateToken(String token, int expiresIn){
        //save token when its arrived
        accessToken =token;
        expirationTime = System.currentTimeMillis()/1000 + expiresIn;
        //Setup manager again
        setup(context);
    }

    public boolean addSongToLibrary(Song song){

        //Check if the song is found on spotify
        if(song.getSpotifyID().equals(""))
            return false;

        //Build empty post body
        RequestBody body = new FormBody.Builder()
                .build();

        //build request
        Request request = new Request.Builder()
                .addHeader("Authorization","Bearer " + SpotifyManager.getInstance().getAccessToken())
                .url("https://api.spotify.com/v1/me/tracks?ids=" + song.getSpotifyID())
                .put(body)
                .build();

        //post the request
        getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TESTING", "SpotifyOnFailure: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("TESTING", "onResponse: " + response.toString());
            }
        });

        return true;
    }
}