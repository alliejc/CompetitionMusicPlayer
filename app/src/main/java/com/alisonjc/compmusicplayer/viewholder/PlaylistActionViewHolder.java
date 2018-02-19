package com.alisonjc.compmusicplayer.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.adapter.ActionPlaylistAdapter;
import com.alisonjc.compmusicplayer.adapter.PlaylistAdapter;
import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistModel.Item;
import com.alisonjc.compmusicplayer.util.Constants;
import com.squareup.picasso.Picasso;

/**
 * Created by acaldwell on 2/18/18.
 */

public class PlaylistActionViewHolder extends RecyclerView.ViewHolder {
    public TextView playlistTitle;
    public TextView songCount;
    public ImageView image;
    public View itemView;
    public ImageView icon;

    public PlaylistActionViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;

        playlistTitle = (TextView) itemView.findViewById(R.id.header_text);
        songCount = (TextView) itemView.findViewById(R.id.sub_text);
        image = (ImageView) itemView.findViewById(R.id.image);
        icon = (ImageView) itemView.findViewById(R.id.action_icon);
    }

    public void bind(final Item item, String url, int action, ActionPlaylistAdapter.OnPlaylistAction listener) {
        if(item != null){
            if(item.getName() != null){
                playlistTitle.setText(item.getName());
            }
            Picasso.with(itemView.getContext()).load(url).error(R.drawable.ic_menu_gallery).into(image);
            if(item.getTracks() != null){
                songCount.setText(item.getTracks().getTotal().toString() + " songs");
            }
            if(action == Constants.ADD){
                icon.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_add));
            } else if (action == Constants.REMOVE){

            }
        }

        itemView.setOnClickListener(view ->
                listener.onPlaylistAction(item.getUri(), item.getId(), item.getName(), action));
    }
}
