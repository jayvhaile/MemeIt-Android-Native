package com.memeit.backend.dataclasses;

public class AuthToken {
    private String token;

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "Token: "+token;
    }
}
