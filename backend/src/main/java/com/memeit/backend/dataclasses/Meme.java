package com.memeit.backend.dataclasses;

import java.util.List;

/**
 * Created by Jv on 4/29/2018.
 */
public class Meme {
    private String img_url;
    private List<String> texts;
    private List<String> tags;
    private String pid;


    private String _id;
    private int likeCount;
    private int commentCount;
    private boolean isLikedByMe;


    public Meme(String img_url, List<String> texts, List<String> tags) {
        this.img_url = img_url;
        this.texts = texts;
        this.tags = tags;
    }
    public String getImg_url() {
        return img_url;
    }

    public List<String> getMemeTexts() {
        return texts;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getPosterUserId() {
        return pid;
    }

    public String getMemeID() {
        return _id;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public boolean isLikedByMe() {
        return isLikedByMe;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public void setMemeTexts(List<String> texts) {
        this.texts = texts;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
