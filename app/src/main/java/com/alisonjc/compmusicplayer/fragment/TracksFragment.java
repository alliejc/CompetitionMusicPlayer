package com.alisonjc.compmusicplayer.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alisonjc.compmusicplayer.adapter.TracksAdapter;
import com.alisonjc.compmusicplayer.callbacks.IOnOverflowSelected;
import com.alisonjc.compmusicplayer.callbacks.IOnTrackChanged;
import com.alisonjc.compmusicplayer.util.EndlessScrollListener;
import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.callbacks.IOnTrackSelected;
import com.alisonjc.compmusicplayer.databinding.TrackItemModel;
import com.alisonjc.compmusicplayer.spotify.SpotifyService;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.view.View.GONE;


public class TracksFragment extends Fragment implements IOnTrackChanged, IOnTrackSelected {

    private IOnTrackSelected mListener;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<TrackItemModel> mTracksList;
    private TracksAdapter mAdapter;
    private View rootView;
    private TextView mHintText;
    private int mItemPosition = 0;
    private int mTotalTracks = 0;
    private int mOffset;
    private int mLimit = 20;
    private static final String TAG = "TracksFragment";
    private SpotifyService mSpotifyService = SpotifyService.getSpotifyService();

    public TracksFragment() {
    }

    public static TracksFragment newInstance() {
        TracksFragment fragment = new TracksFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.list, container, false);
        ButterKnife.bind(this, rootView);
        mHintText = (TextView) rootView.findViewById(R.id.hint_text);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewSetup();
    }

    private void recyclerViewSetup() {
        mTracksList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        loadDataFromApi(mOffset);

        mAdapter = new TracksAdapter<>(mTracksList, getActivity(), (item, position) -> {
            mItemPosition = position;
            setCurrentPlayingSong(position);
        }, (action, item, position, songTitle) -> {
            mItemPosition = position;
            showPlaylistActionDialog(item.getUri(), action, songTitle);
        });

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new EndlessScrollListener(mLayoutManager, mTotalTracks) {
            @Override
            public void onLoadMore(int offset) {
                mOffset = offset;
                loadDataFromApi(mOffset);
            }
        });
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        if (mTracksList.size() <= 0) {
//            showHintText();
//        } else {
//            hideHintText();
//        }
//    }

    private void showHintText() {
        if (mHintText.getVisibility() == GONE) {
            mHintText.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(GONE);
        }
    }

    private void hideHintText() {
        if (mHintText.getVisibility() == View.VISIBLE) {
            mHintText.setVisibility(GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showPlaylistActionDialog(String uri, int action, String songTitle){
        PlaylistActionDialog frag = PlaylistActionDialog.newInstance(uri, action, songTitle);
        FragmentManager manager = getFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("uri", uri);
        bundle.putInt("action", action);
        bundle.putString("songTitle", songTitle);
        frag.setArguments(bundle);
        frag.show(manager, "dialog");
    }

    public void loadDataFromApi(final int offset) {
        mSpotifyService.getUserTracks(offset, mLimit)
                .flatMapIterable(userTracks -> {
                    mTotalTracks = userTracks.getTotal();
                    return userTracks.getItems();
                })
                .map(item -> new TrackItemModel(item))
                .toList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userTracks -> {
                    mAdapter.updateAdapter(userTracks);
                }, throwable -> {
                    mSpotifyService.userLogout(getActivity());
                }, () -> {
                });
    }

    private void setCurrentPlayingSong(int itemPosition) {
        this.mItemPosition = itemPosition;
        mAdapter.recyclerViewSelector(mItemPosition);
        mRecyclerView.smoothScrollToPosition(mItemPosition);
        onSongSelected(mTracksList.get(itemPosition).getSongName(), mTracksList.get(itemPosition).getArtist(), mTracksList.get(itemPosition).getUri());
    }

    public void onSongSelected(String songName, String artistName, String uri) {
        if (mListener != null) {
            mListener.onTrackSelected(songName, artistName, uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IOnTrackSelected) {
            mListener = (IOnTrackSelected) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTracksInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onControllerTrackChange(boolean skipforward) {
        if (skipforward) {
            if (mAdapter.getItemCount() <= mItemPosition + 1) {
                mItemPosition = 0;
                setCurrentPlayingSong(mItemPosition);
            } else {
                setCurrentPlayingSong(mItemPosition + 1);
            }

        } else {
            if (mItemPosition < 1) {
                mItemPosition = 0;
                setCurrentPlayingSong(mItemPosition);
            } else {
                setCurrentPlayingSong(mItemPosition - 1);
            }
        }
    }

    @Override
    public void onTrackSelected(String trackName, String artistName, String uri) {
    }
}
