package com.alisonjc.compmusicplayer.spotify.action_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by acaldwell on 2/19/18.
 */

public class CreatePlaylist {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("public")
    @Expose
    private Boolean isPublic;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }
}
