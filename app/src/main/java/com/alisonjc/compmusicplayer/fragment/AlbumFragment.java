//package com.alisonjc.compmusicplayer.fragment;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.StaggeredGridLayoutManager;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.alisonjc.compmusicplayer.R;
//import com.alisonjc.compmusicplayer.adapter.AlbumAdapter;
//import com.alisonjc.compmusicplayer.adapter.PlaylistAdapter;
//import com.alisonjc.compmusicplayer.adapter.TracksAdapter;
//import com.alisonjc.compmusicplayer.callbacks.IOnOverflowSelected;
//import com.alisonjc.compmusicplayer.databinding.TrackItemModel;
//import com.alisonjc.compmusicplayer.spotify.SpotifyService;
//import com.alisonjc.compmusicplayer.spotify.spotify_model.UserTracksModel.Album;
//import com.alisonjc.compmusicplayer.spotify.spotify_model.UserTracksModel.UserAlbumsList;
//import com.alisonjc.compmusicplayer.util.EndlessScrollListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.ButterKnife;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.functions.Func1;
//import rx.schedulers.Schedulers;
//
///**
// * Created by acaldwell on 2/19/18.
// */
//
//public class AlbumFragment extends Fragment {
//    private SpotifyService mSpotifyService = SpotifyService.getSpotifyService();
//    List<Album> mAlbums = new ArrayList<>();
//    private int mTotalAlbums = 0;
//    private AlbumAdapter mAdapter;
//    private RecyclerView mRecyclerView;
//    private LinearLayoutManager mLayoutManager;
//    private int mItemPosition = 0;
//    private View mRootview;
//
//    public AlbumFragment() {
//    }
//
//    public static AlbumFragment newInstance() {
//        AlbumFragment fragment = new AlbumFragment();
//
//        return fragment;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        mRootview = inflater.inflate(R.layout.list, container, false);
//        ButterKnife.bind(this, mRootview);
////        mHintText = (TextView) mRootview.findViewById(R.id.hint_text);
//        mRecyclerView = (RecyclerView) mRootview.findViewById(R.id.recycler_view);
//
//        return mRootview;
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        recyclerViewSetup();
//    }
//
//    private void recyclerViewSetup() {
//        mAlbums = new ArrayList<>();
//        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.setHasFixedSize(true);
//        loadDataFromApi();
//
//        mAdapter = new AlbumAdapter(mAlbums, getActivity(), new AlbumAdapter.onItemClickListener() {
//            @Override
//            public void onItemClick(Album album, int position) {
//                mItemPosition = position;
//            }
//        });
//
//        mRecyclerView.setAdapter(mAdapter);
//        mAdapter.updateAdapter(mAlbums);
//
////        mAdapter = new AlbumAdapter(mAlbums, getActivity(), new TracksAdapter.OnItemClickListener() {
////            @Override
////            public void onItemClick(Object item, int position) {
////                mItemPosition = position;
//////            setCurrentPlayingSong(position);
////            }
////        }, new IOnOverflowSelected() {
////            @Override
////            public void onOverflowClicked(int action, TrackItemModel item, int position, String songTitle) {
////                mItemPosition = position;
//////            showPlaylistActionDialog(item.getUri(), action, songTitle);
////            }
////        });
//    }
//
//    private void loadDataFromApi(){
//        mSpotifyService.getSavedAlbums()
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(albums -> {
//                    if (albums != null) {
//                        mAlbums.add(albums);
//                    } else {
//                        mSpotifyService.userLogout(getContext());
//                    }
//                }, throwable -> {
//                }, () -> {
//                });
//    }
//
//
////    public void loadDataFromApi() {
////        mSpotifyService.getSavedAlbums()
////                .flatMapIterable(savedAlbum -> {
////                    mTotalAlbums = savedAlbum.getTotal();
////                    Log.e("savedAlbun", savedAlbum.getNext());
////                    return savedAlbum.getItems();
////                })
////                .map(item -> new Album())
////                .toList()
////                .subscribeOn(Schedulers.newThread())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(albums -> {
////                    mAdapter.updateAdapter(albums);
////                }, throwable -> {
////                    mSpotifyService.userLogout(getActivity());
////                }, () -> {
////                });
////    }
//}
