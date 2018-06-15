package com.memeit.backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.memeit.backend.dataclasses.AuthInfo;
import com.memeit.backend.dataclasses.AuthToken;
import com.memeit.backend.dataclasses.Badge;
import com.memeit.backend.dataclasses.Comment;
import com.memeit.backend.dataclasses.Meme;
import com.memeit.backend.dataclasses.Notification;
import com.memeit.backend.dataclasses.User;
import com.memeit.backend.utilis.PrefUtils;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

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


    private static final long CACHE_SIZE=10;//todo jv get cache size from settings
    private static final long MAX_CACHE_DAYS=30;


    private Context mContext;
    private MemeItClient(final Context context, String BASE_URL) {
        mContext=context;
        initConnectionListener();
        MemeItAuth.init(context);


        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(provideOfflineCacheInterceptor())
                .addNetworkInterceptor(provideCacheInterceptor())
                .addInterceptor(provideAuthInterceptor())
                .cache(provideCache());


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
    BroadcastReceiver connectionReceiver;
    ConnectivityManager cm ;
    boolean isConnected;
    private void initConnectionListener(){
        cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectionReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NetworkInfo ni = cm.getActiveNetworkInfo();
                isConnected=ni!=null&&ni.isConnectedOrConnecting();
                Log.d(TAG, "onReceive: "+isConnected);
            }
        };
        mContext.registerReceiver(connectionReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }
    private Cache provideCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File(mContext.getCacheDir(), "http‐cache"),
                    10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            Log.e(TAG, "Could not create Cache!");
        }
        return cache;
    }
    private Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                CacheControl cacheControl;
                if (isConnected) {
                    cacheControl = new CacheControl.Builder()
                            .maxAge(0, TimeUnit.SECONDS)
                            .build();
                } else {
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
    private Interceptor provideAuthInterceptor(){
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

    MemeInterface getInterface() {
        return memeInterface;
    }


    static interface MemeInterface {
        //=============================Auth Related=================================================

        //------POST------
        @POST("signin")
        public Call<AuthToken> loginWithEmail(@Body AuthInfo user);

        @POST("signin/google")
        public Call<AuthToken> loginWithGoogle(@Body AuthInfo user);

        @POST("signup")
        public Call<AuthToken> signUpWithEmail(@Body AuthInfo user);

        @POST("signup/google")
        public Call<AuthToken> signUpWithGoogle(@Body AuthInfo user);

        @DELETE("user/me")
        public Call<AuthToken> deleteMe(@Body AuthInfo user);


        //=========================User Related=====================================================
        //------GET------
        @GET("user/me")
        public Call<User> getMyUser();

        @GET("user/{id}/")
        public Call<User> getUserById(@Path("id") String uid);



        @GET("user/me/finished")
        public Call<Boolean> isMyUserDataSaved();

        @GET("user/me/followers")
        public Call<List<User>> getMyFollowersList(@Query("skip") int skip, @Query("limit") int limit);

        @GET("user/me/followings")
        public Call<List<User>> getMyFollowingList(@Query("skip") int skip, @Query("limit") int limit);

        @GET("user/{id}/followers")
        public Call<List<User>> getFollowersListForUser(@Query("skip") int skip,
                                                        @Query("limit") int limit,
                                                        @Path("id") String uid);

        @GET("user/{id}/following")
        public Call<List<User>> getFollowingListForUser(@Query("skip") int skip,
                                                        @Query("limit") int limit,
                                                        @Path("id") String uid);

        @GET("user/{id}/posts")
        public Call<List<Meme>> getPostsOfUser(@Path("uid") String userID,@Query("search") @Nullable String search);

        @GET("user/me/notifications")
        public Call<List<Notification>> getMyNotifications(@Query("skip") int skip,
                                                           @Query("limit") int limit);

        @GET("user/me/notifications/count")
        public Call<Integer> getNotifCount();

        @GET("user/{id}/badges")
        public Call<List<Badge>> getBadgesFor(@Path("uid") String uid);

        @GET("user/me/badges")
        public Call<List<Badge>> getMyBadges();

        //------POST-----

        @POST("user/{id}/follow")
        public Call<ResponseBody> followUser(@Path("id") String uid);

        @POST("user/{id}/unfollow")
        public Call<ResponseBody> unfollowUser(@Path("id") String uid);


        //------PUT------
        @PUT("user/me")
        public Call<User> uploadUserData(@Body User user);

        @PUT("user/me/notifications/markseenall")
        public Call<ResponseBody> markNotificationSeen();

        @PUT("user/me/notifications/markseen")
        public Call<ResponseBody> markSingleNotificationSeen(@Body String nid);

        //-----Delete-----

        @DELETE("user/me")
        public Call<ResponseBody> deleteMe();

        //================================Memes Related=============================================

        @GET("meme/home")
        public Call<List<Meme>> getHomeMemes(@Query("skip") int skip,
                                             @Query("limit") int limit);

        @GET("meme/home/guest")
        public Call<List<Meme>> getHomeMemesForGuest(@Query("skip") int skip,
                                                     @Query("limit") int limit);

        @GET("meme/trending")
        public Call<List<Meme>> getTrendingMemes(@Query("skip") int skip,
                                                 @Query("limit") int limit);

        @GET("meme/favourite")
        public Call<List<Meme>> getFavouriteMemes(@Query("skip") int skip,
                                                  @Query("limit") int limit);

        @GET("meme/favourite")
        public Call<List<Meme>> getFavouriteMemesFor(@Query("uid") String uid,
                                                     @Query("skip") int skip,
                                                     @Query("limit") int limit);

        @GET("meme/posts")
        public Call<List<Meme>> getMyMemes(@Query("skip") int skip,
                                           @Query("limit") int limit);

        @GET("meme/posts")
        public Call<List<Meme>> getMemesFor(@Query("uid") String uid,
                                            @Query("skip") int skip,
                                            @Query("limit") int limit);

        @GET("meme/search")
        public Call<List<Meme>> getFilteredMemes(@Query("query") String query,
                                                 @Query("skip") int skip,
                                                 @Query("limit") int limit);

        @GET("meme/comments")
        public Call<List<Comment>> getCommentForMeme(@Query("mid") String mid,
                                                     @Query("skip") int skip,
                                                     @Query("limit") int limit);


        @POST("meme")
        public Call<Meme> postMeme(@Body Meme meme);

        @PUT("meme")
        public Call<Meme> updateMeme(@Body String mid, @Body Meme meme);

        @DELETE("meme")
        public Call<ResponseBody> deleteMeme(@Body String mid);


        @POST("meme/favourite")
        public Call<ResponseBody> addMemeToFavourite(@Body String mid);

        @DELETE("meme/favourite")
        public Call<ResponseBody> removeMemeFromFavourite(@Body String mid);


        @POST("meme/comment")
        public Call<Comment> postComment(@Body Comment comment, @Body String mid);

        @DELETE("meme/comment")
        public Call<ResponseBody> deleteComment(@Body String cid, @Body String mid);

        @PUT("meme/comment")
        public Call<ResponseBody> updateComment(@Body Comment comment, @Body String mid, @Body String cid);

        @POST("meme/like")
        public Call<ResponseBody> likeMeme(@Body String mid);

        @DELETE("meme/like")
        public Call<ResponseBody> unlikeMeme(@Body String mid);
    }


}
