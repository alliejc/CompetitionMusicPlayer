package com.alisonjc.compmusicplayer.fragment;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.adapter.ActionPlaylistAdapter;
import com.alisonjc.compmusicplayer.adapter.PlaylistAdapter;
import com.alisonjc.compmusicplayer.adapter.TracksAdapter;
import com.alisonjc.compmusicplayer.callbacks.IOnOverflowSelected;
import com.alisonjc.compmusicplayer.databinding.TrackItemModel;
import com.alisonjc.compmusicplayer.spotify.SpotifyService;
import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistModel.Item;
import com.alisonjc.compmusicplayer.util.EndlessScrollListener;
import com.alisonjc.compmusicplayer.util.Util;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by acaldwell on 2/18/18.
 */

public class PlaylistActionDialog extends DialogFragment {

    private static final String TAG = PlaylistActionDialog.class.getSimpleName();

    private int mAction;
    private String mSongtitle;
    private String mTrackUri;
    private ActionPlaylistAdapter mAdapter;
    private List<Item> mPlaylistItemList;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SpotifyService mSpotifyService = SpotifyService.getSpotifyService();

    public PlaylistActionDialog(){}

    public static PlaylistActionDialog newInstance(String uri, int action, String songtitle) {
        PlaylistActionDialog frag = new PlaylistActionDialog();
        Bundle args = new Bundle();
        args.putString("uri", uri);
        args.putInt("action", action);
        args.putString("songTitle", songtitle);

        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAction = getArguments().getInt("action");
        mTrackUri = getArguments().getString("uri");
        mSongtitle = getArguments().getString("songTitle");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setDialogStyle();

        View rootView = inflater.inflate(R.layout.list, container, false);
        TextView title = (TextView) rootView.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        return rootView;
    }

    private void setDialogStyle(){
        setStyle(android.app.DialogFragment.STYLE_NORMAL, 0);

        if(getDialog() != null && getDialog().getWindow() != null) {
            getDialog().setCancelable(true);
            getDialog().getWindow().setTitle(getString(R.string.choose_a_playlist));
        }
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new ActionPlaylistAdapter(getActivity(), mPlaylistItemList, mAction, new ActionPlaylistAdapter.OnPlaylistAction() {
            @Override
            public void onPlaylistAction(String uri, String playlistId, String playlistTitle, int action) {
                //make call to Spotify adding playlist
                addPlaylist(playlistId, uri);

                String message = mSongtitle + " was successfully added to " + playlistTitle;
                getDialog().dismiss();
                Snackbar snackbar = Snackbar.make(getActivity().getCurrentFocus(), message, Snackbar.LENGTH_LONG);
                snackbar.show();
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
                        mPlaylistItemList.addAll(userPlaylists.getItems());
                        mAdapter.updateAdapter(mPlaylistItemList);
                    } else {
                        mSpotifyService.userLogout(getActivity());
                    }
                }, throwable -> {
                }, () -> {
                });
    }

    private void addPlaylist(String id, String uri){

        mSpotifyService.addTrackToPlaylist(id, uri)
               .subscribeOn(Schedulers.newThread())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(response -> {
                   if(response != null){
                       Log.e("response", String.valueOf(response));
                   }
               }, throwable -> {
                   Log.e(String.valueOf(throwable), "throwable");
               }, () -> {
               });
    }
}
