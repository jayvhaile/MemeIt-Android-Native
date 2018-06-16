package com.memeit.backend.dataclasses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jv on 6/16/2018.
 */

public class Poster {
    @SerializedName("pid")
    private String ID;
    @SerializedName("name")
    private String name;
    @SerializedName("pic")
    private String profileUrl;

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }
}
