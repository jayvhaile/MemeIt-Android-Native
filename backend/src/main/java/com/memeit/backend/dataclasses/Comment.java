package com.memeit.client.dataclasses;

/**
 * Created by Jv on 5/13/2018.
 */
public class Comment {
    private String posterID;
    private String memeID;
    private String comment;

    public Comment(String posterID, String memeID, String comment) {
        this.posterID = posterID;
        this.memeID = memeID;
        this.comment = comment;
    }

    public String getPosterID() {
        return posterID;
    }

    public String getMemeID() {
        return memeID;
    }

    public String getComment() {
        return comment;
    }
}
