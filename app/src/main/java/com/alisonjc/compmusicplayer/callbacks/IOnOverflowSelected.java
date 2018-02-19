package com.alisonjc.compmusicplayer.callbacks;

import com.alisonjc.compmusicplayer.databinding.TrackItemModel;

/**
 * Created by acaldwell on 2/17/18.
 */

public interface IOnOverflowSelected {
    void onOverflowClicked(int action, TrackItemModel item, int position, String songTitle);
}
