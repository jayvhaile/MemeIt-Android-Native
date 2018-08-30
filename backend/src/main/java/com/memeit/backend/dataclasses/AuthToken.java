package com.memeit.backend.dataclasses;

public class AuthToken {
    private String token;
    private String uid;

    public String getUid() {
        return uid;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "Token: "+token;
    }
}
