package com.alisonjc.compmusicplayer.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alisonjc.compmusicplayer.R;

import java.util.ArrayList;

/**
 * Created by acaldwell on 2/19/18.
 */

public class CreatePlaylistDialog extends DialogFragment {
    private EditText mTitleEditText;

    public CreatePlaylistDialog() {
    }

    public static PlaylistActionDialog newInstance() {
        PlaylistActionDialog frag = new PlaylistActionDialog();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setDialogStyle();

        View rootView = inflater.inflate(R.layout.fragment_create_playlist, container, false);
        mTitleEditText = (EditText) rootView.findViewById(R.id.playlist_edit_text);

        return rootView;
    }

    private void setDialogStyle(){
        setStyle(android.app.DialogFragment.STYLE_NORMAL, 0);

        if(getDialog() != null && getDialog().getWindow() != null) {
            getDialog().setCancelable(true);
            getDialog().getWindow().setTitle(getString(R.string.choose_a_playlist));
        }
    }

}
