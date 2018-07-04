package com.memeit.backend.dataclasses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jv on 6/29/2018.
 */

public class Reaction {
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

    public Reaction(ReactionType type, String memeID) {
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
