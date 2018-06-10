package com.alisonjc.compmusicplayer.viewholder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.alisonjc.compmusicplayer.BR;
import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.adapter.TracksAdapter;
import com.alisonjc.compmusicplayer.callbacks.IOnOverflowSelected;
import com.alisonjc.compmusicplayer.databinding.ItemTrackRecyclerBinding;
import com.alisonjc.compmusicplayer.databinding.TrackItemModel;
import com.alisonjc.compmusicplayer.databinding.TrackRecyclerItemModel;
import com.alisonjc.compmusicplayer.util.Util;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;


public class GenericViewHolder extends RecyclerView.ViewHolder {
    private ItemTrackRecyclerBinding mItemTrackRecyclerBinding;
    public ImageView imageView;
    public View layout;
    public ImageView menuIcon;

    public GenericViewHolder(ItemTrackRecyclerBinding itemBinding) {
        super(itemBinding.getRoot());
        this.mItemTrackRecyclerBinding = itemBinding;
        imageView = (ImageView) itemView.findViewById(R.id.track_image);
        layout = (View) itemView.findViewById(R.id.track_layout);
        menuIcon = (ImageView) itemView.findViewById(R.id.track_menu_icon);
    }

    public void bindItem(TrackRecyclerItemModel trackRecyclerItemModel) {
        mItemTrackRecyclerBinding.setVariable(BR.recycler_view_item, trackRecyclerItemModel);
        mItemTrackRecyclerBinding.executePendingBindings();
    }

    public void bindItemListener(TrackItemModel item, final TracksAdapter.OnItemClickListener listener) {
        layout.setOnClickListener(view -> {
            listener.onItemClick(item, getAdapterPosition());
        });
    }
}