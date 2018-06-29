package com.memeit.backend;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jv on 6/29/2018.
 */

public class CacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder=chain.request().newBuilder();
        if(isConnected()){

        }else{

        }
        return null;
    }

    public boolean isConnected() {
        return true;
    }
}
