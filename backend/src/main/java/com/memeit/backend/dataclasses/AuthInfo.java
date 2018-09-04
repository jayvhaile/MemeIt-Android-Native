package com.memeit.backend.dataclasses;

public class AuthInfo {
    private String username;
    private String email;
    private String password;
    private String gid;

    public static AuthInfo ofPassword(String password){
        AuthInfo a=new AuthInfo();
        a.password=password;
        return a;
    }
    public AuthInfo(String username,String email, String password) {
        this.username=username;
        this.email = email;
        this.password = password;
    }
    public AuthInfo(String username, String password) {
        this.username=username;
        this.password = password;
    }
    public AuthInfo(String email, String gid,Void placeholder) {
        this.email = email;
        this.gid=gid;
    }

    public AuthInfo() {
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
