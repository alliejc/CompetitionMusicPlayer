package com.alisonjc.compmusicplayer.playlists;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.RecyclerDivider;
import com.alisonjc.compmusicplayer.spotify.SpotifyService;
import com.alisonjc.compmusicplayer.spotify.model.playlists.Item;
import com.alisonjc.compmusicplayer.spotify.model.playlists.UserPlaylists;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import roboguice.fragment.RoboFragment;


public class PlaylistFragment extends RoboFragment implements OnPlaylistInteractionListener {

    @Inject
    private SpotifyService mSpotifyService;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private PlaylistRecyclerAdapter mAdapter;
    private List<Item> mPlaylistItemList;
    private OnPlaylistInteractionListener mListener;
    private Drawable dividerDrawable;


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

        View rootView = inflater.inflate(R.layout.recyclerview_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            dividerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.recycler_view_divider);
            mPlaylistItemList = new ArrayList<>();
            mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);
            RecyclerView.ItemDecoration dividerItemDecoration = new RecyclerDivider(dividerDrawable);
            mRecyclerView.addItemDecoration(dividerItemDecoration);

            mAdapter = new PlaylistRecyclerAdapter(getContext(), mPlaylistItemList, new PlaylistRecyclerAdapter.onItemClickListener() {
                @Override
                public void onItemClick(Item item) {

                    String userId = item.getOwner().getId();
                    String playlistId = item.getId();
                    String playlistTitle = item.getName();
                    mListener.onPlaylistSelected(userId, playlistId, playlistTitle);
                }
            });
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.updateAdapter(mPlaylistItemList);

            recyclerViewSetup();
        }
    }

    private void recyclerViewSetup() {

        mSpotifyService.getUserPlayLists().enqueue(new Callback<UserPlaylists>() {
            @Override
            public void onResponse(Call<UserPlaylists> call, Response<UserPlaylists> response) {

                if (response.isSuccess() && response.body() != null) {
                    mAdapter.updateAdapter(response.body().getItems());

                } else if (response.code() == 401) {
                    //userLogout();
                }
            }

            @Override
            public void onFailure(Call<UserPlaylists> call, Throwable t) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlaylistInteractionListener) {
            mListener = (OnPlaylistInteractionListener) context;
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

