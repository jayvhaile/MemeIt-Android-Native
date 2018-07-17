package com.innov8.memeit.CustomClasses;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloudinary.Transformation;
import com.cloudinary.Url;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.ResponsiveUrl;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
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
                .stepSize(50)
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

    public static void updateViewSize(Context context, DraweeView view, ImageInfo info){
        if(info!=null){
            int sw=CustomMethods.getScreenWidth(context);

            float ratio=((float) info.getWidth())/info.getHeight();
            view.getLayoutParams().width=sw;
            view.getLayoutParams().height= (int)(sw/ratio);
            Toast.makeText(context,view.getLayoutParams().width +" "+view.getLayoutParams().height,
                    Toast.LENGTH_SHORT).show();
            view.invalidate();
        }else {
            Toast.makeText(context,"null",
                    Toast.LENGTH_SHORT).show();

        }

    }


}
