package com.example.alisonjc.compmusicplayer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.alisonjc.compmusicplayer.spotify.Item;
import com.example.alisonjc.compmusicplayer.spotify.SpotifyService;
import com.spotify.sdk.android.player.Player;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaylistTracksActivity extends Activity {

    private static final int REQUEST_CODE = 1337;
    //private static final String REDIRECT_URI = "comp-music-player-login://callback";
    private static final String CLIENT_ID = "fea06d390d9848c3b5c0ff43bbe0b2d0";
    private Player mPlayer;

    private static ArrayAdapter<String> mArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_tracks_list);

        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        ListView listView = (ListView) findViewById(R.id.playlisttracksview);
        listView.setAdapter(mArrayAdapter);
    }

    private void getPlaylistTracks(String token, String userId, String playlistId) {

        getSpotifyService().getPlaylistTracks("Bearer " + token, userId, playlistId).enqueue(new Callback<PlaylistTracksActivity>() {
            @Override
            public void onResponse(Call<PlaylistTracksActivity> call, Response<PlaylistTracksActivity> response) {

                mArrayAdapter.clear();
                for(Item item : response.body().getItems()) {
                    mArrayAdapter.add(item.getName());
                   mArrayAdapter.getCount();
                }
                mArrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<PlaylistTracksActivity> call, Throwable t) {

            }
        });

    }

    private SpotifyService getSpotifyService() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        return retrofit.create(SpotifyService.class);

    }


}

