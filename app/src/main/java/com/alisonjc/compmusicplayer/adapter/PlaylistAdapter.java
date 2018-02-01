package com.alisonjc.compmusicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistModel.Item;
import com.alisonjc.compmusicplayer.viewholder.PlaylistViewHolder;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistViewHolder> {

    private List<Item> mPlaylistItemList;
    private Context mContext;
    private final onItemClickListener listener;

    public interface onItemClickListener {
        void onItemClick(Item item);
    }

    public PlaylistAdapter(Context context, List<Item> playlistItemList, onItemClickListener listener) {

        this.mPlaylistItemList = playlistItemList;
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.item_playlist, parent, false);
        PlaylistViewHolder holder = new PlaylistViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {

        Item item = mPlaylistItemList.get(position);

        List<Object> o = item.getImages();
        LinkedTreeMap map = new LinkedTreeMap();
        if(o.get(0) != null){
            map = (LinkedTreeMap) o.get(0);
        }

        String url = (String) map.get("url");

        holder.bind(item, url, listener);
    }

    public void updateAdapter(List<Item> items) {

        mPlaylistItemList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mPlaylistItemList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}