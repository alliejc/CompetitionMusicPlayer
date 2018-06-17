package com.alisonjc.compmusicplayer.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.alisonjc.compmusicplayer.databinding.TrackItemModel;


public class MediaPlayerViewModel extends ViewModel {

    private static final String TAG = MediaPlayerViewModel.class.getSimpleName();
    public MutableLiveData<TrackItemModel> currentTrack = new MutableLiveData<>();

    public void onPlayClicked(){
        Log.e(TAG, "onPlayClicked");
    }

    public void onPauseClicked(){
        Log.e(TAG, "onPauseClicked");
    }

}
