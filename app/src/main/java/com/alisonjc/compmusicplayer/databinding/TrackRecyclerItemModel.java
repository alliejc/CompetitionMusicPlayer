package com.alisonjc.compmusicplayer.databinding;


import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.alisonjc.compmusicplayer.BR;

public class TrackRecyclerItemModel extends BaseObservable {

    private String mHeaderText;
    private String mSubHeaderText;
    private String mImage;
    private TrackItemInterface mTrackItem;

    public TrackRecyclerItemModel(TrackItemModel trackItemModel) {
        this.mTrackItem = trackItemModel;
    }

    @Bindable
    public String getHeaderText(){
        mHeaderText = mTrackItem.getSongName();
        return this.mHeaderText;
    }

    public void setHeaderText(String headerText){
        this.mHeaderText = headerText;
        notifyPropertyChanged(BR.headerText);
    }

    @Bindable
    public String getSubHeaderText(){
        mSubHeaderText = mTrackItem.getArtist();
        return this.mSubHeaderText;
    }

    public void setSubHeaderText(String subHeaderText){
        this.mSubHeaderText = subHeaderText;
        notifyPropertyChanged(BR.subHeaderText);
    }

    public String getImage() {
        mImage = mTrackItem.getImage();
        return this.mImage;
    }

    public void setImage(String image) {
        this.mImage = image;
//        notifyPropertyChanged(BR.image);
    }
}
