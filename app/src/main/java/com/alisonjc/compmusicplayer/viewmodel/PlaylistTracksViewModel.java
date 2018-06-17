package com.alisonjc.compmusicplayer.viewmodel;

import android.databinding.ObservableField;

import com.alisonjc.compmusicplayer.databinding.TrackItemModel;


public class PlaylistTracksViewModel {
    private ObservableField<TrackItemModel> mCurrentPlayingTrack;

    public void init() {
        mCurrentPlayingTrack = new ObservableField<>();
    }

    public void setCurrentTrack(TrackItemModel track){
        mCurrentPlayingTrack.set(track);
    }

    public TrackItemModel getCurrentTrack(){
        return this.mCurrentPlayingTrack.get();
    }
}
