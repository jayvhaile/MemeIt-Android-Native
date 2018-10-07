package com.memeit.backend.dataclasses

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

enum class SignInMethod {
    USERNAME, GOOGLE, FACEBOOK
}

data class MUser(internal val token: String,
                 val signInMethod: SignInMethod,
                 val id: String,
                 val username: String,
                 val name: String? = null,
                 val profilePic: String? = null,
                 val coverPic: String? = null,
                 val tags: List<Tag> = listOf()) {

    companion object {
        private const val KEY_TOKEN = "__myuser_token__"
        private const val KEY_METHOD = "__myuser_method__"
        private val KEY_UID = "__myuser_uid__"
        private val KEY_USERNAME = "__myuser_username__"
        private val KEY_NAME = "__myuser_name__"
        private val KEY_PPIC = "__myuser_pic__"
        private val KEY_CPIC = "__myuser_cpic__"
        private val KEY_TAGS = "__myuser_tags__"

        var muser: MUser? = null
        var stale = true
        fun save(dest: SharedPreferences,
                 token: String? = null,
                 signInMethod: SignInMethod? = null,
                 id: String? = null,
                 username: String? = null,
                 name: String? = null,
                 profilePic: String? = null,
                 coverPic: String? = null,
                 tags: List<Tag>? = null) {
            val e = dest.edit()

            if (token != null) e.putString(KEY_TOKEN, token)
            if (signInMethod != null) e.putString(KEY_METHOD, signInMethod.toString())
            if (id != null) e.putString(KEY_UID, id)
            if (username != null) e.putString(KEY_USERNAME, username)
            if (name != null) e.putString(KEY_NAME, name)
            if (profilePic != null) e.putString(KEY_PPIC, profilePic)
            if (coverPic != null) e.putString(KEY_CPIC, coverPic)

            if (tags != null) {
                val t = setOf(*tags.map { it.tag }.toTypedArray())
                e.putStringSet(KEY_TAGS, t)
            }
            e.apply()
            stale = true
        }

        fun save(dest: SharedPreferences, user: User) {
            save(dest, id = user.uid,
                    username = user.username,
                    name = user.name,
                    profilePic = user.imageUrl,
                    coverPic = user.coverImageUrl)
        }


        fun get(pref: SharedPreferences): MUser? {
            if (stale) {
                muser = createFromCache(pref)
                stale = false
            }
            return muser
        }

        private fun createFromCache(pref: SharedPreferences): MUser? {
            val token = pref.getString(KEY_TOKEN, null)
            var method: SignInMethod? = null
            pref.getString(KEY_METHOD, null)?.let { method = SignInMethod.valueOf(it) }
            val uid = pref.getString(KEY_UID, null)
            val username = pref.getString(KEY_USERNAME, null)
            val name = pref.getString(KEY_NAME, null)
            val pic = pref.getString(KEY_PPIC, null)
            val cpic = pref.getString(KEY_CPIC, null)
            val tagSet = pref.getStringSet(KEY_TAGS, null)
            val tags: List<Tag> = tagSet?.map { Tag(it, 0, 0) } ?: listOf()
            return if (token.isNullOrEmpty() ||
                    method == null ||
                    uid.isNullOrEmpty() ||
                    username.isNullOrEmpty())
                null
            else
                MUser(token!!, method!!, uid!!, username!!, name, pic, cpic, tags)
        }

        fun delete(context: Context) {
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            sp.edit()
                    .remove(KEY_TOKEN)
                    .remove(KEY_UID)
                    .remove(KEY_USERNAME)
                    .remove(KEY_NAME)
                    .remove(KEY_PPIC)
                    .remove(KEY_CPIC)
                    .remove(KEY_TAGS)
                    .apply()
        }

    }

}
