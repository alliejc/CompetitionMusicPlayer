package com.alisonjc.compmusicplayer.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.adapter.PlaylistRecyclerAdapter;
import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistModel.Item;

public class PlaylistViewHolder extends RecyclerView.ViewHolder {

    public TextView playlistTitle;
    public TextView songCount;
    public ImageView image;

    public PlaylistViewHolder(View itemView) {
        super(itemView);

        playlistTitle = (TextView) itemView.findViewById(R.id.playlist_header_text);
        songCount = (TextView) itemView.findViewById(R.id.playlist_sub_text);
        image = (ImageView) itemView.findViewById(R.id.playlist_image);
    }

    public void bind(final Item item, final PlaylistRecyclerAdapter.onItemClickListener listener) {
        playlistTitle.setText(item.getName());
        songCount.setText(item.getTracks().getTotal().toString() + " songs");

        itemView.setOnClickListener(view ->
                listener.onItemClick(item));
    }
}
