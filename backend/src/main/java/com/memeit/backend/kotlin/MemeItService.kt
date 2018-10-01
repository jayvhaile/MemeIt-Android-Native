package com.memeit.backend.kotlin

import com.memeit.backend.dataclasses.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface MemeItService {

    @POST("auth/signin")
    abstract fun loginWithEmail(@Body user: AuthInfo): Call<AuthToken>

    @POST("auth/signin/google")
    abstract fun loginWithGoogle(@Body user: AuthInfo): Call<AuthToken>

    @POST("auth/signup")
    abstract fun signUpWithEmail(@Body user: AuthInfo): Call<AuthToken>


    @POST("auth/signup/google")
    abstract fun signUpWithGoogle(@Body user: AuthInfo): Call<AuthToken>

    @DELETE("user/me")
    abstract fun deleteMe(@Body user: AuthInfo): Call<AuthToken>

    @GET("auth/username")
    abstract fun isUsernameAvailable(@Query("username") username: String): Call<Username>

    @PUT("auth/username")
    abstract fun updateUsername(@Body username: User): Call<ResponseBody>

    @PUT("auth/password")
    abstract fun updatePassword(@Body password: AuthInfo): Call<ResponseBody>

    //=========================User Related=====================================================
    //------GET------
    @GET("user/me")
    abstract fun getMyUser(): Call<User>

    @GET("user/{id}/")
    abstract fun getUserById(@Path("id") uid: String): Call<User>


    @GET("user/me/finished")
    abstract fun isMyUserDataSaved(): Call<Boolean>

    @GET("user/me/followers")
    abstract fun getMyFollowersList(@Query("skip") skip: Int, @Query("limit") limit: Int): Call<List<User>>

    @GET("user/me/followings")
    abstract fun getMyFollowingList(@Query("skip") skip: Int, @Query("limit") limit: Int): Call<List<User>>

    @GET("user/{id}/followers")
    abstract fun getFollowersListForUser(@Path("id") uid: String,
                                         @Query("skip") skip: Int,
                                         @Query("limit") limit: Int
    ): Call<List<User>>

    @GET("user/{id}/followings")
    abstract fun getFollowingListForUser(@Path("id") uid: String,
                                         @Query("skip") skip: Int,
                                         @Query("limit") limit: Int): Call<List<User>>


    @GET("user/me/tags")
    abstract fun getMyTags(
            @Query("skip") skip: Int,
            @Query("limit") limit: Int): Call<List<Tag>>

    @GET("user/{id}/tags")
    abstract fun getTagsFor(
            @Path("id") uid: String,
            @Query("skip") skip: Int,
            @Query("limit") limit: Int): Call<List<Tag>>

    @GET("user/me/notifications")
    abstract fun getMyNotifications(@Query("skip") skip: Int,
                                    @Query("limit") limit: Int): Call<List<Map<String, Any>>>

    @GET("user/me/notifications/count")
    abstract fun getNotifCount(): Call<Int>

    @GET("user/{id}/badges")
    abstract fun getBadgesFor(@Path("uid") uid: String): Call<List<Badge>>

    @GET("user/me/badges")
    abstract fun getMyBadges(): Call<List<Badge>>

    @GET("user/following/suggestion")
    abstract fun getUserSuggestions(): Call<List<User>>

    //------POST-----


    @POST("user/{id}/follow")
    abstract fun followUser(@Path("id") uid: String): Call<ResponseBody>

    @POST("user/{id}/unfollow")
    abstract fun unfollowUser(@Path("id") uid: String): Call<ResponseBody>


    //------PUT------
    @PUT("user/me/setup")
    abstract fun uploadUserData(@Body user: User): Call<ResponseBody>

    @PUT("user/me/name")
    abstract fun updateName(@Body name: User): Call<ResponseBody>

    @PUT("user/me/profile_pic")
    abstract fun updateProfilePic(@Body pic: User): Call<ResponseBody>

    @PUT("user/me/cover_pic")
    abstract fun updateCoverPic(@Body cpic: User): Call<ResponseBody>

    @POST("user/me/follow_tags")
    abstract fun setFollowingTags(@Body tags: Array<String>): Call<ResponseBody>

    @PUT("user/me/follow_tags")
    abstract fun followTags(@Body tags: Array<String>): Call<ResponseBody>

    @DELETE("user/me/follow_tags")
    abstract fun unfollowTag(@Query("tag") tag: String): Call<ResponseBody>


    @POST("user/me/notifications/markseenall")
    abstract fun markNotificationSeen(): Call<ResponseBody>

    @POST("user/me/notifications/{id}/markseen")
    abstract fun markSingleNotificationSeen(@Path("id") nid: String): Call<ResponseBody>

    //-----Delete-----

    @DELETE("user/me")
    abstract fun deleteMe(): Call<ResponseBody>

    //================================Memes Related=============================================

    @GET("meme/{id}")
    abstract fun getMemeById(@Path("id") id: String): Call<Meme>

    @GET("meme/{id}/refresh")
    abstract fun getRefreshedMeme(@Path("id") id: String): Call<Meme>

    @GET("meme/home")
    abstract fun getHomeMemes(@Query("skip") skip: Int,
                              @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/home/guest")
    abstract fun getHomeMemesForGuest(@Query("skip") skip: Int,
                                      @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/trending")
    abstract fun getTrendingMemes(@Query("skip") skip: Int,
                                  @Query("limit") limit: Int): Call<List<Meme>>


    @GET("meme/me/favourite")
    abstract fun getFavouriteMemes(@Query("skip") skip: Int,
                                   @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/{uid}/favourite")
    abstract fun getFavouriteMemesFor(@Path("uid") uid: String,
                                      @Query("skip") skip: Int,
                                      @Query("limit") limit: Int): Call<List<Meme>>


    @GET("meme/me/posts")
    abstract fun getMyMemes(@Query("skip") skip: Int,
                            @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/{uid}/posts")
    abstract fun getMemesFor(@Path("uid") uid: String,
                             @Query("skip") skip: Int,
                             @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/search")
    abstract fun getFilteredMemes(@Query("text") text: String,
                                  @Query("tags") tags: Array<String>,
                                  @Query("skip") skip: Int,
                                  @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/{mid}/reactions")
    abstract fun getReactorsForMeme(@Path("mid") mid: String,
                                    @Query("skip") skip: Int,
                                    @Query("limit") limit: Int): Call<List<Reaction>>

    @GET("meme/comment")
    abstract fun getCommentForMeme(@Query("mid") mid: String,
                                   @Query("skip") skip: Int,
                                   @Query("limit") limit: Int): Call<List<Comment>>


    @POST("meme")
    abstract fun postMeme(@Body meme: Meme): Call<Meme>

    @PUT("meme")
    abstract fun updateMeme(@Body meme: Meme): Call<Meme>


    @DELETE("meme/{mid}")
    abstract fun deleteMeme(@Path("mid") mid: String): Call<ResponseBody>


    @POST("meme/{mid}/favourite")
    abstract fun addMemeToFavourite(@Path("mid") mid: String): Call<ResponseBody>

    @DELETE("meme/{mid}/favourite")
    abstract fun removeMemeFromFavourite(@Path("mid") mid: String): Call<ResponseBody>


    @POST("meme/comment")
    abstract fun postComment(@Body comment: Comment): Call<Comment>

    @DELETE("meme/comment")
    abstract fun deleteComment(@Query("mid") mid: String, @Query("cid") cid: String): Call<ResponseBody>

    @PUT("meme/comment")
    abstract fun updateComment(@Body comment: Comment): Call<ResponseBody>


    @POST("meme/comment/{cid}/like")
    abstract fun likeComment(@Path("cid") commentID: String): Call<ResponseBody>

    @POST("meme/comment/{cid}/dislike")
    abstract fun dislikeComment(@Path("cid") commentID: String): Call<ResponseBody>


    @DELETE("meme/comment/{cid}/like")
    abstract fun removeLikeComment(@Path("cid") commentID: String): Call<ResponseBody>

    @DELETE("meme/comment/{cid}/dislike")
    abstract fun removeDislikeComment(@Path("cid") commentID: String): Call<ResponseBody>

    @POST("meme/react")
    abstract fun reactToMeme(@Body reaction: Reaction): Call<ResponseBody>

    @DELETE("meme/react")
    abstract fun unreactToMeme(@Body mid: String): Call<ResponseBody>


    @GET("meme/tags/popular")
    abstract fun getPopularTags(@Query("search") text: String,
                                @Query("skip") skip: Int,
                                @Query("limit") limit: Int): Call<List<Tag>>

    @GET("meme/tags/trending")
    abstract fun getTrendingTags(@Query("skip") skip: Int,
                                 @Query("limit") limit: Int): Call<List<Tag>>

    @GET("meme/tags/suggested")
    abstract fun getSuggestedTags(@Query("skip") skip: Int,
                                  @Query("limit") limit: Int): Call<List<Tag>>

}