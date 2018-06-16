package com.memeit.backend.dataclasses;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Jv on 4/29/2018.
 */
public class MemeResponse {
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

    public Date getDateObject() {
        return new Date(date);
    }
    public String getFormattedDate(DateFormat format) {
        return format.format(getDateObject());
    }

    public long getReactionCount() {
        return reactionCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public double getPoint() {
        return point;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MemeResponse){
            MemeResponse response= (MemeResponse) obj;
            return response.getMemeId().equals(this.getMemeId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getMemeId().hashCode();
    }
}
