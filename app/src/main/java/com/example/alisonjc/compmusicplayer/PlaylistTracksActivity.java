package com.example.alisonjc.compmusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.alisonjc.compmusicplayer.spotify.Item;
import com.example.alisonjc.compmusicplayer.spotify.PlaylistTracksList;
import com.example.alisonjc.compmusicplayer.spotify.SpotifyService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaylistTracksActivity extends Activity {

    private static final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID = "fea06d390d9848c3b5c0ff43bbe0b2d0";

    private static ArrayAdapter<String> mArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_tracks_list);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        String s = b.getString("playlistId");
        String string = b.getString("playlistUri");

        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        ListView listView = (ListView) findViewById(R.id.playlisttracksview);
        listView.setAdapter(mArrayAdapter);
    }

    private void getPlaylistTracks(String token, String userId, String playlistId) {

        getSpotifyService().getPlaylistTracks("Bearer " + token, userId, playlistId).enqueue(new Callback<PlaylistTracksList>() {
            @Override
            public void onResponse(Call<PlaylistTracksList> call, Response<PlaylistTracksList> response) {

                mArrayAdapter.clear();
                for(Item item : response.body().getItems()) {
                    mArrayAdapter.add(item.getName());
                   mArrayAdapter.getCount();
                }
                mArrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<PlaylistTracksList> call, Throwable t) {

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

