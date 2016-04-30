package com.example.alisonjc.compmusicplayer.spotify.service;

import com.example.alisonjc.compmusicplayer.spotify.service.model.playlists.SpotifyUser;
import com.example.alisonjc.compmusicplayer.spotify.service.model.playlists.UserPlaylists;
import com.example.alisonjc.compmusicplayer.spotify.service.model.tracklists.PlaylistTracksList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface SpotifyServiceInterface {

    //the logged in user information
    @GET("v1/me")
    Call<SpotifyUser> getCurrentUser(@Header("Authorization") String bearerToken);

    //logged in users playlists
    @GET("v1/me/playlists")
    Call<UserPlaylists> getCurrentUserPlaylists(@Header("Authorization") String bearerToken);

    //Not currently used
    @GET("v1/users/{user_id}/playlists")
    Call<UserPlaylists> getUserPlayLists(@Header("Authorization") String bearerToken, @Path("user_id") String userId);

    //playlist tracks for playlist for specific user
    @GET("/v1/users/{user_id}/playlists/{playlist_id}/tracks")
    Call<PlaylistTracksList> getPlaylistTracks(@Header("Authorization") String bearerToken, @Path("user_id") String userId, @Path("playlist_id") String playlistId);
}

