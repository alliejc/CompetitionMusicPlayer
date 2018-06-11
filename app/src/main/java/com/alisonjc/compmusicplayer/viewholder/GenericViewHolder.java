package com.alisonjc.compmusicplayer.viewholder;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.alisonjc.compmusicplayer.BR;
import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.adapter.TracksAdapter;
import com.alisonjc.compmusicplayer.databinding.ItemTrackRecyclerBinding;
import com.alisonjc.compmusicplayer.databinding.TrackItemModel;
import com.alisonjc.compmusicplayer.databinding.TrackRecyclerItemModel;

import io.gresse.hugo.vumeterlibrary.VuMeterView;


public class GenericViewHolder extends RecyclerView.ViewHolder {
    private ItemTrackRecyclerBinding mItemTrackRecyclerBinding;
    public ImageView imageView;
    public View layout;
    public ImageView menuIcon;
    public VuMeterView equalizer;


    public GenericViewHolder(ItemTrackRecyclerBinding itemBinding) {
        super(itemBinding.getRoot());
        this.mItemTrackRecyclerBinding = itemBinding;
        imageView = (ImageView) itemView.findViewById(R.id.track_image);
        layout = (View) itemView.findViewById(R.id.track_layout);
        menuIcon = (ImageView) itemView.findViewById(R.id.track_menu_icon);
        equalizer = (VuMeterView) itemView.findViewById(R.id.equalizer);
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