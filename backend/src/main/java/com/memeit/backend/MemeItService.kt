package com.memeit.backend

import com.memeit.backend.models.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface MemeItService {

    @POST("auth/signin")
    fun signInWithEmail(@Body user: UsernameAuthRequest): Call<AuthResponse>

    @POST("auth/signin/google")
    fun signInWithGoogle(@Body user: GoogleAuthSignInRequest): Call<AuthResponse>

    @POST("auth/signin/facebook")
    fun signInWithFacebook(@Body user: FacebookAuthSignInRequest): Call<AuthResponse>

    @POST("auth/signup")
    fun signUpWithEmail(@Body user: UsernameAuthRequest): Call<AuthResponse>


    @POST("auth/signup/google")
    fun signUpWithGoogle(@Body user: GoogleAuthSignUpRequest): Call<AuthResponse>

    @POST("auth/signup/facebook")
    fun signUpWithFacebook(@Body user: FacebookAuthSignUpRequest): Call<AuthResponse>


    @GET("user/me")
    fun loadMyUser(): Call<User>

    @PUT("user/me")
    fun updateUser(@Body user: UserReq): Call<User>

    //todo fix this
    @DELETE("user/me")
    fun deleteMe(@Body user: MyUser): Call<ResponseBody>

    @GET("auth/username")
    fun isUsernameAvailable(@Query("username") username: String): Call<Username>

    @PUT("auth/username")
    fun updateUsername(@Body username: User): Call<ResponseBody>

    @PUT("auth/password")
    fun updatePassword(@Body password: ChangePasswordRequest): Call<ResponseBody>

    @POST("auth/{username}/forgotpassword")
    fun forgotPassword(@Path("username") username: String)


    //==============================================================================================
    @GET("user/{id}/")
    fun getUserById(@Path("id") uid: String): Call<User>

    @GET("user/username/{username}/")
    fun getUserByUserName(@Path("username") username: String): Call<User>


    @GET("user/me/followers")
    fun getMyFollowersList(@Query("skip") skip: Int, @Query("limit") limit: Int): Call<List<User>>

    @GET("user/me/followings")
    fun getMyFollowingList(@Query("skip") skip: Int, @Query("limit") limit: Int): Call<List<User>>

    @GET("user/{id}/followers")
    fun getFollowersListForUser(@Path("id") uid: String,
                                @Query("skip") skip: Int,
                                @Query("limit") limit: Int
    ): Call<List<User>>

    @GET("user/{id}/followings")
    fun getFollowingListForUser(@Path("id") uid: String,
                                @Query("skip") skip: Int,
                                @Query("limit") limit: Int): Call<List<User>>


    @GET("user/me/tags")
    fun getMyTags(
            @Query("skip") skip: Int,
            @Query("limit") limit: Int): Call<List<Tag>>

    @GET("user/{id}/tags")
    fun getTagsFor(
            @Path("id") uid: String,
            @Query("skip") skip: Int,
            @Query("limit") limit: Int): Call<List<Tag>>

    @GET("user/me/notifications")
    fun getMyNotifications(@Query("skip") skip: Int,
                           @Query("limit") limit: Int): Call<List<Map<String, Any>>>

    @GET("user/me/notifications/count")
    fun getNotifCount(): Call<Int>

    @GET("user/{id}/badges")
    fun getBadgesFor(@Path("id") uid: String): Call<List<Badge>>

    @GET("user/search")
    fun searchUser(@Query("name") name: String? = null,
                    @Query("username") username: String? = null,
                    @Query("and") and: Boolean = false,
                    @Query("skip") skip: Int,
                    @Query("limit") limit: Int): Call<List<User>>


    /**
     * @deprecate
     * */
    @GET("user/me/badges")
    fun getMyBadges_DO_NOT_USE(): Call<List<Badge>>

    @GET("user/following/suggestion")
    fun getUserSuggestions(): Call<List<User>>

    //------POST-----


    @POST("user/{id}/follow")
    fun followUser(@Path("id") uid: String): Call<ResponseBody>

    @POST("user/{id}/unfollow")
    fun unfollowUser(@Path("id") uid: String): Call<ResponseBody>


    //------PUT------

    @POST("user/me/follow_tags")
    fun setFollowingTags(@Body tags: Array<String>): Call<ResponseBody>

    @PUT("user/me/follow_tags")
    fun followTags(@Body tags: Array<String>): Call<ResponseBody>

    @DELETE("user/me/follow_tags")
    fun unfollowTag(@Query("tag") tag: String): Call<ResponseBody>


    @POST("user/me/notifications/markseenall")
    fun markNotificationSeen(): Call<ResponseBody>

    @POST("user/me/notifications/{id}/markseen")
    fun markSingleNotificationSeen(@Path("id") nid: String): Call<ResponseBody>

    //-----Delete-----

    @DELETE("user/me")
    fun deleteMe(): Call<ResponseBody>


    //==============================================================================================
    @GET("meme/{id}")
    fun getMemeById(@Path("id") id: String): Call<Meme>

    @GET("meme/{id}/guest")
    fun getMemeByIdGuest(@Path("id") id: String): Call<Meme>

    @GET("meme/{id}/refresh")
    fun getRefreshedMeme(@Path("id") id: String): Call<Meme>

    @GET("meme/home")
    fun getHomeMemes(@Query("skip") skip: Int,
                     @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/trending/guest")
    fun getTrendingMemesForGuest(@Query("skip") skip: Int,
                                 @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/trending")
    fun getTrendingMemes(@Query("skip") skip: Int,
                         @Query("limit") limit: Int): Call<List<Meme>>


    @GET("meme/me/favourite")
    fun getFavouriteMemes(@Query("skip") skip: Int,
                          @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/{uid}/favourite")
    fun getFavouriteMemesFor(@Path("uid") uid: String,
                             @Query("skip") skip: Int,
                             @Query("limit") limit: Int): Call<List<Meme>>


    @GET("meme/me/posts")
    fun getMyMemes(@Query("skip") skip: Int,
                   @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/{uid}/posts")
    fun getMemesFor(@Path("uid") uid: String,
                    @Query("skip") skip: Int,
                    @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/search")
    fun getFilteredMemes(@Query("text") text: String?,
                         @Query("tags") tags: Array<String>,
                         @Query("skip") skip: Int,
                         @Query("limit") limit: Int): Call<List<Meme>>

    @GET("meme/{mid}/reactions")
    fun getReactorsForMeme(@Path("mid") mid: String,
                           @Query("skip") skip: Int,
                           @Query("limit") limit: Int): Call<List<Reaction>>

    @GET("meme/{mid}/reactions/grouped")
    fun getReactionCountByType(@Path("mid") mid: String): Call<List<ReactionGroup>>

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


    @POST("meme/{mid}/favourite")
    fun addMemeToFavourite(@Path("mid") mid: String): Call<ResponseBody>

    @DELETE("meme/{mid}/favourite")
    fun removeMemeFromFavourite(@Path("mid") mid: String): Call<ResponseBody>


    @POST("meme/comment")
    fun postComment(@Body comment: Comment): Call<Comment>

    @DELETE("meme/comment")
    fun deleteComment(@Query("mid") mid: String, @Query("cid") cid: String): Call<ResponseBody>

    @PUT("meme/comment")
    fun updateComment(@Body comment: Comment): Call<ResponseBody>


    @POST("meme/comment/{cid}/like")
    fun likeComment(@Path("cid") commentID: String): Call<ResponseBody>

    @POST("meme/comment/{cid}/dislike")
    fun dislikeComment(@Path("cid") commentID: String): Call<ResponseBody>


    @DELETE("meme/comment/{cid}/like")
    fun removeLikeComment(@Path("cid") commentID: String): Call<ResponseBody>

    @DELETE("meme/comment/{cid}/dislike")
    fun removeDislikeComment(@Path("cid") commentID: String): Call<ResponseBody>

    @POST("meme/react")
    fun reactToMeme(@Body reaction: Reaction): Call<ResponseBody>

    @DELETE("meme/react")
    fun unreactToMeme(@Body mid: String): Call<ResponseBody>


    @GET("meme/tags/popular")
    fun getPopularTags(@Query("search") text: String?,
                       @Query("skip") skip: Int,
                       @Query("limit") limit: Int): Call<List<Tag>>

    @GET("meme/tags/trending")
    fun getTrendingTags(@Query("skip") skip: Int,
                        @Query("limit") limit: Int): Call<List<Tag>>

    @GET("meme/tags/suggested")
    fun getSuggestedTags(@Query("skip") skip: Int,
                         @Query("limit") limit: Int): Call<List<Tag>>

    @POST("others/feedback")
    fun postFeedback(@Body feedback: Feedback): Call<ResponseBody>


    @GET("others/signedUrl")
    fun getSignedUrl(@Query("filename") filename: String, @Query("contentType") contentType: String): Call<MemeItClient.SignedUrl>

    @POST("/auth/ftoken")
    fun updateUserToken(@Body token: FirebaseToken): Call<ResponseBody>

    @POST("user/report")
    fun reportUser(@Body report: Report.UserReport): Call<ResponseBody>

    @POST("meme/report")
    fun reportMeme(@Body report: Report.MemeReport): Call<ResponseBody>


    @Multipart
    @PUT()
    fun uploadImage(@Url url: String, @Part image: MultipartBody.Part): Call<ResponseBody>
}