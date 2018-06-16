package com.memeit.backend.dataclasses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jv on 6/16/2018.
 */

public class MemeRequest {
    @SerializedName("img_url")
    private String memeImageUrl;
    @SerializedName("date")
    private Long date;
    @SerializedName("texts")
    private List<String> texts;
    @SerializedName("tags")
    private List<String> tags;

    public MemeRequest(String memeImageUrl) {
        this.memeImageUrl = memeImageUrl;
    }
    public MemeRequest(String memeImageUrl,  Long date) {
        this.memeImageUrl = memeImageUrl;
        this.date = date;
    }
    public MemeRequest(String memeImageUrl,  List<String> texts, List<String> tags) {
        this.memeImageUrl = memeImageUrl;
        this.date = date;
        this.texts = texts;
        this.tags = tags;
    }
    public MemeRequest(String memeImageUrl, Long date, List<String> texts, List<String> tags) {
        this.memeImageUrl = memeImageUrl;
        this.date = date;
        this.texts = texts;
        this.tags = tags;
    }

    public void setMemeImageUrl(String memeImageUrl) {
        this.memeImageUrl = memeImageUrl;
    }


    public void setDate(Long date) {
        this.date = date;
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getMemeImageUrl() {
        return memeImageUrl;
    }

    public Long getDate() {
        return date;
    }

    public List<String> getTexts() {
        return texts;
    }

    public List<String> getTags() {
        return tags;
    }
}
