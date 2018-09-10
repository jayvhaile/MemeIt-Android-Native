package com.innov8.memeit.CustomClasses;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.cloudinary.Transformation;
import com.cloudinary.Url;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.ResponsiveUrl;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
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
        int quality=SettingsActivity.Companion.getImageQualityLevel(sdv.getContext());
        final Transformation t=new Transformation().quality(String.valueOf(quality))
                .flags("progressive");
        Url a=MediaManager.get().url().transformation(t).source(id);
        MediaManager.get().responsiveUrl(ResponsiveUrl.Preset.FIT)
                .stepSize(100)
                .generate(a, sdv, new ResponsiveUrl.Callback() {
                    @Override
                    public void onUrlReady(Url url) {

                        String u=url.generate()+".jpg";
                        Log.d(TAG, "onUrlReady: "+u);
                        ImageRequest lowReq= ImageRequestBuilder.fromRequest(ImageRequest.fromUri(u))
                                .setProgressiveRenderingEnabled(true)
                                .build();

                        sdv.setImageRequest(lowReq);
                    }
                });
    }

    public static void loadImage(final SimpleDraweeView sdv,final String id){
        if(TextUtils.isEmpty(id)){
            sdv.setImageURI((Uri) null);
            return;
        }
        int quality=SettingsActivity.Companion.getImageQualityLevel(sdv.getContext());
        final Transformation t=new Transformation().quality(String.valueOf(quality));
        Url a=MediaManager.get().url().transformation(t).source(id);
        MediaManager.get().responsiveUrl(ResponsiveUrl.Preset.FIT)
                .stepSize(100)
                .generate(a, sdv, new ResponsiveUrl.Callback() {
                    @Override
                    public void onUrlReady(Url url) {
                        final Transformation tlow=new Transformation()
                                .quality("5")
                                .width(42)
                                .height(42);
                        Url low=MediaManager.get().url().transformation(tlow).source(id);
                        ImageRequest lowReq= ImageRequestBuilder.fromRequest(ImageRequest.fromUri(low.generate()))
                                .setPostprocessor(new IterativeBoxBlurPostProcessor(10))
                                .build();
                        String u=url.generate();
                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setLowResImageRequest(lowReq)
                                .setImageRequest(ImageRequest.fromUri(u))
                                .setOldController(sdv.getController())
                                .build();
                        sdv.setController(controller);

                    }
                });
    }
    public static void loadImage1(final SimpleDraweeView sdv,final String id){
        if(TextUtils.isEmpty(id)){
            sdv.setImageURI((Uri) null);
            return;
        }
        final Transformation tlow=new Transformation()
                .quality("5")
                .width(100)
                .height(100)
                .effect("blur",100);
        Url low=MediaManager.get().url().transformation(tlow).source(id);
        String u=low.generate();
        Log.d(TAG, "loadImage1: "+u);
        ImageRequest lowReq= ImageRequestBuilder.fromRequest(ImageRequest.fromUri(u))
                .setPostprocessor(new IterativeBoxBlurPostProcessor(3))
                .build();
        sdv.setImageRequest(lowReq);
    }


}
