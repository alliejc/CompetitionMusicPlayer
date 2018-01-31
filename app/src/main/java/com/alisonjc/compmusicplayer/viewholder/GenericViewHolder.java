package com.alisonjc.compmusicplayer.viewholder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.alisonjc.compmusicplayer.BR;
import com.alisonjc.compmusicplayer.adapter.TracksRecyclerAdapter;
import com.alisonjc.compmusicplayer.databinding.RecyclerviewItemBinding;
import com.alisonjc.compmusicplayer.databinding.TrackItemModel;
import com.alisonjc.compmusicplayer.databinding.TrackRecyclerItemModel;


public class GenericViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "GenericViewHolder";
    private RecyclerviewItemBinding mRecyclerviewItemBinding;

    public GenericViewHolder(RecyclerviewItemBinding itemBinding) {
        super(itemBinding.getRoot());
        this.mRecyclerviewItemBinding = itemBinding;
    }

    public void bindItem(TrackRecyclerItemModel trackRecyclerItemModel) {
        mRecyclerviewItemBinding.setVariable(BR.recycler_view_item, trackRecyclerItemModel);
        mRecyclerviewItemBinding.executePendingBindings();
    }

    public void bindItemListener(TrackItemModel item, final TracksRecyclerAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(view -> {
            Log.i(TAG, "itemView OnClick");

            listener.onItemClick(item, getAdapterPosition());
        });
    }
}