package com.memeit.client.dataclasses;

import java.util.List;

/**
 * Created by Jv on 4/29/2018.
 */
public class Meme {
    private int layoutType;
    private List<MemeText> texts;
    private List<String> imageUrl;

    private String memeID;
    private int likeCount;
    private int commentCount;
    private String poster_id;

    private boolean isLikedByMe;
    private boolean isEditable;

    private boolean isEdited;
    private String originalID;

    public Meme(int layoutType, List<MemeText> texts, List<String> imageUrl, String poster_id, boolean isEdited, String originalID) {
        this.layoutType = layoutType;
        this.texts = texts;
        this.imageUrl = imageUrl;
        this.poster_id = poster_id;
        this.isEdited = isEdited;
        this.originalID = originalID;
    }

    public int getLayoutType() {
        return layoutType;
    }

    public List<MemeText> getTexts() {
        return texts;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public String getMemeID() {
        return memeID;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public String getPosterID() {
        return poster_id;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public boolean isLikedByMe() {
        return isLikedByMe;
    }

    public String getPoster_id() {
        return poster_id;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public String getOriginalID() {
        return originalID;
    }

    public String getOriginalPosterID() {
        if (isEdited)
            return originalID;
        else return null;
    }
}
