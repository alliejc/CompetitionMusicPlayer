package com.alisonjc.compmusicplayer.viewholder;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.adapter.PlaylistAdapter;
import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistModel.Item;
import com.alisonjc.compmusicplayer.util.Constants;
import com.squareup.picasso.Picasso;

public class PlaylistViewHolder extends RecyclerView.ViewHolder {

    public TextView playlistTitle;
    public TextView songCount;
    public ImageView image;
    public View itemView;
    public ImageView overflow;

    public PlaylistViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;

        playlistTitle = (TextView) itemView.findViewById(R.id.playlist_header_text);
        songCount = (TextView) itemView.findViewById(R.id.playlist_sub_text);
        image = (ImageView) itemView.findViewById(R.id.playlist_image);
        overflow = (ImageView) itemView.findViewById(R.id.menu_icon);
    }

    public void bind(final Item item, String url, final PlaylistAdapter.onItemClickListener listener) {
        if(item != null){
            if(item.getName() != null){
                playlistTitle.setText(item.getName());
            }
//            if(item.getName().equals(Constants.ADD_PLAYLIST)){
//                Picasso.with(itemView.getContext()).load(R.drawable.ic_add_copy).error(R.drawable.ic_menu_gallery).into(image);
//            } else {
                Picasso.with(itemView.getContext()).load(url).error(R.drawable.ic_menu_gallery).into(image);
//            }
            if(item.getTracks() != null){
                songCount.setText(item.getTracks().getTotal().toString() + " songs");
            }
        }

        itemView.setOnClickListener(view ->
                listener.onItemClick(item));
    }
}
