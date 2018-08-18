package com.innov8.memeit;
import android.app.Application;

import com.cloudinary.android.MediaManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.memeit.backend.MemeItClient;

import java.util.HashMap;
import java.util.Map;

public class MemeItApp extends Application{
    private static final String LOCAL_SERVER_URL="http://127.0.0.1:5000/api/";
    private static final String SERVER_URL="https://safe-beyond-33046.herokuapp.com/api/";
    @Override
    public void onCreate() {
        super.onCreate();
        MemeItClient.init(getApplicationContext(),SERVER_URL);
        Map config = new HashMap();
        config.put("cloud_name", "innov8");
        config.put("api_key", "591249199742556");
        config.put("api_secret", "yT2mxv0vQrEWjzsPrmyD6xu5a-Y");
        MediaManager.init(this, config);
        Fresco.initialize(this);
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
