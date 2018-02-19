package com.alisonjc.compmusicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistModel.Item;
import com.alisonjc.compmusicplayer.util.Constants;
import com.alisonjc.compmusicplayer.viewholder.AddPlaylistViewHolder;
import com.alisonjc.compmusicplayer.viewholder.PlaylistActionViewHolder;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

/**
 * Created by acaldwell on 2/18/18.
 */

public class ActionPlaylistAdapter extends RecyclerView.Adapter<PlaylistActionViewHolder>{

        private List<Item> mPlaylistItemList;
        private Context mContext;
        private int mAction;
        private OnPlaylistAction mListener;

    public interface OnPlaylistAction{
        void onPlaylistAction(String uri, String playlistId, String playlistTitle, int action);
    }

        public ActionPlaylistAdapter(Context context, List<Item> playlistItemList, int action, OnPlaylistAction l) {
            this.mPlaylistItemList = playlistItemList;
            this.mContext = context;
            this.mAction = action;
            this.mListener = l;
        }

        @Override
        public PlaylistActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);

            View v = inflater.inflate(R.layout.item_list, parent, false);
                return new PlaylistActionViewHolder(v);
        }

    @Override
        public void onBindViewHolder(PlaylistActionViewHolder holder, int position) {
            Item item = mPlaylistItemList.get(position);

                List<Object> o = item.getImages();
                LinkedTreeMap map = new LinkedTreeMap();
            if(o.size() > 0) {
                if (o.get(0) != null) {
                    map = (LinkedTreeMap) o.get(0);
                }
            }

                String url = (String) map.get("url");
                holder.bind(item, url, mAction, mListener);
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
