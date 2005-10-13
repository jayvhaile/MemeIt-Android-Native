package com.innov8.memeit.CustomClasses;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;

/*
 * Created by Biruk on 5/11/2018.
 */

public class CustomMethods {

    // Set window content to draw behind the status and navigation bars.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setFullScreen(Activity a){
        a.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    // This method is just to test the git push.
    public int test(){
        return 5;
    }
}
