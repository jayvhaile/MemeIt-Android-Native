package com.innov8.memeit.CustomClasses;

import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.cloudinary.Transformation;
import com.cloudinary.Url;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.ResponsiveUrl;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by Jv on 6/30/2018.
 */

public class ImageUtils {

    public static void loadImageTo(final SimpleDraweeView sdv, String url){
        if(TextUtils.isEmpty(url)){
            sdv.setImageURI((Uri) null);
            return;
        }
        MediaManager.get().responsiveUrl(ResponsiveUrl.Preset.FIT)
                .stepSize(10)
                .generate(Uri.parse(url).getLastPathSegment(), sdv, new ResponsiveUrl.Callback() {
                    @Override
                    public void onUrlReady(Url url) {
                        sdv.setImageURI(url.generate());
                    }
                });
    }


}
