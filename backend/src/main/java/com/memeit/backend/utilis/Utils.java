package com.memeit.backend.utilis;

import android.graphics.Bitmap;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;

import java.nio.ByteBuffer;

public class Utils {
    public static <T> void checkAndFireSuccess(OnCompleteListener<T> listener, T t) {
        if (listener != null) {
            listener.onSuccess(t);
        }
    }

    public static void checkAndFireError(OnCompleteListener listener, OnCompleteListener.Error error) {
        if (listener != null) {
            listener.onFailure(error);
        }
    }
    public void uploadImage(Bitmap bitmap, UploadCallback callback){
        ByteBuffer bb=ByteBuffer.allocate(1);
        bitmap.copyPixelsToBuffer(bb);
        MediaManager.get().upload(bb.array()).callback(callback);
    }
}
