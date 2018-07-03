package com.memeit.backend;

import com.memeit.backend.dataclasses.AuthInfo;
import com.memeit.backend.dataclasses.AuthToken;
import com.memeit.backend.dataclasses.Badge;
import com.memeit.backend.dataclasses.Comment;
import com.memeit.backend.dataclasses.Meme;
import com.memeit.backend.dataclasses.Notification;
import com.memeit.backend.dataclasses.Reaction;
import com.memeit.backend.dataclasses.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Jv on 6/17/2018.
 */
interface MemeInterface {
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
    public Call<List<Meme>> getPostsOfUser(@Path("uid") String userID, @Query("search") String search);

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
    public Call<Meme> updateMeme(@Body Meme meme);


    @DELETE("meme/{mid}")
    public Call<ResponseBody> deleteMeme(@Path("mid") String mid);


    @POST("meme/favourite")
    public Call<ResponseBody> addMemeToFavourite(@Body String mid);

    @DELETE("meme/favourite")
    public Call<ResponseBody> removeMemeFromFavourite(@Body String mid);


    @POST("meme/comment")
    public Call<Comment> postComment(@Body Comment comment);

    @DELETE("meme/comment")
    public Call<ResponseBody> deleteComment(@Body Comment comment);

    @PUT("meme/comment")
    public Call<ResponseBody> updateComment(@Body Comment comment);

    @POST("meme/react")
    public Call<ResponseBody> reactToMeme(@Body Reaction reaction);
    @DELETE("meme/react")
    public Call<ResponseBody> unreactToMeme(@Body String mid);
}