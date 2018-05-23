package com.memeit.client;

import com.memeit.client.dataclasses.Comment;
import com.memeit.client.dataclasses.Meme;
import com.memeit.client.dataclasses.User;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

import java.util.List;

/**
 * Created by Jv on 4/29/2018.
 */
public class MemeItClient {
    private static final String BASE_URL="http://127.0.0.1:5000";
    private MemeInterface memeInterface;
    private static MemeItClient memeItClient;

    private MemeItClient(){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        memeInterface =retrofit.create(MemeInterface.class);
    }
    public static MemeItClient getInstance(){
        if (memeItClient==null)
            memeItClient=new MemeItClient();
        return memeItClient;
    }
    public MemeInterface getInterface(){
        return memeInterface;
    }

    public static interface MemeInterface {
        @GET("user")
        public Call<User> getUserById(@Query("id") String userID);

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

    }


}
