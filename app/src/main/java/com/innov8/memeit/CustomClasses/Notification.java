package com.innov8.memeit.CustomClasses;

import com.memeit.backend.dataclasses.Reaction;

public class Notification {

    public enum Type{
        COMMENT,REACT,FOLLOW,GENERAL,BADGE
    }
    Type type;
    long time;
    String commenter;
    String reacter;
    String follower;
    String badgeType;
    String generalTitle;
    String generalDetail;
    Reaction.ReactionType reactionType;

    String memeId;
    String personId;

    public Notification(Type type) {
        this.type = type;
    }

    public Notification setType(Type type) {
        this.type = type;
        return this;
    }

    public Notification setTime(long time) {
        this.time = time;
        return this;
    }

    public Notification setCommenter(String commenter) {
        this.commenter = commenter;
        return this;
    }

    public Notification setReacter(String reacter) {
        this.reacter = reacter;
        return this;
    }

    public Notification setFollower(String follower) {
        this.follower = follower;
        return this;
    }

    public Notification setBadgeType(String badgeType) {
        this.badgeType = badgeType;
        return this;
    }

    public Notification setMemeId(String memeId) {
        this.memeId = memeId;
        return this;
    }

    public Notification setPersonId(String personId) {
        this.personId = personId;
        return this;
    }

    public Type getType() {
        return type;
    }

    public long getTime() {
        return time;
    }

    public String getCommenter() {
        return commenter;
    }

    public String getReacter() {
        return reacter;
    }

    public String getFollower() {
        return follower;
    }

    public String getBadgeType() {
        return badgeType;
    }

    public String getMemeId() {
        return memeId;
    }

    public String getPersonId() {
        return personId;
    }

    public Reaction.ReactionType getReactionType() {
        return reactionType;
    }

    public void setReactionType(Reaction.ReactionType reactionType) {
        this.reactionType = reactionType;
    }

    public String getGeneralTitle() {
        return generalTitle;
    }

    public void setGeneralTitle(String generalTitle) {
        this.generalTitle = generalTitle;
    }

    public String getGeneralDetail() {
        return generalDetail;
    }

    public void setGeneralDetail(String generalDetail) {
        this.generalDetail = generalDetail;
    }
}
