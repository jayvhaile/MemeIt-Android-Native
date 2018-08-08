package com.memeit.backend.kotlin

import com.memeit.backend.dataclasses.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface MemeInterface {

    @POST("auth/signin")
    fun loginWithEmail(@Body user: AuthInfo): Call<AuthToken>

    @POST("auth/signin/google")
    fun loginWithGoogle(@Body user: AuthInfo): Call<AuthToken>

    @POST("auth/signup")
    fun signUpWithEmail(@Body user: AuthInfo): Call<AuthToken>

    @POST("auth/signup/google")
    fun signUpWithGoogle(@Body user: AuthInfo): Call<AuthToken>

    @DELETE("user/me")
    fun deleteMe(@Body user: AuthInfo): Call<AuthToken>


    //=========================User Related=====================================================
    //------GET------
    @GET("user/me")
    fun getMyUser(): Call<User>

    @GET("user/{id}/")
    fun getUserById(@Path("id") uid: String): Call<User>


    @GET("user/me/finished")
    fun isMyUserDataSaved(): Call<Boolean>

    @GET("user/me/followers")
    fun getMyFollowersList(@Query("skip") skip: Int, @Query("limit") limit: Int): Call<List<User>>

    @GET("user/me/followings")
    fun getMyFollowingList(@Query("skip") skip: Int, @Query("limit") limit: Int): Call<List<User>>

    @GET("user/{id}/followers")
    fun getFollowersListForUser(@Query("skip") skip: Int,
                                @Query("limit") limit: Int,
                                @Path("id") uid: String): Call<List<User>>

    @GET("user/{id}/following")
    fun getFollowingListForUser(@Query("skip") skip: Int,
                                @Query("limit") limit: Int,
                                @Path("id") uid: String): Call<List<User>>

    @GET("user/{id}/posts")
    fun getPostsOfUser(@Path("uid") userID: String, @Query("search") search: String): Call<List<Meme>>

    @GET("user/me/notifications")
    fun getMyNotifications(@Query("skip") skip: Int,
                           @Query("limit") limit: Int): Call<List<Notification>>

    @GET("user/me/notifications/count")
    fun getNotifCount(): Call<Int>

    @GET("user/{id}/badges")
    fun getBadgesFor(@Path("uid") uid: String): Call<List<Badge>>

    @GET("user/me/badges")
    fun getMyBadges(): Call<List<Badge>>

    //------POST-----

    @POST("user/{id}/follow")
    fun followUser(@Path("id") uid: String): Call<ResponseBody>

    @POST("user/{id}/unfollow")
    fun unfollowUser(@Path("id") uid: String): Call<ResponseBody>


    //------PUT------
    @PUT("user/me")
    fun uploadUserData(@Body user: User): Call<User>

    @PUT("user/me/notifications/markseenall")
    fun markNotificationSeen(): Call<ResponseBody>

    @PUT("user/me/notifications/markseen")
    fun markSingleNotificationSeen(@Body nid: String): Call<ResponseBody>

    //-----Delete-----

    @DELETE("user/me")
    fun deleteMe(): Call<ResponseBody>

    //================================Memes Related=============================================

    @GET("meme/{id}/refresh")
    fun getRefreshedMeme(@Path("id") id: String): Call<Meme>

    @GET("meme/home")
    fun getHomeMemes(@Query("skip") skip: Int,
                     @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/home/guest")
    fun getHomeMemesForGuest(@Query("skip") skip: Int,
                             @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/trending")
    fun getTrendingMemes(@Query("skip") skip: Int,
                         @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/favourite")
    fun getFavouriteMemes(@Query("skip") skip: Int,
                          @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/favourite")
    fun getFavouriteMemesFor(@Query("uid") uid: String,
                             @Query("skip") skip: Int,
                             @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/me/posts")
    fun getMyMemes(@Query("skip") skip: Int,
                   @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/posts")
    fun getMemesFor(@Query("uid") uid: String,
                    @Query("skip") skip: Int,
                    @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/search")
    fun getFilteredMemes(@Query("query") query: String,
                         @Query("skip") skip: Int,
                         @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/comment")
    fun getCommentForMeme(@Query("mid") mid: String,
                          @Query("skip") skip: Int,
                          @Query("limit") limit: Int): Call<List<Comment>>


    @POST("meme")
    fun postMeme(@Body meme: Meme): Call<Meme>

    @PUT("meme")
    fun updateMeme(@Body meme: Meme): Call<Meme>


    @DELETE("meme/{mid}")
    fun deleteMeme(@Path("mid") mid: String): Call<ResponseBody>


    @POST("meme/favourite")
    fun addMemeToFavourite(@Body mid: String): Call<ResponseBody>

    @DELETE("meme/favourite")
    fun removeMemeFromFavourite(@Body mid: String): Call<ResponseBody>


    @POST("meme/comment")
    fun postComment(@Body comment: Comment): Call<Comment>

    @DELETE("meme/comment")
    fun deleteComment(@Body comment: Comment): Call<ResponseBody>

    @PUT("meme/comment")
    fun updateComment(@Body comment: Comment): Call<ResponseBody>

    @POST("meme/react")
    fun reactToMeme(@Body reaction: Reaction): Call<ResponseBody>

    @DELETE("meme/react")
    fun unreactToMeme(@Body mid: String): Call<ResponseBody>
}