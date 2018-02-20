package com.alisonjc.compmusicplayer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.alisonjc.compmusicplayer.spotify.SpotifyHelper;
import com.alisonjc.compmusicplayer.util.Constants;
import com.alisonjc.compmusicplayer.util.EndlessScrollListener;
import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.callbacks.IOnTrackSelected;
import com.alisonjc.compmusicplayer.databinding.TrackItemModel;
import com.alisonjc.compmusicplayer.spotify.SpotifyService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.view.View.GONE;

public class PlaylistTracksFragment extends Fragment implements IOnTrackChanged, IOnTrackSelected {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private IOnTrackSelected mListener;
    private LinearLayoutManager mLayoutManager;
    private List<TrackItemModel> mPlaylistTracksList;
    private TracksAdapter mAdapter;
    private View rootView;
    private TextView mHintText;
    private String mPlaylistId;
    private String mUserId;
    private int mItemPosition = 0;
    private int mTotalTracks = 0;
    private int mOffset;
    private int mLimit = 20;
    private String mSongtitle;
    private String mPlaylistTitle;

    private static final String TAG = "PlaylistTracksFragment";
    private SpotifyService mSpotifyService = SpotifyService.getSpotifyService();

    public PlaylistTracksFragment() {
    }

    public static PlaylistTracksFragment newInstance(String userId, String playlistId, String playlistTitle) {
        PlaylistTracksFragment fragment = new PlaylistTracksFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        args.putString("playlistId", playlistId);
        args.putString("playlistTitle", playlistTitle);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlaylistId = getArguments().getString("playlistId");
        mUserId = getArguments().getString("userId");
        mPlaylistTitle = getArguments().getString("playlistTitle");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.list, container, false);
        mHintText = (TextView) rootView.findViewById(R.id.hint_text);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewSetup();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        if (mPlaylistTracksList.size() <= 0) {
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

    private void recyclerViewSetup() {
        mPlaylistTracksList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        loadMoreDataFromApi(mOffset);

        mAdapter = new TracksAdapter<>(mPlaylistTracksList, getContext(), (item, position) -> {
            mItemPosition = position;
            PlaylistTracksFragment.this.setCurrentPlayingSong(mItemPosition);
        }, (action, item, position, songTitle) -> {
            mSongtitle = songTitle;
            if (action == Constants.REMOVE) {
                SpotifyHelper.removeTrackFromPlaylist(PlaylistTracksFragment.this.getActivity(), mPlaylistId, item.getUri(), mPlaylistTitle, mSongtitle, mSpotifyService);
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new EndlessScrollListener(mLayoutManager, mTotalTracks) {
            @Override
            public void onLoadMore(int offset) {
                mOffset = offset;
                loadMoreDataFromApi(mOffset);
            }
        });
    }

    public void loadMoreDataFromApi(final int offset) {
        mSpotifyService.getPlaylistTracks(mUserId, mPlaylistId, offset, mLimit)
                .flatMapIterable(playlistTracksList -> {
                    mTotalTracks = playlistTracksList.getTotal();
                    return playlistTracksList.getItems();
                })
                .map(item -> new TrackItemModel(item))
                .toList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(playlistTracksList -> {
                        mAdapter.updateAdapter(playlistTracksList);
                }, throwable -> {
                    mSpotifyService.userLogout(getContext());
                }, () -> {
                });
    }

    private void setCurrentPlayingSong(int itemPosition) {
        Log.i(TAG, "setCurrentPlayingSong");
        this.mItemPosition = itemPosition;
        mAdapter.recyclerViewSelector(mItemPosition);
        mRecyclerView.smoothScrollToPosition(mItemPosition);
        onSongSelected(mPlaylistTracksList.get(itemPosition).getSongName(), mPlaylistTracksList.get(itemPosition).getArtist(), mPlaylistTracksList.get(itemPosition).getUri());
    }

    public void onSongSelected(String songName, String artistName, String uri) {
        Log.i(TAG, "onSongSelected");
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
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onControllerTrackChange(boolean skipforward) {
        if (skipforward) {
            if (mAdapter.getItemCount() <= mItemPosition + 1) {
                mItemPosition = 0;
                setCurrentPlayingSong(mItemPosition);
            } else {
                setCurrentPlayingSong(mItemPosition + 1);
                Log.i(TAG, "onControllerTrackChangeFORWARD");
            }
        } else {
            if (mItemPosition < 1) {
                mItemPosition = 0;
                setCurrentPlayingSong(mItemPosition);
            } else {
                setCurrentPlayingSong(mItemPosition - 1);
                Log.i(TAG, "onControllerTrackChangeBACK");
            }
        }
    }

    @Override
    public void onTrackSelected(String trackName, String artistName, String uri) {
        Log.i(TAG, "onTrackSelected");
    }
}
