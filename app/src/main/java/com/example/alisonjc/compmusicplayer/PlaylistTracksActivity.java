package com.example.alisonjc.compmusicplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.alisonjc.compmusicplayer.spotify.SpotifyService;
import com.example.alisonjc.compmusicplayer.spotify.tracklist.Item;
import com.example.alisonjc.compmusicplayer.spotify.tracklist.PlaylistTracksList;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.PlayerStateCallback;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaylistTracksActivity extends AppCompatActivity implements PlayerNotificationCallback, MediaPlayer.OnPreparedListener {

    private static final String CLIENT_ID = "fea06d390d9848c3b5c0ff43bbe0b2d0";
    private String token = "";
    private String playlistName = "";
    private Player mPlayer;
    private int itemPosition = 0;
    private static PlaylistTracksItemAdapter mPlaylistTracksItem;
    private int pauseTimeAt = 300000;
    private Timer mTimer;
    private boolean mBeepPlayed = false;
    private ListView mListView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_tracks_list);

        Intent intent = getIntent();
        final Bundle b = intent.getExtras();
        token = b.getString("spotifyToken");
        String playlistId = b.getString("playlistId");
        String userId = b.getString("ownerId");
        playlistName = b.getString("playlistName");

        toolbarPlayerSetup();
        getPlaylistTracks(token, userId, playlistId);

        mPlaylistTracksItem = new PlaylistTracksItemAdapter(this, R.layout.playlist_tracks_item, new ArrayList<Item>());

        mListView = (ListView) findViewById(R.id.playlisttracksview);
        mListView.setAdapter(mPlaylistTracksItem);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCurrentPlayingSong(position);
                playSong(position);
            }
        });

        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                getPlayer().getPlayerState(new PlayerStateCallback() {
                    @Override
                    public void onPlayerState(PlayerState playerState) {

                        if(playerState.positionInMs > pauseTimeAt - 8000 && !mBeepPlayed) {
                            playBeep();
                            mBeepPlayed=true;

                        }
                        if (playerState.positionInMs > pauseTimeAt) {
                            mPlayer.pause();
                            onSkipNextClicked();
                        }
                    }
                });
            }
        };

        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 1000, 1000);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

            switch (view.getId()) {

                case R.id.one_minute_thirty:
                    if (checked){
                        pauseTimeAt = 100000;
                    }
                    break;
                case R.id.two_minutes:
                    if (checked) {
                        pauseTimeAt = 210000;
                    }
                        break;
            }
        }


    private void setCurrentPlayingSong(int itemPosition){
        this.itemPosition = itemPosition;
        listviewSelector();
    }

    private void onPauseClicked() {
        if(mPlayer == null) {
            Toast.makeText(this, "Please select a song", Toast.LENGTH_SHORT).show();
        } else {
            mPlayer.pause();
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
        if (mPlaylistTracksItem.getCount() < itemPosition + 2) {
                itemPosition = 0;
                playSong(itemPosition);
            } else {
                playSong(itemPosition + 1);
            } if(mPlayer == null) {
                Toast.makeText(this, "Please select a song", Toast.LENGTH_SHORT).show();
            }
            }

    private void onPreviousClicked() {
            if (itemPosition < 1) {
                itemPosition = 0;
                playSong(itemPosition);
            } else {
                playSong(itemPosition - 1);
            } if (mPlayer == null) {
                Toast.makeText(this, "Please select a song", Toast.LENGTH_SHORT).show();
            }
        }

    private void listviewSelector() {
        mListView.clearChoices();
        mListView.setItemChecked(itemPosition, true);
        mListView.setSelected(true);
        mPlaylistTracksItem.notifyDataSetChanged();
    }

    private void playSong(int locationid) {
        mBeepPlayed = false;
        setCurrentPlayingSong(locationid);
        getSupportActionBar().setSubtitle(mPlaylistTracksItem.getItem(locationid).getTrack().getName());
        getPlayer().play("spotify:track:" + mPlaylistTracksItem.getItem(locationid).getTrack().getId());
    }

    private void playBeep() {
            final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.beep);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.release();
            }
        });
    }

    private Player getPlayer() {
        if(mPlayer != null) {
            return mPlayer;
        } else {
            final Config playerConfig = new Config(getApplicationContext(), token, CLIENT_ID);
            mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                        @Override
                        public void onInitialized(Player player) {
                        }
                        @Override
                        public void onError(Throwable throwable) {
                            Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                        }
                    }
            );
            mPlayer.isInitialized();
            return mPlayer;
        }
        }

    public void getPlaylistTracks(String token, String userId, String playlistId) {

        getSpotifyService().getPlaylistTracks("Bearer " + token, userId, playlistId).enqueue(new Callback<PlaylistTracksList>() {
            @Override
            public void onResponse(Call<PlaylistTracksList> call, Response<PlaylistTracksList> response) {

                if(response.body() != null) {
                    mPlaylistTracksItem.clear();
                    mPlaylistTracksItem.addAll(response.body().getItems());
                    mPlaylistTracksItem.notifyDataSetChanged();
                }
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

    private void toolbarPlayerSetup() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.tool_bar);
            setSupportActionBar(myToolbar);
            ActionBar actionBar = getSupportActionBar();

            actionBar.setTitle(playlistName);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);

            myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            Toolbar myPlayerToolbar = (Toolbar) findViewById(R.id.tool_bar_player);

            myPlayerToolbar.findViewById(R.id.skip_previous).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onPreviousClicked();
                }
            });

            myPlayerToolbar.findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onPlayClicked();
                }
            });

            myPlayerToolbar.findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onPauseClicked();
                }
            });

            myPlayerToolbar.findViewById(R.id.skip_next).setOnClickListener(new View.OnClickListener() {
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
        mTimer.cancel();
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}

