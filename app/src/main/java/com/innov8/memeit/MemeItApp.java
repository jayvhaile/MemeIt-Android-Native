package com.innov8.memeit;

import android.app.Application;

import com.cloudinary.android.MediaManager;
import com.memeit.backend.MemeItClient;

import java.util.HashMap;
import java.util.Map;

public class MemeItApp extends Application{
    private static final String SERVER_URL="http://127.0.0.1:5000/api/";
    @Override
    public void onCreate() {
        super.onCreate();
        MemeItClient.init(this,SERVER_URL);

        Map config = new HashMap();
        config.put("cloud_name", "innov8");
        MediaManager.init(this, config);
    }
}
