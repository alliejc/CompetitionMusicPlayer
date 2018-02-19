package com.alisonjc.compmusicplayer.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistModel.Item;
import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistModel.Owner;
import com.alisonjc.compmusicplayer.viewholder.AddPlaylistViewHolder;
import com.alisonjc.compmusicplayer.viewholder.PlaylistActionViewHolder;
import com.alisonjc.compmusicplayer.viewholder.PlaylistViewHolder;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;
    private static final int ITEM_ACTION_VIEW = 2;

    private List<Item> mPlaylistItemList;
    private Context mContext;
    private final onItemClickListener listener;
    private String mTag;

    public interface onItemClickListener {
        void onItemClick(Item item);
    }

    public PlaylistAdapter(Context context, List<Item> playlistItemList, onItemClickListener listener) {
        this.mPlaylistItemList = playlistItemList;
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View v;
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            v = inflater.inflate(R.layout.item_add_playlist, parent, false);
            return new AddPlaylistViewHolder(v);
        } else {
                v = inflater.inflate(R.layout.item_playlist, parent, false);
                return new PlaylistViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Item item = mPlaylistItemList.get(position);

        if(getItemViewType(position) == ITEM_VIEW_TYPE_HEADER) {
            List<Object> o = item.getImages();

            if (o.get(0) != null) {
                ((AddPlaylistViewHolder) holder).bind(item, listener);
            }

        } else if (getItemViewType(position) == ITEM_VIEW_TYPE_ITEM){
            List<Object> o = item.getImages();
            LinkedTreeMap map = new LinkedTreeMap();
            if (o.get(0) != null) {
                map = (LinkedTreeMap) o.get(0);
            }

            String url = (String) map.get("url");
            ((PlaylistViewHolder)holder).bind(item, url, listener);
        }
    }

    public void updateAdapter(List<Item> items) {
        mPlaylistItemList.addAll(items);
        addPlaylist();
        notifyDataSetChanged();
    }

    public boolean isHeader(Item item) {
        return item.getName().equals("Add a Playlist");
    }

    @Override
    public int getItemViewType(int position) {
            return isHeader(mPlaylistItemList.get(position)) ?
                    ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

        public void addPlaylist(){
        List<Object> addImage = new ArrayList<>();
        addImage.add(mContext.getString(R.string.add_a_playlist_image));

        Owner owner = new Owner();
        owner.setId("Add a Playlist");

        Item item = new Item();
        item.setName("Add a Playlist");
        item.setId("Add a Playlist");
        item.setOwner(owner);
        item.setImages(addImage);

            mPlaylistItemList.add(0, item);
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
