package com.alisonjc.compmusicplayer;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alisonjc.compmusicplayer.databinding.MediaControllerViewModel;
import com.alisonjc.compmusicplayer.databinding.MediaControlsBinding;
import com.alisonjc.compmusicplayer.spotify.MusicPlayer;
import com.alisonjc.compmusicplayer.spotify.TrackItem;
import com.alisonjc.compmusicplayer.tracks.OnControllerTrackChangeListener;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MediaController extends Fragment implements OnControllerTrackChangeListener {


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

    @Inject
    private MediaControllerViewModel mMediaControllerViewModel;

    @Inject
    private MediaControlsBinding mMediaControlsBinding;

    private int mSongProgress = 0;
    private Handler mSeekBarHandler = new Handler();
    private Handler mMusicTimerHandler = new Handler();
    private SpotifyPlayer mPlayer;
    private int mEndSongAt = 90000;
    private boolean mBeepPlayed = false;
    private View mPlayerControls;
    private int mSeconds = 0;
    private int mMinutes = 0;
    private OnControllerTrackChangeListener mOnControllerTrackChangeListener;
    private static final String TAG = "MediaController";
    private MusicPlayer mMusicPlayer = MusicPlayer.getmMusicPlayer();

    public MediaController() {
    }

    public static MediaController newInstance() {
        return new MediaController();
    }

    private final Player.OperationCallback mOperationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onError(Error error) {
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaControlsBinding = DataBindingUtil.setContentView(getActivity(), R.layout.media_controls);
        mMediaControlsBinding.setMedia_controller(mMediaControllerViewModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.media_controls, container, false);
        mPlayerControls = rootView.findViewById(R.id.music_player);
        ButterKnife.bind(this, rootView);

        if (mPlayer == null) {
            mPlayer = mMusicPlayer.getPlayer(getContext());
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playerControlsSetup();
    }

    public void playSong(TrackItem trackItem) {
        Log.i(TAG, "playSong");

        mBeepPlayed = false;
        Log.i(TAG, "mBeepPlayed = false");

        mMediaControllerViewModel.setTrackItem(trackItem);
        mPlayer.playUri(mOperationCallback, trackItem.getUri(), 0, 0);

        setMusicTimer();
        showPauseButton();
        setSeekBar();
    }

    private void setMusicTimer() {

        if (mPlayer != null && mPlayer.getPlaybackState() != null) {

            mSongProgress = (int) mPlayer.getPlaybackState().positionMs;

            if (mSongProgress >= mEndSongAt - 10000 && !mBeepPlayed) {
                Log.i(TAG, "setTimerPlayBeep");

                playBeep();
                mBeepPlayed = true;

                Log.i(TAG, "mBeepPlayed = true");

            } else if (mSongProgress >= mEndSongAt && mBeepPlayed) {
                Log.i(TAG, "SetTimeSkipNext");

                onSkipNextClicked();
                mBeepPlayed = false;
                Log.i(TAG, "mBeepPlayed = false");
            }
            mMusicTimerHandler.postDelayed(musicTimerRun, 1000);
        }
    }

    private Runnable musicTimerRun = () -> {
        setMusicTimer();
    };


    private void setSeekBar() {
        Log.i("setSeekBar", "SetSeekBar" + mSongProgress);

        if (mPlayer != null) {

            mMediaControllerViewModel.setSongProgress(mSongProgress);
            mMediaControllerViewModel.setSeekBarMax(mEndSongAt);

            mSeconds = ((mSongProgress / 1000) % 60);
            mMinutes = ((mSongProgress / 1000) / 60);

            mSongLocationView.setText(String.format("%2d:%02d", mMinutes, mSeconds, 0));
        }
        mSeekBarHandler.postDelayed(seekrun, 1000);

    }

    Runnable seekrun = () -> {
        setSeekBar();
    };

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
                    Log.i("onProgressChanged", "removeCallbacks - Seek");

                    mPlayer.seekToPosition(mOperationCallback, progress);

                    seekBar.setProgress(mSongProgress);
                    Log.i("onProgressChanged", "SetProgress" + seekBar.getProgress());

                    mSongLocationView.setText(String.format("%2d:%02d", mMinutes, mSeconds, 0));

                    mSongProgress = progress;
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
        Log.i(TAG, "PlayBeep");

        final MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.beep);

        mediaPlayer.setOnPreparedListener(mediaPlayer1 -> {
            mediaPlayer.start();
            Log.i(TAG, "MediaPlayerSTART");
        });

        mediaPlayer.setOnCompletionListener(mediaPlayer1 -> {
            mediaPlayer.release();
            Log.i(TAG, "MediaPlayerRELEASE");
        });
    }

    private void onPauseClicked() {

        if (mPlayer != null) {
            mPlayer.pause(mOperationCallback);
            showPlayButton();
        } else {
            Toast.makeText(getActivity(), "Please select a song", Toast.LENGTH_SHORT).show();
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
        Log.i(TAG, "onSkipClicked");

        if (mPlayer != null) {
            Log.i(TAG, "onSkipNextClickedPLAYERNOTNULL");
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

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach");
        clearPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPlayer != null) {
            setSeekBar();
        }
        Log.i(TAG, "onResume");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnControllerTrackChangeListener) {
            mOnControllerTrackChangeListener = (OnControllerTrackChangeListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnControllerInteractionListener");
        }
    }

    @Override
    public void onControllerTrackChange(boolean skipforward) {

        if (mOnControllerTrackChangeListener != null) {
            Log.i(TAG, "onControllerTrackChangeNOTNULL");
            mOnControllerTrackChangeListener.onControllerTrackChange(skipforward);

        } else {
            Log.i(TAG, "onControllerTrackChangeNULL");
        }

    }

    public void clearPlayer() {
        Log.i(TAG, "clearPlayer");

        resetHandlers();

        setSeekBar();
        setMusicTimer();
        showPlayButton();

        if (mPlayer != null) {
            mPlayer.pause(mOperationCallback);
            Spotify.destroyPlayer(mPlayer);
        }
    }
}

