package com.memeit.backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.memeit.backend.utilis.PrefUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jv on 4/29/2018.
 */
public class MemeItClient{
    private static final String BASE_URL = "http://127.0.0.1:5000";

    private MemeInterface memeInterface;
    private static MemeItClient memeItClient;
    private static final String TAG = "memeitclient";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_PRAGMA = "Pragma";
    private static final long CACHE_SIZE=10;
    private static final long MAX_CACHE_DAYS=30;
    private Cache cache;
    BroadcastReceiver connectionReceiver;
    ConnectivityManager cm ;
    boolean isConnected;
    public static MemeItClient getInstance() {
        if (memeItClient == null)
            throw new RuntimeException("Should Initialize Client First!");
        return memeItClient;
    }
    public static void init(Context context, String baseURL) {
        if (memeItClient != null)
            throw new RuntimeException("Client Already Initialized!");
        memeItClient = new MemeItClient(context, baseURL);
    }


    private MemeItClient(final Context context, String BASE_URL) {
        initConnectionListener(context);
        MemeItAuth.init();
        initCache(context);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(provideOfflineCacheInterceptor())
                .addNetworkInterceptor(provideCacheInterceptor())
                .addInterceptor(provideAuthInterceptor(context))
                .cache(cache);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        memeInterface = retrofit.create(MemeInterface.class);

        MemeItUsers.init();
        MemeItMemes.init();
        PrefUtils.init(context);

    }
    private void initConnectionListener(Context context){
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectionReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NetworkInfo ni = cm.getActiveNetworkInfo();
                isConnected=ni!=null&&ni.isConnectedOrConnecting();
                Log.d(TAG, "onReceive: "+isConnected);
            }
        };
        context.registerReceiver(connectionReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }
    private void initCache(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                cache = new Cache(new File(context.getDataDir(), "memeit-http‐cache"),
                        10 * 1024 * 1024); // 10 MB
            }else{
                cache = new Cache(new File(context.getCacheDir(), "memeit-http‐cache"),
                        10 * 1024 * 1024); // 10 MB
            }
        } catch (Exception e) {
            Log.e(TAG, "Could not create Cache!");
        }
    }
    public void clearCache(){
        try {
            cache.evictAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                CacheControl cacheControl;
                if (isConnected) {
                    //todo check etag and update accordingly
                    cacheControl = new CacheControl.Builder()
                            .maxAge(0, TimeUnit.SECONDS)
                            .build();
                } else {
                    //todo provide from cache
                    cacheControl = new CacheControl.Builder()
                            .maxStale(30, TimeUnit.DAYS)
                            .build();
                }
                return response.newBuilder()
                        .removeHeader(HEADER_PRAGMA)
                        .removeHeader(HEADER_CACHE_CONTROL)
                        .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                        .build();
            }
        };
    }
    private Interceptor provideOfflineCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!isConnected) {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(30, TimeUnit.DAYS)
                            .build();
                    request = request.newBuilder()
                            .removeHeader(HEADER_PRAGMA)
                            .removeHeader(HEADER_CACHE_CONTROL)
                            .cacheControl(cacheControl)
                            .build();
                }
                return chain.proceed(request);
            }
        };
    }
    private Interceptor provideAuthInterceptor(final Context context){
       return new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                String token = MemeItAuth.getInstance().getToken(context);
                if (TextUtils.isEmpty(token))
                    return chain.proceed(chain.request());
                Request request = chain.request();
                Request.Builder req = request.newBuilder().header("authorization", token);
                return chain.proceed(req.build());
            }
        };
    }


    public MemeInterface getInterface() {
        return memeInterface;
    }
}
