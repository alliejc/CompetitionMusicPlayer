package com.alisonjc.compmusicplayer.databinding;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.alisonjc.compmusicplayer.BR;
import com.alisonjc.compmusicplayer.spotify.TrackItem;

public class MediaControllerViewModel extends BaseObservable {

    private String mSong;
    private String mArtist;
    private TrackItem mTrackItem;
    private String mUri;
    private int mSongProgress;
    private int mSeekBarMax;
    private int mPlayButtonVisible;
    private int mPauseButtonVisible;

    public MediaControllerViewModel() {
    }

    public void setTrackItem(TrackItem trackItem) {
        mTrackItem = trackItem;
        mSong = trackItem.getSongName();
        mArtist = trackItem.getArtist();
        mUri = trackItem.getUri();
        notifyPropertyChanged(BR.songName);
        notifyPropertyChanged(BR.artist);
    }

    @Bindable
    public String getSongName() {
        return this.mSong;
    }

    @Bindable
    public String getArtist() {
        return this.mArtist;
    }

    @Bindable
    public String getUri() {
        return this.mUri;
    }

    @Bindable
    public int getProgress() {
        return this.mSongProgress;
    }

    @Bindable
    public String getProgressText() {
        int seconds = ((mSongProgress / 1000) % 60);
        int minutes = ((mSongProgress / 1000) / 60);

        return String.format("%2d:%02d", minutes, seconds, 0);
    }

    public void setSongProgress(int songProgress) {
        mSongProgress = songProgress;
        notifyPropertyChanged(BR.progress);
        notifyPropertyChanged(BR.progressText);
    }

    @Bindable
    public int getSeekBarMax() {
        return this.mSeekBarMax;
    }

    public void setSeekBarMax(int seekBarMax) {
        mSeekBarMax = seekBarMax;
        notifyPropertyChanged(BR.seekBarMax);
    }

    public void setPlayButtonVisible(){
        //0 == visible
        //8 == gone
        mPlayButtonVisible = 0;
        mPauseButtonVisible = 8;

        notifyPropertyChanged(BR.playButtonVisible);
    }

    @Bindable
    public int getPlayButtonVisible(){
        return  this.mPlayButtonVisible;
    }

    @Bindable
    public int getPauseButtonVisible(){
        return this.mPauseButtonVisible;
    }



}
