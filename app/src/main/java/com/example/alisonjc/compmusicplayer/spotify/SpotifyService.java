package com.example.alisonjc.compmusicplayer.spotify;

import com.example.alisonjc.compmusicplayer.spotify.tracklist.PlaylistTracksList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by alisonjc on 2/7/16.
 */
public interface SpotifyService {

    @GET("v1/me")
    Call<SpotifyUser> getCurrentUser(@Header("Authorization") String bearerToken);

    @GET("v1/me/playlists")
    Call<UserPlaylists> getCurrentUserPlaylists(@Header("Authorization") String bearerToken);

   // @GET("v1/users/{user_id}/playlists")
    //Call<UserPlaylists> getUserPlayLists(@Header("Authorization") String bearerToken, @Path("user_id") String userId);

    @GET("/v1/users/{user_id}/playlists/{playlist_id}/tracks")
    Call<PlaylistTracksList> getPlaylistTracks(@Header("Authorization") String bearerToken, @Path("user_id") String userId, @Path("playlist_id") String playlistId);
}

