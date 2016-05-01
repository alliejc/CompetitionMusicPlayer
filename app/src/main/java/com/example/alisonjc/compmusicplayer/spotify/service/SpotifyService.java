package com.example.alisonjc.compmusicplayer.spotify.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SpotifyService {

    private SpotifyServiceInterface mSpotifyService;


    public SpotifyService() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mSpotifyService = retrofit.create(SpotifyServiceInterface.class);

    }

    public SpotifyServiceInterface getSpotifyService() {
        return mSpotifyService;
    }
}
