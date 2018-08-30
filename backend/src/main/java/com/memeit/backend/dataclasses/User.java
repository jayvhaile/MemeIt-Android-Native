package com.memeit.backend.dataclasses;

/**
 * Created by Jv on 4/29/2018.
 */
public class User {
    private String uid;
    private String name;
    private String pic;
    private String cpic;
    private int followerCount;
    private int followingCount;
    private int postCount;
    private boolean isFollowingMe;
    private boolean isFollowedByMe;

    public User(String name, String imageUrl) {
        this.name = name;
        this.pic = imageUrl;
    }

    public String getUserID() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return pic;
    }
    public String getCoverImageUrl() {
        return cpic;
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
