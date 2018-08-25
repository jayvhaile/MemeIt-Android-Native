package com.innov8.memeit.CustomClasses;

public class Suggestion {
    String tag;
    int followers;

    public Suggestion(String tag, int followers) {
        this.tag = tag;
        this.followers = followers;
    }

    public Suggestion() {
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }
}
