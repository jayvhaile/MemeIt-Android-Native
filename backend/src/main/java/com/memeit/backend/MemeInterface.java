package com.memeit.backend;

import com.memeit.backend.dataclasses.AuthInfo;
import com.memeit.backend.dataclasses.AuthToken;
import com.memeit.backend.dataclasses.Badge;
import com.memeit.backend.dataclasses.Comment;
import com.memeit.backend.dataclasses.Meme;
import com.memeit.backend.dataclasses.Reaction;
import com.memeit.backend.dataclasses.Tag;
import com.memeit.backend.dataclasses.User;
import com.memeit.backend.dataclasses.Username;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Jv on 6/17/2018.
 */
public interface MemeInterface {
    //=============================Auth Related=================================================

    //------POST------
    @POST("auth/signin")
    public Call<AuthToken> loginWithEmail(@Body AuthInfo user);

    @POST("auth/signin/google")
    public Call<AuthToken> loginWithGoogle(@Body AuthInfo user);

    @POST("auth/signup")
    public Call<AuthToken> signUpWithEmail(@Body AuthInfo user);


    @POST("auth/signup/google")
    public Call<AuthToken> signUpWithGoogle(@Body AuthInfo user);

    @DELETE("user/me")
    public Call<AuthToken> deleteMe(@Body AuthInfo user);

    @GET("auth/username")
    public Call<Username> isUsernameAvailable(@Query("username") String username);

    @PUT("auth/username")
    public Call<ResponseBody> updateUsername(@Body User username);

    @PUT("auth/password")
    public Call<ResponseBody> updatePassword(@Body AuthInfo password);

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
    public Call<List<User>> getFollowersListForUser(@Path("id") String uid,
                                                    @Query("skip") int skip,
                                                    @Query("limit") int limit
    );

    @GET("user/{id}/followings")
    public Call<List<User>> getFollowingListForUser(@Path("id") String uid,
                                                    @Query("skip") int skip,
                                                    @Query("limit") int limit);


    @GET("user/me/tags")
    public Call<List<Tag>> getMyTags(
            @Query("skip") int skip,
            @Query("limit") int limit);

    @GET("user/me/notifications")
    public Call<List<Map<String, Object>>> getMyNotifications(@Query("skip") int skip,
                                                              @Query("limit") int limit);

    @GET("user/me/notifications/count")
    public Call<Integer> getNotifCount();

    @GET("user/{id}/badges")
    public Call<List<Badge>> getBadgesFor(@Path("uid") String uid);

    @GET("user/me/badges")
    public Call<List<Badge>> getMyBadges();

    @GET("user/following/suggestion")
    public Call<List<User>> getUserSuggestions();

    //------POST-----

    @POST("user/{id}/follow")
    public Call<ResponseBody> followUser(@Path("id") String uid);

    @POST("user/{id}/unfollow")
    public Call<ResponseBody> unfollowUser(@Path("id") String uid);


    //------PUT------
    @PUT("user/me/setup")
    public Call<ResponseBody> uploadUserData(@Body User user);

    @PUT("user/me/name")
    public Call<ResponseBody> updateName(@Body User name);

    @PUT("user/me/profile_pic")
    public Call<ResponseBody> updateProfilePic(@Body User pic);

    @PUT("user/me/cover_pic")
    public Call<ResponseBody> updateCoverPic(@Body User cpic);

    @POST("user/me/follow_tags")
    public Call<ResponseBody> setFollowingTags(@Body String[] tags);

    @PUT("user/me/follow_tags")
    public Call<ResponseBody> followTags(@Body String[] tags);

    @DELETE("user/me/follow_tags")
    public Call<ResponseBody> unfollowTag(@Query("tag") String tag);


    @POST("user/me/notifications/markseenall")
    public Call<ResponseBody> markNotificationSeen();

    @POST("user/me/notifications/{id}/markseen")
    public Call<ResponseBody> markSingleNotificationSeen(@Path("id") String nid);

    //-----Delete-----

    @DELETE("user/me")
    public Call<ResponseBody> deleteMe();

    //================================Memes Related=============================================

    @GET("meme/{id}/refresh")
    public Call<Meme> getRefreshedMeme(@Path("id") String id);

    @GET("meme/home")
    public Call<List<Meme>> getHomeMemes(@Query("skip") int skip,
                                         @Query("limit") int limit);

    @GET("meme/home/guest")
    public Call<List<Meme>> getHomeMemesForGuest(@Query("skip") int skip,
                                                 @Query("limit") int limit);

    @GET("meme/trending")
    public Call<List<Meme>> getTrendingMemes(@Query("skip") int skip,
                                             @Query("limit") int limit);


    @GET("meme/me/favourite")
    public Call<List<Meme>> getFavouriteMemes(@Query("skip") int skip,
                                              @Query("limit") int limit);

    @GET("meme/{uid}/favourite")
    public Call<List<Meme>> getFavouriteMemesFor(@Path("uid") String uid,
                                                 @Query("skip") int skip,
                                                 @Query("limit") int limit);


    @GET("meme/me/posts")
    public Call<List<Meme>> getMyMemes(@Query("skip") int skip,
                                       @Query("limit") int limit);

    @GET("meme/{uid}/posts")
    public Call<List<Meme>> getMemesFor(@Path("uid") String uid,
                                        @Query("skip") int skip,
                                        @Query("limit") int limit);

    @GET("meme/search")
    public Call<List<Meme>> getFilteredMemes(@Query("text") String text,
                                             @Query("tags") String[] tags,
                                             @Query("skip") int skip,
                                             @Query("limit") int limit);

    @GET("meme/comment")
    public Call<List<Comment>> getCommentForMeme(@Query("mid") String mid,
                                                 @Query("skip") int skip,
                                                 @Query("limit") int limit);


    @POST("meme")
    public Call<Meme> postMeme(@Body Meme meme);

    @PUT("meme")
    public Call<Meme> updateMeme(@Body Meme meme);


    @DELETE("meme/{mid}")
    public Call<ResponseBody> deleteMeme(@Path("mid") String mid);


    @POST("meme/{mid}/favourite")
    public Call<ResponseBody> addMemeToFavourite(@Path("mid") String mid);

    @DELETE("meme/{mid}/favourite")
    public Call<ResponseBody> removeMemeFromFavourite(@Path("mid") String mid);


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



    @GET("meme/tags/popular")
    public Call<List<Tag>> getPopularTags(@Query("search") String text,
                                          @Query("skip") int skip,
                                          @Query("limit") int limit);

    @GET("meme/tags/trending")
    Call<List<Tag>> getTrendingTags(@Query("skip") int skip,
                                    @Query("limit") int limit);
}
