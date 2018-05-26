package com.memeit.backend;

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
}
