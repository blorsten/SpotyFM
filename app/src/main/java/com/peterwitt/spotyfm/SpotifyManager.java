package com.peterwitt.spotyfm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.peterwitt.spotyfm.RadioAPI.DateTime;
import com.peterwitt.spotyfm.RadioAPI.Song;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.UserPrivate;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit.RetrofitError;

public class SpotifyManager {
    static final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID = "cb0dc3e4eb424c238e8dec78309e37ae";
    private static final String REDIRECT_URI = "com.peterwitt.spotyfm://callback";
    private static final SpotifyManager ourInstance = new SpotifyManager();
    private OkHttpClient _client;
    private String accessToken = "";
    private SpotifyApi api;
    private SpotifyService service;
    private Activity context;
    private long expirationTime;
    private HashMap<String, String> playlists = new HashMap<>();
    private ArrayList<String> playlistNames = new ArrayList<>();
    private String selectedPlaylist;
    private DateTime dateTime = new DateTime();
    private UserPrivate me;

    String getSelectedPlaylist() {
        return selectedPlaylist;
    }

    void setSelectedPlaylist(String selectedPlaylist) {
        this.selectedPlaylist = selectedPlaylist;
        SharedPreferences prefs = context.getSharedPreferences("SpotyFM", Context.MODE_PRIVATE);
        prefs.edit().putString("lastPlaylist", this.selectedPlaylist).apply();
    }

    ArrayList<String> getPlaylistNames() {
        return playlistNames;
    }

    private OkHttpClient getClient(){
        if(_client == null)
            _client = new OkHttpClient();

        return _client;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public static SpotifyManager getInstance() {
        return ourInstance;
    }

    public SpotifyService getService() {
        return service;
    }

    private SpotifyManager() {
    }

    void checkToken(){
        if(accessToken.equals("") ||(!accessToken.equals("") && System.currentTimeMillis()/1000 > expirationTime-60))
            setup(context);
    }

    void setup(Activity context){

        //Setup the manager
        this.context = context;

        //if no access token received request new one
        if(accessToken.equals("") ||(!accessToken.equals("") && System.currentTimeMillis()/1000 > expirationTime-60)) {
            //Build request
            AuthenticationRequest request = new AuthenticationRequest
                    .Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                    .setShowDialog(false)
                    .setScopes(new String[]{
                            "user-read-email",
                            "user-library-modify",
                            "playlist-modify-private",
                            "playlist-modify-public",
                            "playlist-read-private",
                            "user-read-private"})
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

            updatePlaylists();

            service.getMe(new retrofit.Callback<UserPrivate>() {
                @Override
                public void success(UserPrivate userPrivate, retrofit.client.Response response) {
                    me = userPrivate;
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    private void updatePlaylists(){
        service.getMyPlaylists(new retrofit.Callback<Pager<PlaylistSimple>>() {
            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, retrofit.client.Response response) {
                playlistNames = new ArrayList<>();
                playlists = new HashMap<>();

                SharedPreferences prefs = context.getSharedPreferences("SpotyFM", Context.MODE_PRIVATE);
                String lastPlaylist = prefs.getString("lastPlaylist", "");

                playlistNames.add("Library");
                boolean hasOurPlaylist = false;

                for (PlaylistSimple item : playlistSimplePager.items) {
                    if(item.name.equals("SpotyFM")){
                        hasOurPlaylist = true;
                        break;
                    }
                }

                if(lastPlaylist.equals("")){
                    setSelectedPlaylist(hasOurPlaylist ? "SpotyFM" : "Library");
                }else {
                    setSelectedPlaylist(lastPlaylist);
                }

                if(!hasOurPlaylist)
                {
                    playlistNames.add("NEW PLAYLIST: SpotyFM");
                }

                for (PlaylistSimple playlist : playlistSimplePager.items) {
                    playlists.put(playlist.name, playlist.id);
                    playlistNames.add(playlist.name);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("TESTING", "failure: could not get playlists: " + error);
            }
        });
    }

    void updateToken(String token, int expiresIn){
        //save token when its arrived
        accessToken =token;
        expirationTime = System.currentTimeMillis()/1000 + expiresIn;
        //Setup manager again
        setup(context);
    }

    boolean addSongToLibrary(final Song song){

        //Check if the song is found on spotify
        if(song.getSpotifyID().equals(""))
            return false;

        Request request;

        if(selectedPlaylist == null)
            return false;

        if(selectedPlaylist.equals("NEW PLAYLIST: SpotyFM")){
            HashMap<String, Object> body = new HashMap<>();
            body.put("name", "SpotyFM");

            service.createPlaylist(me.id ,body , new retrofit.Callback<Playlist>() {
                @Override
                public void success(Playlist playlist, retrofit.client.Response response) {
                    setSelectedPlaylist(playlist.name);
                    playlists.put(playlist.name, playlist.id);
                    addSongToLibrary(song);
                    updatePlaylists();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("TESTING", "failure: Could not create playlist: " + error);
                }
            });

            return true;
        }
        else if(selectedPlaylist.equals("Library")){

            //Build empty post body
            RequestBody body = new FormBody.Builder()
                    .build();

            //build request
            request = new Request.Builder()
                    .addHeader("Authorization","Bearer " + accessToken)
                    .url("https://api.spotify.com/v1/me/tracks?ids=" + song.getSpotifyID())
                    .put(body)
                    .build();
        }else{

            //Build empty post body
            RequestBody body = new FormBody.Builder()
                    .build();

            //build request
            request = new Request.Builder()
                    .addHeader("Authorization","Bearer " + accessToken)
                    .url("https://api.spotify.com/v1/playlists/"+ playlists.get(selectedPlaylist) + "/tracks?uris=spotify:track:" + song.getSpotifyID())
                    .post(body)
                    .build();
        }

        //post the request
        getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TESTING", "SpotifyOnFailure: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                Log.d("TESTING", "onResponse: " + response.toString());
            }
        });

        return true;
    }
}