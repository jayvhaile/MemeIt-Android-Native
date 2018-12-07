package com.memeit.backend.models

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

enum class SignInMethod {
    USERNAME, GOOGLE, FACEBOOK
}

data class MyUser(internal val token: String,
                  val signInMethod: SignInMethod,
                  val id: String,
                  val username: String,
                  val name: String? = null,
                  val bio: String? = null,
                  val profilePic: String? = null,
                  val coverPic: String? = null,
                  val followerCount: Int = 0,
                  val followingCount: Int = 0,
                  val postCount: Int = 0,
                  val badges: List<Badge> = listOf(),
                  val tags: List<Tag> = listOf()) {
    fun toUser() = User(this)


    companion object {
        private const val KEY_TOKEN = "__myuser_token__"
        private const val KEY_METHOD = "__myuser_method__"
        private const val KEY_UID = "__myuser_uid__"
        private const val KEY_USERNAME = "__myuser_username__"
        private const val KEY_NAME = "__myuser_name__"
        private const val KEY_BIO = "__myuser_bio__"
        private const val KEY_PPIC = "__myuser_pic__"
        private const val KEY_CPIC = "__myuser_cpic__"
        private const val KEY_FOLLOWER_COUNT = "__myuser_fwr_count__"
        private const val KEY_FOLLOWING_COUNT = "__myuser_fwg_count__"
        private const val KEY_POST_COUNT = "__myuser_post_count__"
        private const val KEY_BADGES = "__myuser_badges__"
        private const val KEY_TAGS = "__myuser_tags__"

        private var muser: MyUser? = null
        private var stale = true
        fun save(dest: SharedPreferences,
                 token: String? = null,
                 signInMethod: SignInMethod? = null,
                 id: String? = null,
                 username: String? = null,
                 name: String? = null,
                 bio: String? = null,
                 profilePic: String? = null,
                 coverPic: String? = null,
                 followerCount: Int? = null,
                 followingCount: Int? = null,
                 postCount: Int? = null,
                 badges: List<Badge>? = null,
                 tags: List<Tag>? = null) {
            val e = dest.edit()

            if (token != null) e.putString(KEY_TOKEN, token)
            if (signInMethod != null) e.putString(KEY_METHOD, signInMethod.toString())
            if (id != null) e.putString(KEY_UID, id)
            if (username != null) e.putString(KEY_USERNAME, username)
            if (name != null) e.putString(KEY_NAME, name)
            if (bio != null) e.putString(KEY_BIO, bio)
            if (profilePic != null) e.putString(KEY_PPIC, profilePic)
            if (coverPic != null) e.putString(KEY_CPIC, coverPic)
            if (followerCount != null) e.putInt(KEY_FOLLOWER_COUNT, followerCount)
            if (followingCount != null) e.putInt(KEY_FOLLOWING_COUNT, followingCount)
            if (postCount != null) e.putInt(KEY_POST_COUNT, postCount)

            if (tags != null) {
                val t = setOf(*tags.map { it.tag }.toTypedArray())
                e.putStringSet(KEY_TAGS, t)
            }
            if (badges != null) {
                val t = setOf(*badges.map { it.id }.toTypedArray())
                e.putStringSet(KEY_BADGES, t)
            }
            e.apply()
            stale = true
        }

        fun save(dest: SharedPreferences, token: String? = null,
                 signInMethod: SignInMethod? = null, user: User) {
            save(dest,
                    token,
                    signInMethod,
                    id = user.uid,
                    username = user.username,
                    name = user.name,
                    bio = user.bio,
                    profilePic = user.imageUrl,
                    coverPic = user.coverImageUrl,
                    followerCount = user.followerCount,
                    followingCount = user.followingCount,
                    postCount = user.postCount)
        }


        fun get(pref: SharedPreferences): MyUser? {
            if (stale) {
                muser = createFromCache(pref)
                stale = false
            }
            return muser
        }

        private fun createFromCache(pref: SharedPreferences): MyUser? {
            val token = pref.getString(KEY_TOKEN, null)
            var method: SignInMethod? = null
            pref.getString(KEY_METHOD, null)?.let { method = SignInMethod.valueOf(it) }
            val uid = pref.getString(KEY_UID, null)
            val username = pref.getString(KEY_USERNAME, null)
            val name = pref.getString(KEY_NAME, null)
            val bio = pref.getString(KEY_BIO, null)
            val pic = pref.getString(KEY_PPIC, null)
            val cpic = pref.getString(KEY_CPIC, null)
            val fwrc = pref.getInt(KEY_FOLLOWER_COUNT, 0)
            val fwgc = pref.getInt(KEY_FOLLOWING_COUNT, 0)
            val postc = pref.getInt(KEY_POST_COUNT, 0)
            val badgeSet = pref.getStringSet(KEY_BADGES, null)
            val tagSet = pref.getStringSet(KEY_TAGS, null)
            val badges: List<Badge> = badgeSet?.map { Badge.ofID(it) } ?: listOf()
            val tags: List<Tag> = tagSet?.map { Tag(it, 0, 0) } ?: listOf()
            return if (token.isNullOrEmpty() ||
                    method == null ||
                    uid.isNullOrEmpty() ||
                    username.isNullOrEmpty())
                null
            else
                MyUser(token!!, method!!, uid!!, username!!, name, bio, pic, cpic, fwrc, fwgc, postc, badges, tags)
        }

        fun delete(context: Context) {
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            sp.edit()
                    .remove(KEY_TOKEN)
                    .remove(KEY_UID)
                    .remove(KEY_USERNAME)
                    .remove(KEY_NAME)
                    .remove(KEY_BIO)
                    .remove(KEY_PPIC)
                    .remove(KEY_CPIC)
                    .remove(KEY_FOLLOWER_COUNT)
                    .remove(KEY_FOLLOWING_COUNT)
                    .remove(KEY_POST_COUNT)
                    .remove(KEY_BADGES)
                    .remove(KEY_TAGS)
                    .apply()
            stale = true
        }


    }

}
