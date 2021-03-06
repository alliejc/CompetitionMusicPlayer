package com.alisonjc.compmusicplayer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.adapter.PlaylistAdapter;
import com.alisonjc.compmusicplayer.callbacks.IOnPlaylistSelected;
import com.alisonjc.compmusicplayer.spotify.SpotifyHelper;
import com.alisonjc.compmusicplayer.spotify.SpotifyService;
import com.alisonjc.compmusicplayer.spotify.action_models.CreatePlaylist;
import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistModel.Item;
import com.alisonjc.compmusicplayer.util.Util;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class PlaylistFragment extends Fragment implements IOnPlaylistSelected {
    private static final String TAG = PlaylistFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private PlaylistAdapter mAdapter;
    private List<Item> mPlaylistItemList;
    private IOnPlaylistSelected mListener;
    private SpotifyService mSpotifyService = SpotifyService.getSpotifyService();

    public PlaylistFragment() {
    }

    public static PlaylistFragment newInstance() {
        return new PlaylistFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            mPlaylistItemList = new ArrayList<>();
            recyclerViewSetup();
            loadDataFromApi();
        }
    }

    private void recyclerViewSetup() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new PlaylistAdapter(getContext(), mPlaylistItemList, item -> {
            String userId = item.getOwner().getId();
            String playlistId = item.getId();
            String playlistTitle = item.getName();

            mListener.onPlaylistSelected(userId, playlistId, playlistTitle);
        }, title -> {
            CreatePlaylist item = new CreatePlaylist();
                if (title != null && !title.isEmpty()){
                    item.setName(title);
                    item.setPublic(false);
                    Util.closeKeyboard(getActivity());
                    SpotifyHelper.createPlaylist(mSpotifyService, getActivity(), item);
                 } else {
                    Util.closeKeyboard(getActivity());
                    String message = getString(R.string.enter_playlist_title);
                    Util.showSnackBar(getActivity(), message);
                }
        });
        mRecyclerView.setAdapter(mAdapter);
   }

    private void loadDataFromApi(){
            mSpotifyService.getUserPlayLists()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(userPlaylists -> {
                        if (userPlaylists != null) {
                            mAdapter.updateAdapter(userPlaylists.getItems());
                        } else {
                            mSpotifyService.userLogout(getContext());
                        }
                    }, throwable -> {
                    }, () -> {
                    });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IOnPlaylistSelected) {
            mListener = (IOnPlaylistSelected) context;
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
    public void onPlaylistSelected(String userId, String playlistId, String playlistTitle) {
    }
}


