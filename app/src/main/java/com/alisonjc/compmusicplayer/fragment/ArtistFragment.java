//package com.alisonjc.compmusicplayer.fragment;
//
//import android.app.Fragment;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.alisonjc.compmusicplayer.R;
//import com.alisonjc.compmusicplayer.adapter.TracksAdapter;
//import com.alisonjc.compmusicplayer.databinding.TrackItemModel;
//import com.alisonjc.compmusicplayer.spotify.SpotifyService;
//import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistModel.Artist;
//import com.alisonjc.compmusicplayer.util.EndlessScrollListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.ButterKnife;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//
///**
// * Created by acaldwell on 2/19/18.
// */
//
//public class ArtistFragment extends Fragment {
//    private SpotifyService mSpotifyService = SpotifyService.getSpotifyService();
//
//    private View mRootView;
//    private TextView mHintText;
//    private RecyclerView mRecyclerView;
//    private List<TrackItemModel> mArtistList;
//    private LinearLayoutManager mLayoutManager;
//    private List<TrackItemModel> mTracksList;
//    private TracksAdapter mAdapter;
//    private int mOffset;
//    private int mItemPosition = 0;
//
//    public ArtistFragment() {
//    }
//
//    public static ArtistFragment newInstance() {
//        ArtistFragment fragment = new ArtistFragment();
//        return fragment;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        mRootView = inflater.inflate(R.layout.list, container, false);
//        ButterKnife.bind(this, mRootView);
//        mHintText = (TextView) mRootView.findViewById(R.id.hint_text);
//        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
//
//        return mRootView;
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        recyclerViewSetup();
//    }
//
//    private void recyclerViewSetup() {
//        mArtistList = new ArrayList<>();
//        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.setHasFixedSize(true);
//        loadDataFromApi(mOffset);
//
//        mAdapter = new TracksAdapter<>(mTracksList, getActivity(), (item, position) -> {
//            mItemPosition = position;
////            setCurrentPlayingSong(position);
//        }, (action, item, position, songTitle) -> {
//            mItemPosition = position;
////            showPlaylistActionDialog(item.getUri(), action, songTitle);
//        });
//
//        mRecyclerView.setAdapter(mAdapter);
//
//        mRecyclerView.addOnScrollListener(new EndlessScrollListener(mLayoutManager, mTotalTracks) {
//            @Override
//            public void onLoadMore(int offset) {
//                mOffset = offset;
//                loadDataFromApi(mOffset);
//            }
//        });
//    }
//}
