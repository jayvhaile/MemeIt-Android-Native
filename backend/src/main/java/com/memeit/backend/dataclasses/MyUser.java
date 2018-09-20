package com.memeit.backend.dataclasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class MyUser {
    private static final String UID = "__myuser_uid";
    private static final String USERNAME = "__myuser_username";
    private static final String NAME = "__myuser_name";
    private static final String PIC = "__myuser_pic";
    private static final String CPIC = "__myuser_cpic";


    private String uid;
    private String username;
    private String name;
    private String pic;
    private String cpic;

    public MyUser() {
    }

    public static MyUser createFromCache(SharedPreferences in) {
        String uid = in.getString(UID, null);
        String username = in.getString(USERNAME, null);
        String name = in.getString(NAME, null);
        String pic = in.getString(PIC, null);
        String cpic = in.getString(CPIC, null);
        if (TextUtils.isEmpty(uid)) return null;
        else return new MyUser(uid, username, name, pic, cpic);
    }


    public MyUser(String uid, String username, String name, String pic, String cpic) {
        this.uid = uid;
        this.username = username;
        this.name = name;
        this.pic = pic;
        this.cpic = cpic;
    }

    public MyUser(User user) {
        uid = user.getUserID();
        username = user.getUsername();
        name = user.getName();
        pic = user.getImageUrl();
        cpic = user.getCoverImageUrl();
    }

    public MyUser(String uid) {
        this.uid = uid;
    }

    public String getUserID() {
        return uid;
    }

    public String getUsername() {
        return username;
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

    public MyUser setUsername(String username) {
        this.username = username;
        return this;
    }

    public MyUser setName(String name) {
        this.name = name;
        return this;
    }

    public MyUser setPic(String pic) {
        this.pic = pic;
        return this;
    }

    public MyUser setCpic(String cpic) {
        this.cpic = cpic;
        return this;
    }

    public void save(SharedPreferences dest) {
        dest.edit()
                .putString(UID, uid)
                .putString(USERNAME, username)
                .putString(NAME, name)
                .putString(PIC, pic)
                .putString(CPIC, cpic)
                .apply();
    }

    public void save(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        save(sp);
    }

    public static void delete(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit()
                .remove(UID)
                .remove(USERNAME)
                .remove(NAME)
                .remove(PIC)
                .remove(CPIC)
                .apply();
    }


}
