package com.alisonjc.compmusicplayer.adapter;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.databinding.TrackItemModel;
import com.alisonjc.compmusicplayer.databinding.TrackRecyclerItemModel;
import com.alisonjc.compmusicplayer.databinding.RecyclerviewItemBinding;
import com.alisonjc.compmusicplayer.viewholder.GenericViewHolder;

import java.util.List;


public class TracksAdapter<T> extends RecyclerView.Adapter<GenericViewHolder> {

    private List<TrackItemModel> mList;
    private Context mContext;
    private OnItemClickListener mListener;
    private int selectedItem = -1;

    public interface OnItemClickListener {
        void onItemClick(Object item, int position);
    }

    public TracksAdapter(List<TrackItemModel> list, Context context, OnItemClickListener listener) {
        mList = list;
        mContext = context;
        mListener = listener;
    }

    @Override
    public GenericViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        RecyclerviewItemBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.recyclerview_item, parent, false);

        return new GenericViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(final GenericViewHolder holder, final int position) {
        TrackItemModel item = mList.get(position);
        TrackRecyclerItemModel trackRecyclerItemModel = new TrackRecyclerItemModel(item);

        holder.bindItem(trackRecyclerItemModel);
        holder.bindItemListener(item, mListener);
        holder.itemView.setSelected(selectedItem == position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void recyclerViewSelector(int position) {
        notifyItemChanged(selectedItem);
        selectedItem = position;
        notifyItemChanged(selectedItem);
    }

    public void updateAdapter(List<TrackItemModel> items) {
        mList.addAll(items);
        notifyDataSetChanged();
    }
}