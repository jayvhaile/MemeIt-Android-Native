package com.memeit.backend.dataclasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jv on 4/29/2018.
 */
public class User implements Parcelable {
    private String uid;
    private String name;
    private String username;
    private String pic;
    private String cpic;
    private int followerCount;
    private int followingCount;
    private int postCount;
    private boolean isFollowingMe;
    private boolean isFollowedByMe;

    protected User(Parcel in) {
        uid = in.readString();
        name = in.readString();
        username = in.readString();
        pic = in.readString();
        cpic = in.readString();
        followerCount = in.readInt();
        followingCount = in.readInt();
        postCount = in.readInt();
        isFollowingMe = in.readByte() != 0;
        isFollowedByMe = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public static User username(String username){
        User u=new User();
        u.username=username;
        return u;
    }
    public static User name(String name){
        User u=new User();
        u.name=name;
        return u;
    }
    public static User pic(String pic){
        User u=new User();
        u.pic=pic;
        return u;
    }
    public static User cpic(String cpic){
        User u=new User();
        u.cpic=cpic;
        return u;
    }

    public User() {
    }

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

    public String getUsername() {
        return username;
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

    public MyUser toMyUser(){
        return new MyUser(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(pic);
        dest.writeString(cpic);
        dest.writeInt(followerCount);
        dest.writeInt(followingCount);
        dest.writeInt(postCount);
        dest.writeByte((byte) (isFollowingMe ? 1 : 0));
        dest.writeByte((byte) (isFollowedByMe ? 1 : 0));
    }
}
