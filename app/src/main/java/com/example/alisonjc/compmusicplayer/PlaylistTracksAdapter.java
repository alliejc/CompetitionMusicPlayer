package com.example.alisonjc.compmusicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.alisonjc.compmusicplayer.spotify.service.model.tracklists.Item;

import java.util.List;

public class PlaylistTracksAdapter extends ArrayAdapter<Item> {

    public PlaylistTracksAdapter(Context context, int textViewResourceId, List<Item> objects) {
        super(context, textViewResourceId, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent){

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.playlist_tracks_item, null);
        }

        Item i = this.getItem(position);

        if (i != null) {

            TextView sn = (TextView) v.findViewById(R.id.songname);
            TextView an = (TextView) v.findViewById(R.id.artistname);

            if (sn != null){
                sn.setText(i.getTrack().getName());
            }
            if (an != null){
                an.setText(i.getTrack().getArtists().get(0).getName());
            }

        }
        return v;

    }

}

