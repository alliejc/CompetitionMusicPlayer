package com.example.alisonjc.compmusicplayer;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.alisonjc.compmusicplayer.spotify.service.SpotifyService;
import com.example.alisonjc.compmusicplayer.spotify.service.model.tracklists.Item;
import com.example.alisonjc.compmusicplayer.spotify.service.model.tracklists.PlaylistTracksList;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.PlayerStateCallback;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistTracksActivity extends AppCompatActivity implements PlayerNotificationCallback, MediaPlayer.OnPreparedListener {

    private static PlaylistTracksAdapter mPlaylistTracksItem;
    private static final String CLIENT_ID = BuildConfig.CLIENT_ID;

    private String mToken = "";
    private String mPlaylistName = "";
    private int mItemPosition = 0;
    private int mPauseTimeAt = 300000;
    private boolean mBeepPlayed = false;

    private Player mPlayer;
    private Timer mTimer;
    private ListView mListView;
    private ImageButton mPlayButton;
    private ImageButton mPauseButton;

    private SpotifyService mSpotifyService = new SpotifyService();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_tracks_list);

        Intent intent = getIntent();
        final Bundle b = intent.getExtras();
        mToken = b.getString("spotifyToken");
        String playlistId = b.getString("playlistId");
        String userId = b.getString("ownerId");
        mPlaylistName = b.getString("playlistName");

        mPlayButton = (ImageButton) findViewById(R.id.play);
        mPauseButton = (ImageButton) findViewById(R.id.pause);

        toolbarPlayerSetup();
        getPlaylistTracks(mToken, userId, playlistId);

        mPlaylistTracksItem = new PlaylistTracksAdapter(this, R.layout.playlist_tracks_item, new ArrayList<Item>());

        mListView = (ListView) findViewById(R.id.playlisttracksview);
        mListView.setAdapter(mPlaylistTracksItem);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCurrentPlayingSong(position);
                playSong(position);
                showPause();
            }
        });

        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                getPlayer().getPlayerState(new PlayerStateCallback() {
                    @Override
                    public void onPlayerState(PlayerState playerState) {

                        if(playerState.positionInMs > mPauseTimeAt - 8000 && !mBeepPlayed) {
                            playBeep();
                            mBeepPlayed=true;
                        }
                        if (playerState.positionInMs > mPauseTimeAt) {
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
                        mPauseTimeAt = 100000;
                    }
                    break;
                case R.id.two_minutes:
                    if (checked) {
                        mPauseTimeAt = 210000;
                    }
                        break;
            }
        }


    private void setCurrentPlayingSong(int itemPosition){
        this.mItemPosition = itemPosition;
        listviewSelector();
    }

    private void onPauseClicked() {
        if(mPlayer == null) {
            Toast.makeText(this, "Please select a song", Toast.LENGTH_SHORT).show();
        } else {
            mPlayer.pause();
            showPlay();
        }
    }

    private void showPause() {
        mPlayButton.setVisibility(View.GONE);
        mPauseButton.setVisibility(View.VISIBLE);
    }
    private void showPlay() {
        mPauseButton.setVisibility(View.GONE);
        mPlayButton.setVisibility(View.VISIBLE);
    }

    private void onPlayClicked() {

        if(mPlayer == null) {
            Toast.makeText(this, "Please select a song", Toast.LENGTH_SHORT).show();
        } else {
            mPlayer.resume();
            showPause();
        }
    }

    private void onSkipNextClicked() {
        if (mPlaylistTracksItem.getCount() <= mItemPosition + 1) {
                mItemPosition = 0;
                playSong(mItemPosition);
                mListView.setSelection(mItemPosition);
            } else {
                playSong(mItemPosition + 1);
            } if(mPlayer == null) {
                Toast.makeText(this, "Please select a song", Toast.LENGTH_SHORT).show();
            }
            }

    private void onPreviousClicked() {
            if (mItemPosition < 1) {
                mItemPosition = 0;
                playSong(mItemPosition);
            } else {
                playSong(mItemPosition - 1);
            } if (mPlayer == null) {
                Toast.makeText(this, "Please select a song", Toast.LENGTH_SHORT).show();
            }
        }

    private void listviewSelector() {
        mListView.clearChoices();
        mListView.setItemChecked(mItemPosition, true);
        mListView.setSelected(true);
        mPlaylistTracksItem.notifyDataSetChanged();
    }

    private void playSong(int locationid) {
        mBeepPlayed = false;
        showPause();
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
            final Config playerConfig = new Config(getApplicationContext(), mToken, CLIENT_ID);
            mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                        @Override
                        public void onInitialized(Player player) {
                        }
                        @Override
                        public void onError(Throwable throwable) {
                            Log.e("PlaylistActivity", "Could not initialize player: " + throwable.getMessage());
                        }
                    }
            );
            mPlayer.isInitialized();
            return mPlayer;
        }
        }

    public void getPlaylistTracks(String token, String userId, String playlistId) {

        mSpotifyService.getSpotifyService().getPlaylistTracks("Bearer " + token, userId, playlistId).enqueue(new Callback<PlaylistTracksList>() {
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_toolbar:
               userLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void userLogin() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = PlaylistActivity.MySignInDialog.newInstance();
        newFragment.show(ft, "dialog");

    }

    private void userLogout() {

        getPreferences(Context.MODE_PRIVATE).edit().clear().apply();

        Toast.makeText(this, "Logout Successful.  Please login to continue", Toast.LENGTH_LONG).show();

        userLogin();
    }

    @Override
    public MenuInflater getMenuInflater() {
        return super.getMenuInflater();
    }

    private void toolbarPlayerSetup() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.tool_bar);
            setSupportActionBar(myToolbar);
            ActionBar actionBar = getSupportActionBar();

            actionBar.setTitle(mPlaylistName);
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

