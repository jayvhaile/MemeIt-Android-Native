package com.innov8.memeit.CustomClasses;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.cloudinary.Transformation;
import com.cloudinary.Url;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.ResponsiveUrl;
import com.facebook.drawee.view.SimpleDraweeView;
import com.innov8.memeit.SettingsActivity;

/**
 * Created by Jv on 6/30/2018.
 */

public class ImageUtils {
    private static final String TAG="ImageUtils";
    public static void loadImageFromCloudinaryTo(final SimpleDraweeView sdv, final String id){
        if(TextUtils.isEmpty(id)){
            sdv.setImageURI((Uri) null);
            return;
        }
        int quality=SettingsActivity.Companion.getImageQuality(sdv.getContext());
        final Transformation t=new Transformation().quality(String.valueOf(quality));
        Url a=MediaManager.get().url().transformation(t).source(id);
        MediaManager.get().responsiveUrl(ResponsiveUrl.Preset.FIT)
                .stepSize(100)
                .generate(a, sdv, new ResponsiveUrl.Callback() {
                    @Override
                    public void onUrlReady(Url url) {

                        String u=url.generate();
                        Log.d(TAG, "onUrlReady: "+u);
                        Uri x=Uri.parse(u);
                        Log.d(TAG, "onUrlReady: "+x.getLastPathSegment());
                        sdv.setImageURI(u);
                    }
                });
    }


}
