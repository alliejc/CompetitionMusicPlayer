package com.alisonjc.compmusicplayer.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.adapter.PlaylistAdapter;
import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistModel.Item;
import com.squareup.picasso.Picasso;


public class AddPlaylistViewHolder extends RecyclerView.ViewHolder {
    public TextView playlistTitle;
    public ImageView image;
    public View itemView;

    public AddPlaylistViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;

        playlistTitle = (TextView) itemView.findViewById(R.id.add_a_playlist_header_text);
        image = (ImageView) itemView.findViewById(R.id.add_a_playlist_image);
    }

    public void bind(final Item item, final PlaylistAdapter.onItemClickListener listener) {
        if(item != null){
                playlistTitle.setText(item.getName());
                Picasso.with(itemView.getContext()).load(R.drawable.ic_add_copy).error(R.drawable.ic_menu_gallery).into(image);
        }

        itemView.setOnClickListener(view ->
                listener.onItemClick(item));
    }
}
