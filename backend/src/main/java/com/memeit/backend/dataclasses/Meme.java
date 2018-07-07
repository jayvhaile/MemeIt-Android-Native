package com.memeit.backend.dataclasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jv on 6/30/2018.
 */

public class Meme implements Parcelable {
    @SerializedName("mid")
    private String memeId;
    @SerializedName("poster")
    private Poster poster;
    @SerializedName("img_url")
    private String memeImageUrl;
    @SerializedName("tags")
    private List<String> tags;
    @SerializedName("texts")
    private List<String> texts;
    @SerializedName("date")
    private Long date;
    @SerializedName("reactionCount")
    private Long reactionCount;
    @SerializedName("commentCount")
    private Long commentCount;
    @SerializedName("point")
    private Double point;
    public static Meme createMeme(String memeImageUrl){
        return new Meme(memeImageUrl,new ArrayList<String>(),new ArrayList<String>());
    }
    public static Meme createMeme(String memeImageUrl,  List<String> texts){
        return new Meme(memeImageUrl,texts,new ArrayList<String>());
    }
    public static Meme createMeme(String memeImageUrl,  List<String> texts, List<String> tags){
        return new Meme(memeImageUrl,texts,tags);
    }
    public static Meme forID(String memeID){
        return new Meme(memeID);
    }


    public Meme(){

    }
    private Meme(String memeId){
        this.memeId=memeId;
    }
    private Meme(String memeImageUrl, List<String> texts, List<String> tags) {
        this.memeImageUrl = memeImageUrl;
        this.tags = tags;
        this.texts = texts;
    }
    private Meme(List<String> texts, List<String> tags,String mid) {
        this.memeId=mid;
        this.tags = tags;
        this.texts = texts;
    }

    private Meme(String memeId, Poster poster, String memeImageUrl, List<String> tags, List<String> texts, Long date, Long reactionCount, Long commentCount, Double point) {
        this.memeId = memeId;
        this.poster = poster;
        this.memeImageUrl = memeImageUrl;
        this.tags = tags;
        this.texts = texts;
        this.date = date;
        this.reactionCount = reactionCount;
        this.commentCount = commentCount;
        this.point = point;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(memeId);
        parcel.writeString(memeImageUrl);
        parcel.writeStringList(tags);
        parcel.writeStringList(texts);
        parcel.writeLong(date);
        parcel.writeLong(reactionCount);
        parcel.writeLong(commentCount);
        parcel.writeDouble(point);
        parcel.writeParcelable(poster,i);
    }

    protected Meme(Parcel in) {
        memeId = in.readString();
        memeImageUrl = in.readString();
        tags = in.createStringArrayList();
        texts = in.createStringArrayList();
        date=in.readLong();
        reactionCount=in.readLong();
        commentCount=in.readLong();
        point=in.readDouble();
        poster=in.readParcelable(Poster.class.getClassLoader());
    }

    public static final Creator<Meme> CREATOR = new Creator<Meme>() {
        @Override
        public Meme createFromParcel(Parcel in) {
            return new Meme(in);
        }

        @Override
        public Meme[] newArray(int size) {
            return new Meme[size];
        }
    };

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }

    public String getMemeId() {
        return memeId;
    }

    public Poster getPoster() {
        return poster;
    }

    public String getMemeImageUrl() {
        return memeImageUrl;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getTexts() {
        return texts;
    }

    public Long getDate() {
        return date;
    }

    public Long getReactionCount() {
        return reactionCount;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public Double getPoint() {
        return point;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Meme){
            Meme meme= (Meme) obj;
            return this.getMemeId().equals(meme.getMemeId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getMemeId().hashCode();
    }


    public Meme forUpdate(List<String> texts,List<String> tags){
        return new Meme(texts,tags,getMemeId());
    }


    public Reaction makeReaction(Reaction.ReactionType reactionType){
        return Reaction.create(reactionType,getMemeId());
    }
    public Comment makeComment(String comment){
        return Comment.createComment(getMemeId(),comment);
    }


}
