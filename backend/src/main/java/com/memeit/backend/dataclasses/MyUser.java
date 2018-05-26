package com.memeit.backend.dataclasses;

public class MyUser {
    String name;
    String email;
    String password;
    String gid;

    public MyUser(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public MyUser(String email, String gid,Void placeholer) {
        this.email = email;
        this.gid=gid;
    }


}
