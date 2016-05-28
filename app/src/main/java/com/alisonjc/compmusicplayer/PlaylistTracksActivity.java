package com.alisonjc.compmusicplayer;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alisonjc.compmusicplayer.spotify.service.SpotifyService;
import com.alisonjc.compmusicplayer.spotify.service.model.tracklists.Item;
import com.alisonjc.compmusicplayer.spotify.service.model.tracklists.PlaylistTracksList;
import com.google.inject.Inject;
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
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_playlist_tracks_list)
public class PlaylistTracksActivity extends RoboActionBarActivity implements PlayerNotificationCallback, MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener {

    private static PlaylistTracksAdapter mPlaylistTracksItem;
    private static final String CLIENT_ID = BuildConfig.CLIENT_ID;

    private String mToken = "";
    private String mPlaylistName = "";

    private int mItemPosition = 0;
    private int mPauseTimeAt = 100000;
    private boolean mBeepPlayed = false;

    private Player mPlayer;
    private Timer mTimer;
    private int mSongDuration;
    private int mSongLocation;
    private Handler seekHandler = new Handler();

    @InjectView(R.id.play)
    private ImageButton mPlayButton;

    @InjectView(R.id.pause)
    private ImageButton mPauseButton;

    @InjectView(R.id.playlisttracksview)
    private ListView mListView;

    @InjectView(R.id.seekerBarView)
    private SeekBar mSeekBar;

    @InjectView(R.id.musicCurrentLoc)
    private TextView mSongLocationView;

    @InjectView(R.id.musicDuration)
    private TextView mSongDurationView;

    @Inject
    SpotifyService mSpotifyService;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        final Bundle b = intent.getExtras();
        mToken = b.getString("spotifyToken");
        String playlistId = b.getString("playlistId");
        String userId = b.getString("ownerId");
        mPlaylistName = b.getString("playlistName");

        getPlaylistTracks(mToken, userId, playlistId);

        toolbarPlayerSetup();
        listViewSetup();
        startTimerTask();

        mSongLocationView.setText("0:00");
        mSongDurationView.setText(R.string.one_thirty_radio_button);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mPlayer != null && fromUser) {
                    mPlayer.seekToPosition(progress);
                    mSeekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {

            case R.id.one_minute_thirty:
                if (checked) {
                    mSongDurationView.setText(R.string.one_thirty_radio_button);
                    mPauseTimeAt = 90000;
                }
                break;
            case R.id.two_minutes:
                if (checked) {
                    mSongDurationView.setText(R.string.two_minute_radio_button);
                    mPauseTimeAt = 120000;
                }
                break;
        }
    }

    private void startTimerTask() {

        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                getPlayer().getPlayerState(new PlayerStateCallback() {
                    @Override
                    public void onPlayerState(PlayerState playerState) {

                        if (mSongLocation > mPauseTimeAt - 10000 && !mBeepPlayed) {
                            playBeep();
                            mBeepPlayed = true;
                        }
                        if (mSongLocation > mPauseTimeAt) {
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

    private void setSeekBar() {

        if (mPlayer != null) {
            mPlayer.getPlayerState(new PlayerStateCallback() {

                @Override
                public void onPlayerState(PlayerState playerState) {

                    mSongDuration = mPauseTimeAt;
                    mSongLocation = playerState.positionInMs;

                    mSeekBar.setMax(mSongDuration);
                    mSeekBar.setProgress(mSongLocation);

                    int seconds = ((mSongLocation / 1000) % 60);
                    int minutes = ((mSongLocation / 1000) / 60);

                    mSongLocationView.setText(String.format("%2d:%02d", minutes, seconds, 0));


                }
            });
        }

        seekHandler.postDelayed(run, 1000);
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            setSeekBar();

        }
    };

    private void listViewSetup() {

        mPlaylistTracksItem = new PlaylistTracksAdapter(this, R.layout.playlist_tracks_item, new ArrayList<Item>());
        mListView.setAdapter(mPlaylistTracksItem);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCurrentPlayingSong(position);
                playSong(position);
                showPauseButton();
            }
        });
    }

    private void setCurrentPlayingSong(int itemPosition) {

        this.mItemPosition = itemPosition;
        listviewSelector();
    }

    private void onPauseClicked() {

        if (mPlayer == null) {
            Toast.makeText(this, "Please select a song", Toast.LENGTH_SHORT).show();
        } else {
            mPlayer.pause();
            showPlayButton();
        }
    }

    private void showPauseButton() {

        mPlayButton.setVisibility(View.GONE);
        mPauseButton.setVisibility(View.VISIBLE);
    }

    private void showPlayButton() {

        mPauseButton.setVisibility(View.GONE);
        mPlayButton.setVisibility(View.VISIBLE);
    }

    private void onPlayClicked() {

        if (mPlayer == null) {
            Toast.makeText(this, "Please select a song", Toast.LENGTH_SHORT).show();
        } else {
            mPlayer.resume();
            showPauseButton();
        }
    }

    private void onSkipNextClicked() {

        if (mPlaylistTracksItem.getCount() <= mItemPosition + 1) {
            mItemPosition = 0;
            playSong(mItemPosition);
            mListView.setSelection(mItemPosition);
        } else {
            playSong(mItemPosition + 1);
        }
        if (mPlayer == null) {
            Toast.makeText(this, "Please select a song", Toast.LENGTH_SHORT).show();
        }
    }

    private void onPreviousClicked() {

        if (mItemPosition < 1) {
            mItemPosition = 0;
            playSong(mItemPosition);
        } else {
            playSong(mItemPosition - 1);
        }
        if (mPlayer == null) {
            Toast.makeText(this, "Please select a song", Toast.LENGTH_SHORT).show();
        }
    }

    private void listviewSelector() {

        mListView.clearChoices();
        mListView.setItemChecked(mItemPosition, true);
        mListView.setSelected(true);
        mPlaylistTracksItem.notifyDataSetChanged();
    }

    /**
     * Switches the play button to the pause button. Sets the song location and subtitle of the song to be played.  Plays song.
     *
     * @param locationid - location of the song to be played
     */
    private void playSong(int locationid) {

        mBeepPlayed = false;
        showPauseButton();
        setCurrentPlayingSong(locationid);
        getSupportActionBar().setSubtitle(mPlaylistTracksItem.getItem(locationid).getTrack().getName());
        getPlayer().play("spotify:track:" + mPlaylistTracksItem.getItem(locationid).getTrack().getId());
        setSeekBar();
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

        if (mPlayer != null) {
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
            });
            mPlayer.isInitialized();
            return mPlayer;
        }
    }

//    private void onTokenExpired() {
//
//        getPreferences(Context.MODE_PRIVATE).edit().clear().apply();
//        Toast.makeText(this, "Due to Spotify limitations your Spotify login expires every hour, sorry for the inconvenience", Toast.LENGTH_LONG).show();
//        userLogin();
//    }

    /**
     * Gets the current users playlist tracks and updates the adapter
     *
     * @param token      - Spotify user token
     * @param userId     - Spotify user ID
     * @param playlistId - Spotify playlist ID
     */

    public void getPlaylistTracks(String token, String userId, String playlistId) {

        if (token != null) {

            mSpotifyService.getSpotifyService().getPlaylistTracks("Bearer " + token, userId, playlistId).enqueue(new Callback<PlaylistTracksList>() {

                @Override
                public void onResponse(Call<PlaylistTracksList> call, Response<PlaylistTracksList> response) {

                    if (response.isSuccess() && response.body().getItems() != null) {
                        mPlaylistTracksItem.clear();
                        mPlaylistTracksItem.addAll(response.body().getItems());
                        mPlaylistTracksItem.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<PlaylistTracksList> call, Throwable t) {
                    userLogout();
                }
            });
        } else {
            userLogout();
        }
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

        getPreferences(Context.MODE_PRIVATE).edit().clear().apply();
        Toast.makeText(this, "Due to Spotify limitations your Spotify login expires every hour, sorry for the inconvenience", Toast.LENGTH_LONG).show();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = PlaylistActivity.MySignInDialog.newInstance();
        newFragment.show(ft, "dialog");
    }

    private void userLogout() {

        getPreferences(Context.MODE_PRIVATE).edit().clear().apply();
        //Toast.makeText(this, "Logout Successful.  Please login to continue", Toast.LENGTH_LONG).show();
        userLogin();
    }

    private void toolbarPlayerSetup() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setTitle(mPlaylistName);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        assert myToolbar != null;
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Toolbar myPlayerToolbar = (Toolbar) findViewById(R.id.tool_bar_player);

        assert myPlayerToolbar != null;
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
        seekHandler.removeCallbacks(run);
        mSeekBar.setProgress(0);
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

