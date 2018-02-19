package com.alisonjc.compmusicplayer.spotify;

import android.app.Activity;

import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.spotify.action_models.CreatePlaylist;
import com.alisonjc.compmusicplayer.spotify.action_models.RemoveTracks;
import com.alisonjc.compmusicplayer.spotify.action_models.Track;
import com.alisonjc.compmusicplayer.util.Util;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by acaldwell on 2/19/18.
 */

public class SpotifyHelper {

    public static void createPlaylist(SpotifyService spotifyService, Activity activity, CreatePlaylist item){
        spotifyService.createPlaylist(item)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        String message = item.getName() + " was successfully created";
                        Util.showSnackBar(activity, message);
                    }
                }, throwable -> {
                    String message = "Add failed, please check your network connection";
                    Util.showSnackBar(activity, message);
                }, () -> {
                });
    }

    public static void removeTrackFromPlaylist(Activity activity, String id, String uri,
                                               String playlistTitle, String songTitle, SpotifyService spotifyService){
        Track track = new Track();
        track.setUri(uri);
        List<Track> list = new ArrayList<>();
        list.add(track);
        RemoveTracks tracks = new RemoveTracks();
        tracks.setTracks(list);

        spotifyService.removeTrackFromPlaylist(id, tracks)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response != null){
                        String message = songTitle + " was removed from " + playlistTitle;
                        Util.showSnackBar(activity, message);
                    }
                }, throwable -> {
                    String message = activity.getString(R.string.check_network);
                    Util.showSnackBar(activity, message);
                }, () -> {
                });
    }
}
