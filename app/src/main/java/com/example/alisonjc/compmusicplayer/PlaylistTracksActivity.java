package com.example.alisonjc.compmusicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

public class PlaylistTracksActivity extends AppCompatActivity implements PlayerNotificationCallback {

    private static final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID = "fea06d390d9848c3b5c0ff43bbe0b2d0";
    private String token = "";
    private String playlistId = "";
    private String userId = "";
    private Player mPlayer;
    private List<Item> mItems;
    private Integer mTrackLength;
    private int itemPosition = 0;

    private static ArrayAdapter<String> mArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_tracks_list);

        Intent intent = getIntent();
        final Bundle b = intent.getExtras();
        token = b.getString("spotifyToken");
        playlistId = b.getString("playlistId");
        userId = b.getString("ownerId");

        toolbarSetup();



        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        final ListView listView = (ListView) findViewById(R.id.playlisttracksview);
        listView.setAdapter(mArrayAdapter);

        getPlaylistTracks(token, userId, playlistId);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCurrentPlayingSong(position);
                playSong(mItems.get(position).getTrack().getId());
                //mTrackLength = mItems.get(position).getTrack().getDurationMs();
                //endSong();

            }
        });

    }

    private void setCurrentPlayingSong(int itemPosition){
        getSupportActionBar().setSubtitle(mItems.get(itemPosition).getTrack().getName());
        this.itemPosition = itemPosition;

    }

    private int getCurrenPlayingSongLocation() {
        getSupportActionBar().getSubtitle();
        return itemPosition;
    }

    private void onPauseClicked() {

        if(mPlayer != null) {
            mPlayer.pause();

        } else {
            Toast.makeText(this, "Please select a song", Toast.LENGTH_SHORT).show();
        }

    }

    private void onPlayClicked() {

        if(mPlayer == null) {
            Toast.makeText(this, "Please select a song", Toast.LENGTH_SHORT).show();

        } else {
            mPlayer.resume();
        }
    }

    private void onSkipNextClicked() {

        if(mPlayer == null) {
            Toast.makeText(this, "Please select a song", Toast.LENGTH_SHORT).show();

        } else {
            this.setCurrentPlayingSong(this.getCurrenPlayingSongLocation() + 1);
            playSong(mItems.get(itemPosition).getTrack().getId());


        }
    }

    private void onPreviousClicked() {

        if(mPlayer == null) {
            Toast.makeText(this, "Please select a song", Toast.LENGTH_SHORT).show();

        } else {
            this.setCurrentPlayingSong(this.getCurrenPlayingSongLocation() - 1);
            playSong(mItems.get(itemPosition).getTrack().getId());
        }
    }


    private void playSong(final String songId) {

        final Config playerConfig = new Config(getApplicationContext(), token, CLIENT_ID);

            mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {

                        @Override
                        public void onInitialized(Player player) {
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


//    private void endSong() {
//        if(mTrackLength >= 120000) {
//            mPlayer.clearQueue();
//        }
//    }
//    public void endPlaybackAuto(Integer duration) {
//        if(mTrackLength >= 120000 ) {
//            Spotify.destroyPlayer(this);
//        }
//
//    }


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

    private void toolbarSetup() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("CompMusicPlayer");

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);



        myToolbar.findViewById(R.id.skip_previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onPreviousClicked();
            }
        });

        myToolbar.findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onPlayClicked();
            }
        });

        myToolbar.findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onPauseClicked();
            }
        });

        myToolbar.findViewById(R.id.skip_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onSkipNextClicked();
            }
        });

    }


    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {

    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {

    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }
}

