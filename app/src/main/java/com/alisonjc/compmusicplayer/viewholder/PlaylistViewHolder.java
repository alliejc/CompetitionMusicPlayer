package com.alisonjc.compmusicplayer.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.adapter.PlaylistRecyclerAdapter;
import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistModel.Item;

public class PlaylistViewHolder extends RecyclerView.ViewHolder {

    public TextView playlistTitle;
    public TextView songCount;

    public PlaylistViewHolder(View itemView) {
        super(itemView);

        playlistTitle = (TextView) itemView.findViewById(R.id.recyclerview_header_text);
        songCount = (TextView) itemView.findViewById(R.id.recyclerview_sub_text);
    }

    public void bind(final Item item, final PlaylistRecyclerAdapter.onItemClickListener listener) {
        itemView.setOnClickListener(view ->

                listener.onItemClick(item));
    }
}
