
package com.alisonjc.compmusicplayer.spotify.action_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Track {

    @SerializedName("uri")
    @Expose
    private String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
