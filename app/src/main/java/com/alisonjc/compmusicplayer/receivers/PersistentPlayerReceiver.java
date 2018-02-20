package com.alisonjc.compmusicplayer.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alisonjc.compmusicplayer.util.Constants;

/**
 * Created by acaldwell on 2/19/18.
 */

public class PersistentPlayerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.out.println("intent action = " + action);
            long id = intent.getLongExtra("id", -1);

            if(Constants.PLAY.equals(action)) {
                //playAlbum(id);
            } else if(Constants.PAUSE.equals(action)) {
                //queueAlbum(id);
            } else if(Constants.NEXT.equals(action)) {
                //playTrack(id);
            } else if(Constants.PREVIOUS.equals(action)) {
                //queueTrack(id);
            }
            else {
            }
        }
}
