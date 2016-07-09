package com.alisonjc.compmusicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alisonjc.compmusicplayer.spotify.service.model.playlists.Item;

import java.util.List;

/**
 * Created by alisonjc on 7/7/16.
 */
public class NavigationAdapter extends ArrayAdapter<Item> {

    private final LayoutInflater mInflater;

    public NavigationAdapter(Context context, int textViewResourceId, List<Item> objects) {
        super(context, textViewResourceId, objects);
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            v = this.mInflater.inflate(R.layout.drawer_list_item, parent, false);
        }
        Item i = getItem(position);

        if (i != null) {
            TextView pt = (TextView) v.findViewById(R.id.drawer_list_item);

            if (pt != null) {
                pt.setText(i.getName());
            }
        }
        return v;
    }
}

