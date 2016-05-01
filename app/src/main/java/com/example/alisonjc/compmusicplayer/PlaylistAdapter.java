package com.example.alisonjc.compmusicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.alisonjc.compmusicplayer.spotify.service.model.playlists.Item;

import java.util.List;

public class PlaylistAdapter extends ArrayAdapter<Item> {


    public PlaylistAdapter(Context context, int textViewResourceId, List<Item> objects) {
        super(context, textViewResourceId, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.playlist_item, null);
        }
        Item i = getItem(position);

        if (i != null)

        {
            TextView pt = (TextView) v.findViewById(R.id.playlisttitle);

            if (pt != null) {
                pt.setText(i.getName());
            }

        }

        return v;
    }



}
