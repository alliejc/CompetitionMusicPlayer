package com.alisonjc.compmusicplayer.util;

import android.content.Context;
import android.widget.Toast;

import com.alisonjc.compmusicplayer.R;

/**
 * Created by acaldwell on 2/17/18.
 */

public class Util {

    public static void showToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.getView().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.player_background));
        toast.show();
    }
}
