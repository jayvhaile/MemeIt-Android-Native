package com.memeit.backend.dataclasses;

public class AuthInfo {
    private String email;
    private String password;
    private String gid;

    public AuthInfo(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public AuthInfo(String email, String gid, Void placeholer) {
        this.email = email;
        this.gid=gid;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getGid() {
        return gid;
    }
}
