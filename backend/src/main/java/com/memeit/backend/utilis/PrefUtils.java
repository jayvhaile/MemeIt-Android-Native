package com.memeit.backend.utilis;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {

    private SharedPreferences preferences;

    private static PrefUtils preferenceUtils;
    private PrefUtils(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void init(Context context){
        if (preferenceUtils==null){
            preferenceUtils=new PrefUtils(context);
        }else{
            throw new RuntimeException("PreferenceUtil Already Initialized");
        }
    }

    public static SharedPreferences get(){
        if (preferenceUtils==null){
            throw new RuntimeException("Should Initialize PrefUtils First");
        }else{
            return preferenceUtils.preferences;
        }
    }

}
