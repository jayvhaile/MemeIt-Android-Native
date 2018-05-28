package com.memeit.backend.utilis;

import android.net.Uri;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;

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
    public static void uploadImage(Uri uri, UploadCallback callback){

    }
}
