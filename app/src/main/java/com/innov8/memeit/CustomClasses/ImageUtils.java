package com.innov8.memeit.CustomClasses;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.cloudinary.Transformation;
import com.cloudinary.Url;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.ResponsiveUrl;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;

import java.io.File;

/**
 * Created by Jv on 6/30/2018.
 */

public class ImageUtils {
    private static final String TAG="ImageUtils";
    public static void loadImageFromCloudinaryTo(final SimpleDraweeView sdv, String id){
        if(TextUtils.isEmpty(id)){
            sdv.setImageURI((Uri) null);
            return;
        }
        Transformation t=new Transformation().quality("10");
        Url a=MediaManager.get().url().transformation(t).source(id);
        MediaManager.get().responsiveUrl(ResponsiveUrl.Preset.FIT)
                .stepSize(10)
                .generate(a, sdv, new ResponsiveUrl.Callback() {
                    @Override
                    public void onUrlReady(Url url) {
                        String u=url.generate();
                        sdv.setImageURI(u);
                    }
                });
    }
    public static void loadImageFromFileTo(final SimpleDraweeView sdv, String url){
        if(TextUtils.isEmpty(url)){
            sdv.setImageURI((Uri) null);
            return;
        }
        sdv.setImageRequest(ImageRequest.fromFile(new File(url)));
    }
    public static void loadImageFromUriTo(final SimpleDraweeView sdv,Uri uri){
        if(uri==null){
            sdv.setImageURI((Uri) null);
            return;
        }
        sdv.setImageURI(uri);
    }


}
