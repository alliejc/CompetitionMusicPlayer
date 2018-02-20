//package com.alisonjc.compmusicplayer.viewholder;
//
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.alisonjc.compmusicplayer.R;
//import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistModel.Item;
//import com.alisonjc.compmusicplayer.spotify.spotify_model.UserTracksModel.Album;
//import com.squareup.picasso.Picasso;
//
///**
// * Created by acaldwell on 2/19/18.
// */
//
//public class AlbumViewHolder extends RecyclerView.ViewHolder {
//    public TextView albumtitle;
//    public TextView songCount;
//    public ImageView image;
//    public View itemView;
//    public ImageView overflow;
//
//    public AlbumViewHolder(View itemView) {
//        super(itemView);
//        this.itemView = itemView;
//
//        albumtitle = (TextView) itemView.findViewById(R.id.playlist_header_text);
//        songCount = (TextView) itemView.findViewById(R.id.playlist_sub_text);
//        image = (ImageView) itemView.findViewById(R.id.playlist_image);
//        overflow = (ImageView) itemView.findViewById(R.id.menu_icon);
//    }
//
//    public void bind(Album album, String url, final AlbumAdapter.onItemClickListener listener) {
//        if(album != null){
//            if(album.getName() != null){
//                albumtitle.setText(album.getName());
//            }
////            Picasso.with(itemView.getContext()).load(url).error(R.drawable.ic_menu_gallery).into(image);
//        }
//
//        itemView.setOnClickListener(view -> listener.onItemClick(album, getAdapterPosition()));
//    }
//}
