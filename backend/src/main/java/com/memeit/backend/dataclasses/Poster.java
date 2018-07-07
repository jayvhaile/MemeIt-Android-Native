package com.memeit.backend.dataclasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jv on 6/16/2018.
 */

public class Poster implements Parcelable {
    @SerializedName("pid")
    private String ID;
    @SerializedName("name")
    private String name;
    @SerializedName("pic")
    private String profileUrl;

    protected Poster(Parcel in) {
        ID = in.readString();
        name = in.readString();
        profileUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(name);
        dest.writeString(profileUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Poster> CREATOR = new Creator<Poster>() {
        @Override
        public Poster createFromParcel(Parcel in) {
            return new Poster(in);
        }

        @Override
        public Poster[] newArray(int size) {
            return new Poster[size];
        }
    };

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
