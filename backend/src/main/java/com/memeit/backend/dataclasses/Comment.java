package com.memeit.backend.dataclasses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jv on 5/13/2018.
 */
public class Comment {
    @SerializedName("pid")
    private String posterID;
    @SerializedName("mid")
    private String memeID;
    @SerializedName("cid")
    private String commentID;
    @SerializedName("comment")
    private String comment;
    @SerializedName("date")
    private String date;

    /**
     * Creates a new Comment Object
     * Alternatively you can use meme.makeComment which automatically fills you the memeID
     * @param mid the memeID to comment on
     * @param comment the comment text
     * */
    public static Comment createComment(String mid, String comment){
        return new Comment(mid,comment);
    }

    /**
     * Only to be used by the API     *
     * */
    public static Comment createCommentForUpdate(String commentID, String comment){
        return new Comment(commentID,comment,null);
    }

    /**
     * Only to be used by the API     *
     * */
    public static Comment createCommentForDelete(String memeID,String commentID){
        return new Comment(null,commentID,memeID);
    }

    public Comment() {
    }

    private Comment(String memeID, String comment) {
        this.memeID = memeID;
        this.comment = comment;
    }
    private Comment(String commentID, String comment,Void placeHolder) {
        this.commentID=commentID;
        this.comment = comment;
    }
    private Comment(Void placeHolder,String commentID, String memeID) {
        this.commentID=commentID;
        this.memeID=memeID;
    }

    public String getPosterID() {
        return posterID;
    }

    public String getMemeID() {
        return memeID;
    }

    public String getCommentID() {
        return commentID;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }



}
