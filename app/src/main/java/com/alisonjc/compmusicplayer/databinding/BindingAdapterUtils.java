package com.alisonjc.compmusicplayer.databinding;


import android.databinding.BindingAdapter;
import android.widget.ImageButton;

public class BindingAdapterUtils {

    @BindingAdapter({"bind:imageButton"})
    public static void loadImageButton(ImageButton imageButton, String url){
        if (url !=null && !url.isEmpty()) {
            imageButton.setImageDrawable();
        }

        }
}
