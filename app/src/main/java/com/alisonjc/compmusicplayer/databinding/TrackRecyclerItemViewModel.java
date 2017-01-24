package com.alisonjc.compmusicplayer.databinding;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.alisonjc.compmusicplayer.TrackItemInterface;
import com.alisonjc.compmusicplayer.spotify.TrackItem;

public class TrackRecyclerItemViewModel extends BaseObservable {

    private String mHeaderText;
    private String mSubHeaderText;
    private TrackItemInterface mTrackItem;

    public TrackRecyclerItemViewModel(TrackItem trackItem) {
        this.mTrackItem = trackItem;
    }

    @Bindable
    public String getHeaderText(){
        mHeaderText = mTrackItem.getSongName();
        return this.mHeaderText;
    }

    @Bindable
    public String getSubHeaderText(){
        mSubHeaderText = mTrackItem.getArtist();
        return this.mSubHeaderText;
    }
}
