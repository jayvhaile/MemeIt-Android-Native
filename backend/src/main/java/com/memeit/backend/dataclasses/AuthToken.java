package com.memeit.backend.dataclasses;

public class AuthToken {
    private String token;
    private MyUser user;

    public MyUser getMyUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "Token: "+token;
    }
}
