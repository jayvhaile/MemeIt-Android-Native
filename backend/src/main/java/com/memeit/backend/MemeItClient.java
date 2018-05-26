package com.memeit.backend;

import android.content.Context;
import android.text.TextUtils;

import com.memeit.backend.dataclasses.AuthToken;
import com.memeit.backend.dataclasses.MyUser;
import com.memeit.backend.dataclasses.User;
import com.memeit.client.dataclasses.Comment;
import com.memeit.client.dataclasses.Meme;

import java.io.IOException;
import java.util.List;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

/**
 * Created by Jv on 4/29/2018.
 */
public class MemeItClient {
    private static final String BASE_URL="http://127.0.0.1:5000";
    private MemeInterface memeInterface;
    private static MemeItClient memeItClient;

    private MemeItClient(Context context,String BASE_URL){

        MemeItAuth.init(context);
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
    public MemeInterface getInterface(){
        return memeInterface;
    }

    public static interface MemeInterface {
        @GET("user")
        public Call<User> getUserById(@Query("id") String userID);
        @GET("user/me")
        public Call<User> getMyUser();
        @GET("user/followers")
        public Call<List<User>> getFollowersListForUser(@Query("uid") String userID);
        @GET("user/following")
        public Call<List<User>> getFollowingListForUser(@Query("uid") String userID);

        @GET("user/memes")
        public Call<List<Meme>> getPostsOfUser(@Query("uid") String userID);
        @GET("trending")
        public Call<List<Meme>> getTrendingMemes();
        @GET("home")
        public Call<List<Meme>> getHomeMemesForUser(@Query("uid")String userID);
        @GET("meme/comments")
        public Call<List<Comment>> getCommentForMeme(@Query("mid")String memeID);
        @GET("meme/likers")
        public Call<List<User>> getLikersListForMeme(@Query("mid")String memeID);


        @POST("meme")
        public Call<Meme> postMeme(@Query("meme")Meme meme);
        @DELETE("meme")
        public Call<Meme> deleteMeme(@Query("mid")String memeID);
        @PUT("meme")
        public Call<Meme> updateMeme(@Query("meme")Meme meme);


        @POST("meme/comment")
        public Call<Comment> postComment(@Query("meme")Comment comment,@Query("mid")String memeID);
        @DELETE("meme/comment")
        public Call<Comment> deleteComment(@Query("cid")String commentID,@Query("mid")String memeID);
        @PUT("meme/comment")
        public Call<Comment> updateComment(@Query("meme")Comment comment,@Query("mid")String memeID);

        @POST("meme/like")
        public Call<Boolean> likeMeme(@Query("mid")String memeID);
        @DELETE("meme/like")
        public Call<Boolean> unlikeMeme(@Query("mid")String memeID);

        @POST("api/signin")
        public Call<AuthToken> loginWithEmail(@Body MyUser user);
        @POST("api/signin/google")
        public Call<AuthToken> loginWithGoogle(@Body MyUser user);


        @POST("api/signup")
        public Call<AuthToken> signUpWithEmail(@Body MyUser user);
        @POST("api/signup/google")
        public Call<AuthToken> signUpWithGoogle(@Body MyUser user);
        @PUT("api/user")
        public Call<User> uploadUserData(@Body User user);


    }


}
