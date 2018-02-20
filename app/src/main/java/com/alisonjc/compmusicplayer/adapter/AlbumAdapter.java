//package com.alisonjc.compmusicplayer.adapter;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.alisonjc.compmusicplayer.R;
//import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistModel.Item;
//import com.alisonjc.compmusicplayer.spotify.spotify_model.UserTracksModel.Album;
//import com.alisonjc.compmusicplayer.spotify.spotify_model.UserTracksModel.UserAlbumsList;
//import com.alisonjc.compmusicplayer.viewholder.AlbumViewHolder;
//import com.alisonjc.compmusicplayer.viewholder.PlaylistActionViewHolder;
//
//import java.util.List;
//
///**
// * Created by acaldwell on 2/19/18.
// */
//
//public class AlbumAdapter extends RecyclerView.Adapter<AlbumViewHolder> {
//    private List<Album> mList;
//    private Context mContext;
//    private onItemClickListener listener;
//
//    public interface onItemClickListener {
//        void onItemClick(Album album, int position);
//    }
//
//    public AlbumAdapter(List<Album> mList, Context mContext, onItemClickListener listener) {
//        this.mList = mList;
//        this.mContext = mContext;
//        this.listener = listener;
//    }
//
//    @Override
//    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//
//        View v = inflater.inflate(R.layout.item_playlist, parent, false);
//        return new AlbumViewHolder(v);
//
//    }
//
//    @Override
//    public void onBindViewHolder(AlbumViewHolder holder, int position) {
//        Album album = mList.get(holder.getAdapterPosition());
//        holder.bind(album, album.getUri(), listener);
//    }
//
//    @Override
//    public int getItemCount() {
//        return mList.size();
//    }
//
//    public void updateAdapter(List<Album> items) {
//        mList.addAll(items);
//        notifyDataSetChanged();
//    }
//}
