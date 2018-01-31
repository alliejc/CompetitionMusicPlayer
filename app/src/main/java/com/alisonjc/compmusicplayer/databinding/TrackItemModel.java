package com.alisonjc.compmusicplayer.databinding;

import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistTracksModel.Item;


public class TrackItemModel implements TrackItemInterface {

    private String mArtist;
    private String mSongName;
    private String mUri;
    private String mImage;

    public TrackItemModel(Item playlistTrackItem) {
        this.mArtist = playlistTrackItem.getTrack().getArtists().get(0).getName();
        this.mSongName = playlistTrackItem.getTrack().getName();
        this.mUri = playlistTrackItem.getTrack().getUri();
        this.mImage = playlistTrackItem.getTrack().getAlbum().getImages().get(0).getUrl();
    }

    public TrackItemModel(com.alisonjc.compmusicplayer.spotify.spotify_model.UserTracksModel.Item userTrackItem) {
        this.mArtist = userTrackItem.getTrack().getArtists().get(0).getName();
        this.mSongName = userTrackItem.getTrack().getName();
        this.mUri = userTrackItem.getTrack().getUri();
        this.mImage = userTrackItem.getTrack().getAlbum().getImages().get(0).getUrl();
    }

    @Override
    public String getArtist() {
        return mArtist;
    }

    @Override
    public String getSongName() {
        return mSongName;
    }

    @Override
    public String getUri(){
        return mUri;
    }

    @Override
    public String getImage() { return mImage; }
}
