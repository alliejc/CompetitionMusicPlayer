package com.alisonjc.compmusicplayer.databinding;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;
import com.alisonjc.compmusicplayer.BR;

import com.alisonjc.compmusicplayer.spotify.TrackItemModel;

public class MediaControllerViewModel extends BaseObservable {

    private String mSong;
    private String mArtist;
    private TrackItemModel mTrackItemModel;
    private String mUri;
    private int mSongProgress;
    private int mSeekBarMax;
    private boolean mIsPlaying;

    public MediaControllerViewModel() {
    }

    public void setTrackItemModel(TrackItemModel trackItemModel) {
        mTrackItemModel = trackItemModel;
        mSong = trackItemModel.getSongName();
        mArtist = trackItemModel.getArtist();
        mUri = trackItemModel.getUri();
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

    public void setIsPlaying(boolean isPlaying){
        mIsPlaying = isPlaying;
        notifyPropertyChanged(BR.playButtonVisibility);
        notifyPropertyChanged(BR.pauseButtonVisibility);
    }

    @Bindable
    public int getPlayButtonVisibility(){
        return mIsPlaying ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getPauseButtonVisibility(){
        return mIsPlaying ? View.GONE : View.VISIBLE;
    }

}
