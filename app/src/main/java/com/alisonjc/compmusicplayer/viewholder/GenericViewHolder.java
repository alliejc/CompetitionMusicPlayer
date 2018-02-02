package com.alisonjc.compmusicplayer.viewholder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.alisonjc.compmusicplayer.BR;
import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.adapter.TracksAdapter;
import com.alisonjc.compmusicplayer.databinding.RecyclerviewItemBinding;
import com.alisonjc.compmusicplayer.databinding.TrackItemModel;
import com.alisonjc.compmusicplayer.databinding.TrackRecyclerItemModel;
import com.squareup.picasso.Picasso;


public class GenericViewHolder extends RecyclerView.ViewHolder {
    private RecyclerviewItemBinding mRecyclerviewItemBinding;
    public ImageView imageView;
    public ImageView overflowIcon;

    public GenericViewHolder(RecyclerviewItemBinding itemBinding) {
        super(itemBinding.getRoot());
        this.mRecyclerviewItemBinding = itemBinding;
        imageView = (ImageView) itemView.findViewById(R.id.track_image);
        overflowIcon = (ImageView) imageView.findViewById(R.id.track_overflow_icon);
    }

    public void bindItem(TrackRecyclerItemModel trackRecyclerItemModel) {
        mRecyclerviewItemBinding.setVariable(BR.recycler_view_item, trackRecyclerItemModel);
        mRecyclerviewItemBinding.executePendingBindings();
    }

    public void bindItemListener(TrackItemModel item, final TracksAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(view -> {
            listener.onItemClick(item, getAdapterPosition());
        });
    }
}