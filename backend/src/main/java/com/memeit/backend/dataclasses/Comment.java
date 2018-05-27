package com.memeit.backend.dataclasses;

/**
 * Created by Jv on 5/13/2018.
 */
public class Comment {
    private String pid;
    private String comment;

    private String date;

    public Comment(String comment) {
        this.comment = comment;
    }

    public String getPid() {
        return pid;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

}
