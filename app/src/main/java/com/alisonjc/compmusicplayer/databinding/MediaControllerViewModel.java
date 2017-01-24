package com.alisonjc.compmusicplayer.databinding;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.alisonjc.compmusicplayer.spotify.TrackItem;

import static android.databinding.tool.util.GenerationalClassUtil.ExtensionFilter.BR;

public class MediaControllerViewModel extends BaseObservable {

    private String mSong;
    private String mArtist;
    private TrackItem mTrackItem;
    private String mUri;
    private int mSongProgress;
    private int mSeekBarMax;

    public MediaControllerViewModel(TrackItem trackItem) {
        mSong = trackItem.getSongName();
        mArtist = trackItem.getArtist();
        mUri = trackItem.getUri();
        mTrackItem = trackItem;
    }

    public void setTrackItem(TrackItem trackItem){
        mTrackItem = trackItem;
        mSong = trackItem.getSongName();
        mArtist = trackItem.getArtist();
        mUri = trackItem.getUri();
        notifyPropertyChanged(BR.songName);
        notifyPropertyChanged(BR.artist);
    }

    @Bindable
    public String getSongName(){
        return this.mSong;
    }

    @Bindable
    public String getArtist(){
        return this.mArtist;
    }

    @Bindable
    public String getUri(){
        return this.mUri;
    }

    @Bindable
    public int getProgress(){
        return this.mSongProgress;
    }

    public void setSongProgress(int songProgress){
        mSongProgress = songProgress;
        notifyPropertyChanged(BR.progress);
    }

    @Bindable
    public int getSeekBarMax(){
        return this.mSeekBarMax;
    }

    public void setSeekBarMax(int seekBarMax){
        mSeekBarMax = seekBarMax;
        notifyPropertyChanged(BR.seekBarMax);
    }
}
