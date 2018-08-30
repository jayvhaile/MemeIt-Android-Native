package com.memeit.backend.dataclasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jv on 6/29/2018.
 */

public class Reaction implements Parcelable {
    protected Reaction(Parcel in) {
        type = in.readInt();
        memeID = in.readString();
        reactorID = in.readString();
    }

    public static final Creator<Reaction> CREATOR = new Creator<Reaction>() {
        @Override
        public Reaction createFromParcel(Parcel in) {
            return new Reaction(in);
        }

        @Override
        public Reaction[] newArray(int size) {
            return new Reaction[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(memeID);
        dest.writeString(reactorID);
    }

    public enum ReactionType{
        FUNNY(2f),VERY_FUNNY(3f),STUPID(-1f),ANGERING(-2F);

        private float score;

        ReactionType(float score) {
            this.score = score;
        }

        public float getScore() {
            return score;
        }
    }
    @SerializedName("type")
    private int type;
    @SerializedName("mid")
    private String memeID;
    @SerializedName("rid")
    private String reactorID;


    public static Reaction create(ReactionType type,String memeID){
        return new Reaction(type,memeID);
    }
    private Reaction(ReactionType type, String memeID) {
        this.type = type.ordinal();
        this.memeID = memeID;
    }
    public ReactionType getType() {
        return ReactionType.values()[type];
    }

    public String getMemeID() {
        return memeID;
    }

    public String getReactorID() {
        return reactorID;
    }
}
