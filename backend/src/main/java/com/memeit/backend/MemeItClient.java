package com.memeit.backend;

import android.content.Context;
import android.text.TextUtils;

import com.memeit.backend.dataclasses.AuthInfo;
import com.memeit.backend.dataclasses.AuthToken;
import com.memeit.backend.dataclasses.Badge;
import com.memeit.backend.dataclasses.Comment;
import com.memeit.backend.dataclasses.Meme;
import com.memeit.backend.dataclasses.Notification;
import com.memeit.backend.dataclasses.User;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by Jv on 4/29/2018.
 */
public class MemeItClient {
    private static final String BASE_URL="http://127.0.0.1:5000";
    private MemeInterface memeInterface;
    private static MemeItClient memeItClient;
    private static final String TAG="memeitclient";
    private MemeItClient(final Context context, String BASE_URL){

        MemeItAuth.init(context);
        MemeItUsers.init();
        OkHttpClient.Builder builder=new OkHttpClient.Builder();

        builder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                String token=MemeItAuth.getInstance().getToken();
                if (TextUtils.isEmpty(token))
                    return chain.proceed(chain.request());
                Request request=chain.request();
                Request.Builder req=request.newBuilder().header("authorization",token);
                return chain.proceed(req.build());
            }
        });
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        memeInterface =retrofit.create(MemeInterface.class);


    }
    public static MemeItClient getInstance(){
        if (memeItClient==null)
           throw new RuntimeException("Should Initialize Client First!");
        return memeItClient;
    }
    public static void init(Context context,String baseURL){
        if (memeItClient!=null)
            throw new RuntimeException("Client Already Initialized!");
        memeItClient=new MemeItClient(context,baseURL);
    }
    MemeInterface getInterface(){
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
        @POST("user/delete")
        public Call<AuthToken> signOut(@Body AuthInfo user);



        //=========================User Related=====================================================
        //------GET------
        @GET("user")
        public Call<User> getUserById(@Query("id") String userID);
        @GET("user/me")
        public Call<User> getMyUser();
        @GET("user/followers")
        public Call<List<User>> getMyFollowersList(@Query("skip")int skip,@Query("limit")int limit);
        @GET("user/following")
        public Call<List<User>> getMyFollowingList(@Query("skip")int skip,@Query("limit")int limit);
        @GET("user/:uid/followers")
        public Call<List<User>> getFollowersListForUser(@Query("skip")int skip,
                                                        @Query("limit")int limit,
                                                        @Field("uid") String userID);
        @GET("user/:uid/following")
        public Call<List<User>> getFollowingListForUser(@Query("skip")int skip,
                                                        @Query("limit")int limit,
                                                        @Query("uid") String userID);
        @GET("user/memes")
        public Call<List<Meme>> getPostsOfUser(@Query("uid") String userID);
        @GET("user/notifications")
        public Call<List<Notification>> getMyNotifications(@Query("skip")int skip,
                                                           @Query("limit")int limit);
        @GET("user/notifications/count")
        public Call<Integer> getNotifCount();
        @GET("user/:uid/badges")
        public Call<List<Badge>> getBadgesFor(@Query("uid") String uid);
        @GET("user/badges")
        public Call<List<Badge>> getMyBadges();

        //------POST-----

        @POST("user/follow")
        public Call<ResponseBody> followUser(@Body String uid);
        @POST("user/unfollow")
        public Call<ResponseBody> unfollowUser(@Body String uid);


        //------PUT------
        @PUT("user")
        public Call<User> uploadUserData(@Body User user);
        @PUT("user/notifications/markseenall")
        public Call<ResponseBody> markNotificationSeen();
        @PUT("user/notifications/markseen")
        public Call<ResponseBody> markSingleNotificationSeen(@Body String nid);

        //-----Delete-----

        @DELETE("user")
        public Call<ResponseBody> deleteMe();

        //================================Memes Related=============================================

        @GET("meme/home")
        public Call<List<Meme>> getHomeMemes(@Query("skip")int skip,
                                             @Query("limit")int limit);
        @GET("meme/home/guest")
        public Call<List<Meme>> getHomeMemesForGuest(@Query("skip")int skip,
                                                     @Query("limit")int limit);
        @GET("meme/trending")
        public Call<List<Meme>> getTrendingMemes(@Query("skip")int skip,
                                                 @Query("limit")int limit);
        @GET("meme/favourite")
        public Call<List<Meme>> getFavouriteMemes(@Query("skip")int skip,
                                                 @Query("limit")int limit);
        @GET("meme/favourite")
        public Call<List<Meme>> getFavouriteMemesFor(@Query("uid")String uid,
                                                     @Query("skip")int skip,
                                                     @Query("limit")int limit);

        @GET("meme/posts")
        public Call<List<Meme>> getMyMemes(@Query("skip")int skip,
                                           @Query("limit")int limit);

        @GET("meme/posts")
        public Call<List<Meme>> getMemesFor(@Query("uid")String uid,
                                                     @Query("skip")int skip,
                                                     @Query("limit")int limit);

        @GET("meme/search")
        public Call<List<Meme>> getFilteredMemes(@Query("query")String query,
                                            @Query("skip")int skip,
                                            @Query("limit")int limit);
        @GET("meme/comments")
        public Call<List<Comment>> getCommentForMeme(@Query("mid")String mid,
                                                     @Query("skip")int skip,
                                                     @Query("limit")int limit);





        @POST("meme")
        public Call<Meme> postMeme(@Body Meme meme);
        @PUT("meme")
        public Call<Meme> updateMeme(@Body String mid,@Body Meme meme);
        @DELETE("meme")
        public Call<ResponseBody> deleteMeme(@Body String mid);


        @POST("meme/favourite")
        public Call<ResponseBody> addMemeToFavourite(@Body String mid);
        @DELETE("meme/favourite")
        public Call<ResponseBody> removeMemeFromFavourite(@Body String mid);




        @POST("meme/comment")
        public Call<Comment> postComment(@Body Comment comment,@Body String mid);
        @DELETE("meme/comment")
        public Call<ResponseBody> deleteComment(@Body String cid,@Body String mid);
        @PUT("meme/comment")
        public Call<ResponseBody> updateComment(@Body Comment comment,@Body String mid,@Body String cid);

        @POST("meme/like")
        public Call<ResponseBody> likeMeme(@Body String mid);
        @DELETE("meme/like")
        public Call<ResponseBody> unlikeMeme(@Body String mid);
    }


}
