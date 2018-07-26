package com.memeit.backend.utilis;

/**
 * Created by Jv on 7/18/2018.
 */

public interface onComplete<T> {
    public void onResponceFromCache(T t);
    public void onResponceFromNetwork(T t);
    public void onFailed(String error,boolean fromCache);


}
