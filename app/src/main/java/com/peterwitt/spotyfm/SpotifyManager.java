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

    /***
     * Get the last selected playlist name
     * @return playlist name
     */
    String getSelectedPlaylist() {
        return selectedPlaylist;
    }

    /***
     * Sets the selected playlist and saves it to the shared prefs
     * @param selectedPlaylist playlist name
     */
    void setSelectedPlaylist(String selectedPlaylist) {
        this.selectedPlaylist = selectedPlaylist;
        SharedPreferences prefs = context.getSharedPreferences("SpotyFM", Context.MODE_PRIVATE);
        prefs.edit().putString("lastPlaylist", this.selectedPlaylist).apply();
    }

    /***
     * Get a list of playlist names
     * @return list of playlist names
     */
    ArrayList<String> getPlaylistNames() {
        return playlistNames;
    }

    /***
     * Get an OkHttp client, creates one if none is created
     * @return OkHttp client
     */
    private OkHttpClient getClient(){
        if(_client == null)
            _client = new OkHttpClient();

        return _client;
    }

    /***
     * Get the time spotify api is searching for
     * @return DateTime of time
     */
    public DateTime getDateTime() {
        return dateTime;
    }

    /***
     * Gets the instance
     * @return
     */
    public static SpotifyManager getInstance() {
        return ourInstance;
    }

    /***
     * Gets the Spotify service
     * @return Spotify service
     */
    public SpotifyService getService() {
        return service;
    }

    /***
     * Constructor for singleton instatiation
     */
    private SpotifyManager() {
    }

    /***
     * Check if the token is valid, if not get a new one
     */
    void checkToken(){
        if(accessToken.equals("") ||(!accessToken.equals("") && System.currentTimeMillis()/1000 > expirationTime-60))
            setup(context);
    }

    /***
     * Sets up the Spotify Manager to have an access token and initializes the Spotify Service
     * @param context the context of the application
     */
    void setup(Activity context){

        //Save context
        this.context = context;

        //if no access token received or the current is expired request new one
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
            //Setup Kaaes API with accesss token
            api = new SpotifyApi();
            api.setAccessToken(accessToken);
            //Get the Spotify service
            service = api.getService();

            //Get the users playlists
            updatePlaylists();

            //Get the Spotify user
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

    /***
     * Updates the local representation of the users playlists
     */
    private void updatePlaylists(){

        //Get the users plalists from Spotify
        service.getMyPlaylists(new retrofit.Callback<Pager<PlaylistSimple>>() {
            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, retrofit.client.Response response) {
                //Instatiate the local lists
                playlistNames = new ArrayList<>();
                playlists = new HashMap<>();

                //Check if a last playlist is saved
                SharedPreferences prefs = context.getSharedPreferences("SpotyFM", Context.MODE_PRIVATE);
                String lastPlaylist = prefs.getString("lastPlaylist", "");

                //Add the "Library" playlist to the list
                playlistNames.add("Library");

                //Check if the "SpotyFM" playlist exists on the user account
                boolean hasOurPlaylist = false;
                for (PlaylistSimple item : playlistSimplePager.items) {
                    if(item.name.equals("SpotyFM")){
                        hasOurPlaylist = true;
                        break;
                    }
                }

                //Decide what playlist should be the default if none is saved from previously
                if(lastPlaylist.equals("")){
                    setSelectedPlaylist(hasOurPlaylist ? "SpotyFM" : "Library");
                }else {
                    setSelectedPlaylist(lastPlaylist);
                }

                //Add the option to make new playlist if not already on there
                if(!hasOurPlaylist)
                {
                    playlistNames.add("NEW PLAYLIST: SpotyFM");
                }

                //Save the playlist data to the hashmap for later use
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

    /***
     * Updates the token when response is available from the MainActivity
     * @param token The new token
     * @param expiresIn time until the token expires
     */
    void updateToken(String token, int expiresIn){
        //save token when its arrived
        accessToken =token;
        expirationTime = System.currentTimeMillis()/1000 + expiresIn;
        //Setup manager again
        setup(context);
    }

    /***
     * Adds the song to the users selected playlist
     * @param song the song to add
     * @return whether the song could be added or not
     */
    boolean addSongToLibrary(final Song song){

        //Check if the song is found on spotify
        if(song.getSpotifyID().equals(""))
            return false;

        //Check if a playlist is selected
        if(selectedPlaylist == null)
            return false;

        Request request;

        //If the user wants to add a "SpotyFM" playlist
        if(selectedPlaylist.equals("NEW PLAYLIST: SpotyFM")){

            //Create the body of the request
            HashMap<String, Object> body = new HashMap<>();
            body.put("name", "SpotyFM");

            //Send request to create a playlist
            service.createPlaylist(me.id ,body , new retrofit.Callback<Playlist>() {
                @Override
                public void success(Playlist playlist, retrofit.client.Response response) {
                    //Update the local selected playlist
                    setSelectedPlaylist(playlist.name);
                    playlists.put(playlist.name, playlist.id);

                    //Add song
                    addSongToLibrary(song);
                    //Update the local playlists to match the online ones
                    updatePlaylists();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("TESTING", "failure: Could not create playlist: " + error);
                }
            });

            //The song could be added
            return true;
        }
        //If the user wants to add to "Library"
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
        }
        //The user has selected a normal playlist
        else{

            //Build empty post body
            RequestBody body = new FormBody.Builder()
                    .build();

            //build request
            request = new Request.Builder()
                    .addHeader("Authorization","Bearer " + accessToken)

                    .url("https://api.spotify.com/v1/playlists/"+
                            playlists.get(selectedPlaylist) +
                            "/tracks?uris=spotify:track:" +
                            song.getSpotifyID())

                    .post(body)
                    .build();
        }

        //send the request
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

        //The song  could be added
        return true;
    }
}