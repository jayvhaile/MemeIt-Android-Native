package com.memeit.backend.dataclasses;

/**
 * Created by Jv on 4/29/2018.
 */
public class User {
    private String _id;
    private String name;
    private String img_url;
    private int followerCount;
    private int followingCount;
    private int memesCount;
    private boolean isFollowingMe;
    private boolean isFollowedByMe;

    public User(String name, String imageUrl) {
        this.name = name;
        this.img_url = imageUrl;
    }

    public String getUserID() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return img_url;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public int getMemesCount() {
        return memesCount;
    }

    public boolean isFollowingMe() {
        return isFollowingMe;
    }

    public boolean isFollowedByMe() {
        return isFollowedByMe;
    }
}
