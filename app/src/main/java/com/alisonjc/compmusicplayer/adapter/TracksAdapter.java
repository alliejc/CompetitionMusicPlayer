package com.alisonjc.compmusicplayer.adapter;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.callbacks.IOnOverflowSelected;
import com.alisonjc.compmusicplayer.databinding.TrackItemModel;
import com.alisonjc.compmusicplayer.databinding.TrackRecyclerItemModel;
import com.alisonjc.compmusicplayer.databinding.RecyclerviewItemBinding;
import com.alisonjc.compmusicplayer.util.Constants;
import com.alisonjc.compmusicplayer.viewholder.GenericViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;


public class TracksAdapter<T> extends RecyclerView.Adapter<GenericViewHolder> {

    private List<TrackItemModel> mList;
    private Context mContext;
    private OnItemClickListener mListener;
    private IOnOverflowSelected mOverflowSelected;
    private int selectedItem = -1;

    public interface OnItemClickListener {
        void onItemClick(Object item, int position);
    }

    public TracksAdapter(List<TrackItemModel> list, Context context, OnItemClickListener listener, IOnOverflowSelected overflow) {
        mList = list;
        mContext = context;
        mListener = listener;
        mOverflowSelected = overflow;
    }

    @Override
    public GenericViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        RecyclerviewItemBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.recyclerview_item, parent, false);

        return new GenericViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(final GenericViewHolder holder, final int position) {
        TrackItemModel item = mList.get(holder.getAdapterPosition());
        TrackRecyclerItemModel trackRecyclerItemModel = new TrackRecyclerItemModel(item);

        holder.bindItem(trackRecyclerItemModel);
        holder.bindItemListener(item, mListener);
        holder.itemView.setSelected(selectedItem == holder.getAdapterPosition());
        holder.menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(holder.menuIcon, holder.getAdapterPosition(), item);
                    }
                });

        if(selectedItem == holder.getAdapterPosition()) {
            holder.imageView.setImageDrawable(mContext.getDrawable(R.drawable.vector_equalizer));
        } else {
            Picasso.with(holder.itemView.getContext()).load(item.getImage()).error(R.drawable.ic_menu_gallery).into(holder.imageView);
        }
    }

    private void showPopup(View anchor, int position, TrackItemModel trackItemModel){
        PopupMenu popup = new PopupMenu(mContext, anchor);
        popup.inflate(R.menu.track_overflow);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_to_playlist:
                        Log.e("add to playlist", "add");
                        mOverflowSelected.onOverflowClicked(Constants.ADD, trackItemModel, position, trackItemModel.getSongName());
                        break;
                    case R.id.remove_from_playlist:
                        Log.e("remove from playlist", "remove");
                        mOverflowSelected.onOverflowClicked(Constants.REMOVE, trackItemModel, position, trackItemModel.getSongName());
                        break;
                }
                return false;
            }
        });
        popup.show();
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
