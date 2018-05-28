package com.innov8.memeit.CustomClasses;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Biruk on 5/26/2018.
 */

public class UserPrefs {
    public static final String PREFS_NAME = "userPrefs";
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;

    String name;
    String email;
    String id;

    public UserPrefs(Context c) {
        sharedPrefs = c.getSharedPreferences(PREFS_NAME,0);
        editor = sharedPrefs.edit();
    }

    public String getName() {
         return sharedPrefs.getString("name","user");
    }

    public void setName(String name) {
        editor.putString("name",name);
        editor.apply();
    }

    public String getEmail() {
        return sharedPrefs.getString("email","");
    }

    public void setEmail(String email) {
        editor.putString("email",email);
        editor.apply();
    }

    public String getId() {
        return sharedPrefs.getString("id","");
    }

    public void setId(String id) {
        editor.putString("id",id);
        editor.apply();
    }
}
