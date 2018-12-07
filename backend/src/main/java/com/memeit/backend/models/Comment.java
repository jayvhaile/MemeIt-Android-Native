package com.memeit.backend.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jv on 5/13/2018.
 */
public class Comment implements Parcelable{
    @SerializedName("pid")
    private String posterID;
    @SerializedName("mid")
    private String memeID;
    @SerializedName("cid")
    private String commentID;
    @SerializedName("comment")
    private String comment;
    @SerializedName("date")
    private Long date;
    @SerializedName("likeCount")
    private Long likeCount;
    @SerializedName("dislikeCount")
    private Long dislikeCount;

    private boolean isLikedByMe;
    private boolean isDislikedByMe;

    @SerializedName("poster")
    private Poster poster;


    protected Comment(Parcel in) {
        posterID = in.readString();
        memeID = in.readString();
        commentID = in.readString();
        comment = in.readString();
        if (in.readByte() == 0) {
            date = null;
        } else {
            date = in.readLong();
        }
        if (in.readByte() == 0) {
            likeCount = null;
        } else {
            likeCount = in.readLong();
        }
        if (in.readByte() == 0) {
            dislikeCount = null;
        } else {
            dislikeCount = in.readLong();
        }
        isLikedByMe = in.readByte() != 0;
        isDislikedByMe = in.readByte() != 0;
        poster = in.readParcelable(Poster.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterID);
        dest.writeString(memeID);
        dest.writeString(commentID);
        dest.writeString(comment);
        if (date == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(date);
        }
        if (likeCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(likeCount);
        }
        if (dislikeCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(dislikeCount);
        }
        dest.writeByte((byte) (isLikedByMe ? 1 : 0));
        dest.writeByte((byte) (isDislikedByMe ? 1 : 0));
        dest.writeParcelable(poster, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

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
    public Poster getPoster() {
        return poster;
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

    public Long getDate() {
        return date;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public Long getDislikeCount() {
        return dislikeCount;
    }

    public boolean isLikedByMe() {
        return isLikedByMe;
    }

    public boolean isDislikedByMe() {
        return isDislikedByMe;
    }

    public void setPosterID(String posterID) {
        this.posterID = posterID;
    }

    public void setMemeID(String memeID) {
        this.memeID = memeID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public void setDislikeCount(Long dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public void setLikedByMe(boolean likedByMe) {
        if(likedByMe) {
            isLikedByMe = true;
            isDislikedByMe = false;
        }
        else isLikedByMe = false;

    }

    public void setDislikedByMe(boolean dislikedByMe) {
        if(dislikedByMe) {
            isDislikedByMe = true;
            isLikedByMe = false;
        }
        else isDislikedByMe = false;
    }

    public void setPoster(Poster poster) {
        this.poster = poster;
    }
}
