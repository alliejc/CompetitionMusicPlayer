package com.alisonjc.compmusicplayer;


import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alisonjc.compmusicplayer.callbacks.IOnPlaylistSelected;
import com.alisonjc.compmusicplayer.fragment.LoginDialogFrag;
import com.alisonjc.compmusicplayer.fragment.PlaylistFragment;
import com.alisonjc.compmusicplayer.spotify.SpotifyMusicPlayer;
import com.alisonjc.compmusicplayer.spotify.SpotifyService;
import com.alisonjc.compmusicplayer.callbacks.IOnTrackChanged;
import com.alisonjc.compmusicplayer.callbacks.IOnTrackSelected;
import com.alisonjc.compmusicplayer.fragment.PlaylistTracksFragment;
import com.alisonjc.compmusicplayer.fragment.TracksFragment;
import com.alisonjc.compmusicplayer.util.Constants;
import com.alisonjc.compmusicplayer.util.Util;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IOnPlaylistSelected, IOnTrackChanged, IOnTrackSelected {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @BindView(R.id.song_title)
    TextView mSongView;

    @BindView(R.id.artist)
    TextView mArtistView;

    @BindView(R.id.play)
    ImageButton mPlayButton;

    @BindView(R.id.pause)
    ImageButton mPauseButton;

    @BindView(R.id.seekerBarView)
    SeekBar mSeekBar;

    @BindView(R.id.musicCurrentLoc)
    TextView mSongLocationView;

    @BindView(R.id.musicDuration)
    TextView mSongDurationView;

    @BindView(R.id.radio_group)
    RadioGroup mRadioGroup;

    @BindView(R.id.one_minute_thirty)
    RadioButton mOneThirtyMin;

    @BindView(R.id.two_minutes)
    RadioButton mTwoMin;

    private static final int REQUEST_CODE = 1337;
    private static final String TAG = "MainActivity";

    private SpotifyService mSpotifyService = SpotifyService.getSpotifyService();
    private SpotifyMusicPlayer mSpotifyMusicPlayer = SpotifyMusicPlayer.getmSpotifyMusicPlayer();
    private SpotifyPlayer mPlayer;

    private PlaylistTracksFragment mPlaylistTracksFragment;
    private TracksFragment mTracksFragment;
    private IOnTrackChanged mIOnTrackChanged;

    private Handler mSeekBarHandler = new Handler();
    private Handler mMusicTimerHandler = new Handler();

    private boolean mBeepPlayed = false;
    private int mSongLocation = 0;
    private int mEndSongAt = 90000;
    private int mSeconds = 0;
    private int mMinutes = 0;
    private String mPlaylistTitle;
    private String mUserName;
    private String mUserEmail;

    /*VIEWS*/
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private ActionBar mActionBar;
    private View mPlayerControls;
    private BottomSheetBehavior bottomSheetBehavior;
    private TabLayout mTabLayout;
    private Unbinder unbinder;

    private final Player.OperationCallback mOperationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onError(Error error) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlayerControls = findViewById(R.id.music_player);
        unbinder = ButterKnife.bind(this);

        if (mSpotifyService.isLoggedIn()) {
            userLogin();
        }

        toolbarSetup();
        setUpTabs();
//        navigationDrawerSetup();
        setUpMediaPlayerUI();
        playerControlsSetup();
    }

    private void setUpMediaPlayerUI(){
        final View mBottomSheet = findViewById(R.id.bottom_sheet) ;
        bottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet) ;
        bottomSheetBehavior.setPeekHeight(65);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

//    private void navigationDrawerSetup() {
//        mActionBarDrawerToggle = new ActionBarDrawerToggle(
//                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
//        mActionBarDrawerToggle.syncState();
//
//        mNavigationView.setNavigationItemSelectedListener(this);
//        View header = mNavigationView.getHeaderView(0);
//
//        TextView name = (TextView) header.findViewById(R.id.nav_header_top);
//        TextView email = (TextView) header.findViewById(R.id.nav_header_bottom);
//        name.setText(mUserName);
//        email.setText(mUserEmail);
//    }

    @Override
    public void onBackPressed() {

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);

        } else if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStackImmediate();

        } else if (fragmentManager.getBackStackEntryCount() <= 1) {
            moveTaskToBack(true);

        } else {
            super.onBackPressed();
        }
    }

    private void userLogin() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment dialogFragment = LoginDialogFrag.newInstance();
        dialogFragment.show(ft, "dialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_overflow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                mSpotifyService.userLogout(getApplicationContext());
                mNavigationView.setCheckedItem(R.id.nav_playlists);
                clearPlayer();
                userLogin();
                break;

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_playlists) {
           getPlaylists();

        } else if (id == R.id.nav_songs) {
            getAllSongs();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void playSong(){
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mBeepPlayed = false;
        setMusicTimer();
        showPauseButton();
        setSeekBar();
    }

    private void setMusicTimer() {
        if (mPlayer != null && mPlayer.getPlaybackState() != null) {
            mSongLocation = (int) mPlayer.getPlaybackState().positionMs;
            if (mSongLocation >= mEndSongAt - 10000 && !mBeepPlayed) {
                playBeep();
                mBeepPlayed = true;

            } else if (mSongLocation >= mEndSongAt) {
                onSkipNextClicked();
            }
            mMusicTimerHandler.postDelayed(musicTimerRun, 1000);
        }
    }

    private Runnable musicTimerRun = () -> {
        setMusicTimer();
    };

    private void setSeekBar() {
        if (mPlayer != null) {
            mSeekBar.setProgress(mSongLocation);
            mSeekBar.setMax(mEndSongAt);

            mSeconds = ((mSongLocation / 1000) % 60);
            mMinutes = ((mSongLocation / 1000) / 60);

            mSongLocationView.setText(String.format("%2d:%02d", mMinutes, mSeconds, 0));
        }
        mSeekBarHandler.postDelayed(seekrun, 1000);

    }

    private void playerControlsSetup() {
        mSongLocationView.setText(R.string.start_time);
        mSongDurationView.setText(R.string.one_thirty_radio_button);

        mPlayerControls.findViewById(R.id.skip_previous).setOnClickListener(view -> {
            onPreviousClicked();
        });

        mPlayerControls.findViewById(R.id.play).setOnClickListener(view -> {
            onPlayClicked();
        });

        mPlayerControls.findViewById(R.id.pause).setOnClickListener(view -> {
            onPauseClicked();
        });

        mPlayerControls.findViewById(R.id.skip_next).setOnClickListener(view -> {
            onSkipNextClicked();
        });

        mRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            onRadioButtonClicked(i);
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("Seekbar", "onProgressChanged" + progress);

                if (mPlayer != null && fromUser) {
                    mSeekBarHandler.removeCallbacks(seekrun);

                    mPlayer.seekToPosition(mOperationCallback, progress);
                    seekBar.setProgress(mSongLocation);
                    mSongLocationView.setText(String.format("%2d:%02d", mMinutes, mSeconds, 0));

                    mSongLocation = progress;
                    setSeekBar();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("Seekbar", "START" + seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("Seekbar", "STOP" + seekBar.getProgress());
                //onProgressChanged(seekBar, seekBar.getProgress(), true);
            }
        });
    }

    private void playBeep() {
        final MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beep);

        mediaPlayer.setOnPreparedListener(mediaPlayer1 -> {
            mediaPlayer.start();
        });

        mediaPlayer.setOnCompletionListener(mediaPlayer1 -> {
            mediaPlayer.release();
        });
    }

    private void onPauseClicked() {
        if (mPlayer != null) {
            mPlayer.pause(mOperationCallback);
            showPlayButton();
        } else {
            Util.showToast(this, getString(R.string.please_select_a_song));
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
        if (mPlayer != null) {
            mPlayer.resume(mOperationCallback);
            showPauseButton();
        }
    }

    private void onSkipNextClicked() {
        if (mPlayer != null) {
            resetHandlers();
            mPlayer.skipToNext(mOperationCallback);
            onControllerTrackChange(true);
        } else {
            Log.i(TAG, "onSkipNextClickedPLAYERNULL");
        }
    }

    private void resetHandlers() {
        mSeekBarHandler.removeCallbacks(seekrun);
        mMusicTimerHandler.removeCallbacks(musicTimerRun);
    }

    Runnable seekrun = () -> {
        setSeekBar();
    };

    private void onPreviousClicked() {
        if (mPlayer != null) {
            mPlayer.skipToPrevious(mOperationCallback);
            onControllerTrackChange(false);
        }
    }

    public void onRadioButtonClicked(int id) {
        switch (id) {
            case R.id.one_minute_thirty:
                if (mOneThirtyMin.isChecked()) {
                    mSongDurationView.setText(R.string.one_thirty_radio_button);
                    mEndSongAt = 90000;
                }
                break;
            case R.id.two_minutes:
                if (mTwoMin.isChecked()) {
                    mSongDurationView.setText(R.string.two_minute_radio_button);
                    mEndSongAt = 120000;
                }
                break;
        }
    }

    private void toolbarSetup() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(R.string.app_name);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(false);
    }

    private void setUpTabs(){
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getTabData(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                getTabData(tab.getPosition());
            }
        });
    }

    private void getTabData(int tabPosition){
        switch (tabPosition){
            case 0:
                getPlaylists();
                break;
            case 1:
                getAllSongs();
                break;
        }
    }

    private void getAllSongs() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mTracksFragment = TracksFragment.newInstance();
        mIOnTrackChanged = mTracksFragment;
        fragmentManager.beginTransaction().replace(R.id.main_framelayout, mTracksFragment, "tracksFragment").addToBackStack(null).commit();
        mActionBar.setTitle(R.string.songs_drawer);
    }

    private void getPlaylists() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        PlaylistFragment playlistFragment = PlaylistFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.main_framelayout, playlistFragment, "playlistFragment").addToBackStack(null).commit();
        mActionBar.setTitle(R.string.playlists_drawer);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse authResponse = AuthenticationClient.getResponse(resultCode, intent);
            switch (authResponse.getType()) {

                case TOKEN:
                    final String mToken = authResponse.getAccessToken();
                    mSpotifyService.getCurrentUser(mToken)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(spotifyUser -> {

                                mSpotifyService.setUserId(spotifyUser.getId(), getBaseContext());
                                mSpotifyService.setToken(mToken, getBaseContext());
                                mUserName = spotifyUser.getDisplayName();
                                mUserEmail = spotifyUser.getEmail();
//                                navigationDrawerSetup();
                                startPlaylistFragment();

                                if (mPlayer == null) {
                                    mPlayer = mSpotifyMusicPlayer.getPlayer(this);
                                }

                                mActionBar.setTitle(R.string.playlists_drawer);
                            }, throwable -> {

                            }, () -> {

                            });
                    break;

                case ERROR:
                    break;

                default:
            }
        }
    }

    private void startPlaylistFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        PlaylistFragment playlistFragment = PlaylistFragment.newInstance();

        fragmentManager.beginTransaction()
                .replace(R.id.main_framelayout, playlistFragment, "playlistTracksFragment").addToBackStack(null)
                .commit();
    }

    @Override
    public void onPlaylistSelected(String userId, String playlistId, String playlistTitle) {
        if(playlistTitle.equals(Constants.ADD_PLAYLIST)){

        } else {
            mPlaylistTitle = playlistTitle;
            mActionBar.setTitle(mPlaylistTitle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            mPlaylistTracksFragment = PlaylistTracksFragment.newInstance(userId, playlistId, playlistTitle);
            mIOnTrackChanged = mPlaylistTracksFragment;
            fragmentManager.beginTransaction().replace(R.id.main_framelayout, mPlaylistTracksFragment, "playlistTracksFragment").addToBackStack(null).commit();
        }
    }

    @Override
    public void onTrackSelected(String songName, String artistName, String uri) {
        playSong();
        mPlayer.playUri(mOperationCallback, uri, 0, 0);
        mSongView.setText(songName + " - ");
        mArtistView.setText(artistName);
    }

    public void clearPlayer() {
        resetHandlers();
        setSeekBar();
        setMusicTimer();
        showPlayButton();

        if (mPlayer != null) {
            mPlayer.pause(mOperationCallback);
            Spotify.destroyPlayer(mPlayer);
        }
    }

    @Override
    public void onControllerTrackChange(boolean skipforward) {
        if (mIOnTrackChanged != null) {
            mIOnTrackChanged.onControllerTrackChange(skipforward);
        } else {
            Log.i(TAG, "onControllerTrackChangeNULL");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

    }
}

