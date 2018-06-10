package com.alisonjc.compmusicplayer.spotify;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alisonjc.compmusicplayer.BuildConfig;
import com.alisonjc.compmusicplayer.spotify.action_models.CreatePlaylist;
import com.alisonjc.compmusicplayer.spotify.action_models.RemoveTracks;
import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistModel.Item;
import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistModel.SpotifyUser;
import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistModel.UserPlaylists;
import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistTracksModel.PlaylistTracksList;
import com.alisonjc.compmusicplayer.spotify.spotify_model.UserTracksModel.UserTracks;
import com.alisonjc.compmusicplayer.util.Constants;
import com.spotify.sdk.android.player.Config;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

@Singleton
public class SpotifyService {

    private static final String CLIENT_ID = BuildConfig.CLIENT_ID;

    private static SpotifyService mSpotifyService;

    public static SpotifyService getSpotifyService() {
        if (mSpotifyService != null){
            return mSpotifyService;
        } else {
            mSpotifyService = new SpotifyService();
            return mSpotifyService;
        }
    }

    private String mToken = "";
    private String mUserId = "";
    public SharedPreferences myPrefs;
    private final SpotifyServiceInterface mSpotifyServiceInterface;

    @Inject
    public SpotifyService() {

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        mSpotifyServiceInterface = retrofit.create(SpotifyServiceInterface.class);
    }

    public Observable<PlaylistTracksList> getPlaylistTracks(String userId, String playlistId, int offset, int limit) {
        return mSpotifyServiceInterface.getPlaylistTracks("Bearer " + mToken, userId, playlistId, offset, limit);
    }

    public Observable<UserPlaylists> getUserPlayLists() {
        return mSpotifyServiceInterface.getUserPlayLists("Bearer " + mToken, mUserId);
    }

    public Observable<SpotifyUser> getCurrentUser(String token) {
        mToken = token;
        return mSpotifyServiceInterface.getCurrentUser("Bearer " + mToken);
    }

    public Observable<UserTracks> getUserTracks(int offset, int limit) {
        return mSpotifyServiceInterface.getUserTracks("Bearer " + mToken, offset, limit);
    }

    public Observable<Object> addTrackToPlaylist(String playlistId, String uri) {
        return mSpotifyServiceInterface.addTrackToPlaylist("Bearer " + mToken, mUserId, playlistId, uri);
    }

    public Observable<Object> removeTrackFromPlaylist(String playlistId, RemoveTracks tracks) {
        return mSpotifyServiceInterface.removeTrackFromPlaylist("Bearer " + mToken, mUserId, playlistId, tracks);
    }

    public Observable<Object> createPlaylist(CreatePlaylist playlist) {
        return mSpotifyServiceInterface.createPlaylist("Bearer " + mToken, mUserId, playlist);
    }

    public Config getPlayerConfig(Context context) {
        return new Config(context, mToken, CLIENT_ID);
    }

    public boolean isLoggedIn() {
        Log.e("isLoggedIn", mUserId + " " + mToken);
        return (mToken.equals(""));
    }

    public void userLogout(Context context) {
        this.setToken("", context);
        this.setUserId("", context);
        context.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.remove(Constants.USER_ID).apply();
        editor.remove(Constants.USER_TOKEN).apply();
    }

    public void setUserId(String userId, Context context) {
        mUserId = userId;
        myPrefs = context.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
        myPrefs.edit().putString(Constants.USER_ID, mUserId).apply();
    }

    public void setToken(String token, Context context) {
        mToken = token;
        myPrefs = context.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
        myPrefs.edit().putString(Constants.USER_TOKEN, mToken).apply();
    }

    //    public Observable<Album> getSavedAlbums() {
//        return mSpotifyServiceInterface.getSavedAlbums("Bearer " + mToken, 50, 50);
//    }

//    public Observable<UserArtists> getUserAlbums() {
//        return mSpotifyServiceInterface.getUserArtists("Bearer " + mToken);
//    }
//
//    public Observable<ArtistTracksList> getArtistTracksList(String albumId){
//        return mSpotifyService.getAlbumTracks()"Bearer" + mToken, albumId;
//    }

//    public Observable<Artist> getFollowedArtists(){
//        return mSpotifyServiceInterface.getFollowedArtists("Bearer " + mToken, 50);
//    }
}

