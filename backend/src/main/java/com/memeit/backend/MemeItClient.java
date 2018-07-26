package com.memeit.backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Credentials;
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
public class MemeItClient {

    private MemeInterface memeInterface;
    private static MemeItClient memeItClient;
    private static final String TAG = "memeitclient";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_PRAGMA = "Pragma";
    private static final long CACHE_SIZE = 10;
    private static final long MAX_CACHE_DAYS = 30;
    private Cache cache;
    private Context mContext;
    BroadcastReceiver connectionReceiver;
    ConnectivityManager cm;
    boolean isConnected;


    private MemeInterface memeInterfaceC;
    private MemeInterface memeInterfaceN;

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
        mContext = context;
        initConnectionListener();
        MemeItAuth.init(context);
        initCache();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                //.addInterceptor(provideOfflineCacheInterceptor())
                //.addNetworkInterceptor(provideCacheInterceptor())
                .addInterceptor(provideAuthInterceptor())
                .cache(cache);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        memeInterface = retrofit.create(MemeInterface.class);
        memeInterfaceC = getCacheClient(BASE_URL).create(MemeInterface.class);
        memeInterfaceN = getNetworkClient(BASE_URL).create(MemeInterface.class);

        MemeItUsers.init();
        MemeItMemes.init();
        PrefUtils.init(context);

    }

    private Retrofit getCacheClient(String url) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(provideCacheInterceptor())
                .addInterceptor(provideAuthInterceptor())
                .cache(cache);
        return new Retrofit.Builder()
                .baseUrl(url)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private Retrofit getNetworkClient(String url) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addNetworkInterceptor(provideNetworkInterceptor())
                .addInterceptor(provideAuthInterceptor())
                .cache(cache);
        return new Retrofit.Builder()
                .baseUrl(url)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    private void initConnectionListener() {
        cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NetworkInfo ni = cm.getActiveNetworkInfo();
                isConnected = ni != null && ni.isConnectedOrConnecting();
                Log.d(TAG, "onReceive: " + isConnected);
            }
        };
        mContext.registerReceiver(connectionReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    private void initCache() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                cache = new Cache(new File(mContext.getDataDir(), "memeit-http‐cache"),
                        10 * 1024 * 1024); // 10 MB
            } else {
                cache = new Cache(new File(mContext.getCacheDir(), "memeit-http‐cache"),
                        10 * 1024 * 1024); // 10 MB

            }
        } catch (Exception e) {
            Log.e(TAG, "Could not create Cache!");
        }
        ;
    }

    private Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();
                builder.cacheControl(CacheControl.FORCE_CACHE);
                return chain.proceed(builder.build());
            }
        };
    }

    private Interceptor provideNetworkInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();
                builder.cacheControl(CacheControl.FORCE_NETWORK);

                Response response = chain.proceed(builder.build());
                if(response.code()==304){
                    Log.d(TAG, "intercept: "+304);
                    Log.d(TAG, "intercept: "+response.isRedirect());
                }
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(30, TimeUnit.SECONDS)
                        .build();
                return response.newBuilder()
                        .removeHeader(HEADER_PRAGMA)
                        .removeHeader(HEADER_CACHE_CONTROL)
                        .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                        .build();
            }
        };
    }

    private Interceptor provideAuthInterceptor() {
        return new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                String token = MemeItAuth.getInstance().getToken();
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

    public MemeInterface getMemeInterfaceC() {
        return memeInterfaceC;
    }

    public MemeInterface getMemeInterfaceN() {
        return memeInterfaceN;
    }
}
