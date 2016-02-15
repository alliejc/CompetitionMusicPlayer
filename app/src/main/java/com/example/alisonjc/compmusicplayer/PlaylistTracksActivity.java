package com.example.alisonjc.compmusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.alisonjc.compmusicplayer.spotify.SpotifyService;
import com.example.alisonjc.compmusicplayer.spotify.tracklist.Item;
import com.example.alisonjc.compmusicplayer.spotify.tracklist.PlaylistTracksList;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaylistTracksActivity extends Activity implements PlayerNotificationCallback {

    private static final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID = "fea06d390d9848c3b5c0ff43bbe0b2d0";
    private String token = "";
    //public String playlistUri = "";
    private String playlistId = "";
    private String userId = "";
    private Player mPlayer;
    private List<Item> mItems;

    private static ArrayAdapter<String> mArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_tracks_list);

        Intent intent = getIntent();
        final Bundle b = intent.getExtras();
        token = b.getString("spotifyToken");
        //playlistUri = b.getString("playlistUri");
        playlistId = b.getString("playlistId");
        userId = b.getString("userId");



        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        final ListView listView = (ListView) findViewById(R.id.playlisttracksview);
        listView.setAdapter(mArrayAdapter);

        getPlaylistTracks(token, userId, playlistId);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playSong(mItems.get(position).getTrack().getId());

            }
        });
    }

    private void playSong(final String songId) {

        final Config playerConfig = new Config(getApplicationContext(), token, CLIENT_ID);


        mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {

                    @Override
                    public void onInitialized(Player player) {
                        mPlayer.addConnectionStateCallback(player);
                        mPlayer.play("spotify:track:" + songId);

                    }


                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                }
        );
        mPlayer.isInitialized();

    }



    public void getPlaylistTracks(String token, String userId, String playlistId) {


        getSpotifyService().getPlaylistTracks("Bearer " + token, userId, playlistId).enqueue(new Callback<PlaylistTracksList>() {
            @Override
            public void onResponse(Call<PlaylistTracksList> call, Response<PlaylistTracksList> response) {


                mArrayAdapter.clear();
                if(response.body() != null) {
                    mItems = response.body().getItems();
                    for (Item item : response.body().getItems()) {

                        if (item.getTrack().getName() != null) {
                            mArrayAdapter.add(item.getTrack().getName());
                        }
                    }
                }

                mArrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<PlaylistTracksList> call, Throwable t) {

            }
        });


    }




    private SpotifyService getSpotifyService() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();


        return retrofit.create(SpotifyService.class);

    }


    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {

    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {

    }
}

