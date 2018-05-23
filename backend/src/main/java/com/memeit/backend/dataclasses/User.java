package com.memeit.client.dataclasses;

/**
 * Created by Jv on 4/29/2018.
 */
public class User {
    private String uid;
    private String name;
    private String imageUrl;
    private int followerCount;
    private int followingCount;
    private int postCount;
    private boolean isFollowingMe;
    private boolean isFollowedByMe;

    public User(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getUserID() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public int getPostCount() {
        return postCount;
    }

    public boolean isFollowingMe() {
        return isFollowingMe;
    }

    public boolean isFollowedByMe() {
        return isFollowedByMe;
    }
}
