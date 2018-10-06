package com.memeit.backend.dataclasses

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.text.TextUtils

class MyUser {
    var userID: String? = null
    var username: String? = null
    var name: String? = null
    var imageUrl: String? = null
    var coverImageUrl: String? = null

    constructor(uid: String? = null, username: String? = null, name: String? = null, pic: String? = null, cpic: String? = null) {
        this.userID = uid
        this.username = username
        this.name = name
        this.imageUrl = pic
        this.coverImageUrl = cpic
    }

    constructor(user: User) {
        userID = user.userID
        username = user.username
        name = user.name
        imageUrl = user.imageUrl
        coverImageUrl = user.coverImageUrl
    }

    constructor(uid: String) {
        this.userID = uid
    }

    fun save(dest: SharedPreferences) {
        dest.edit()
                .putString(UID, userID)
                .putString(USERNAME, username)
                .putString(NAME, name)
                .putString(PIC, imageUrl)
                .putString(CPIC, coverImageUrl)
                .apply()
    }

    fun save(context: Context) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        save(sp)
    }

    companion object {
        private val UID = "__myuser_uid"
        private val USERNAME = "__myuser_username"
        private val NAME = "__myuser_name"
        private val PIC = "__myuser_pic"
        private val CPIC = "__myuser_cpic"

        fun createFromCache(pref: SharedPreferences): MyUser? {
            val uid = pref.getString(UID, null)
            val username = pref.getString(USERNAME, null)
            val name = pref.getString(NAME, null)
            val pic = pref.getString(PIC, null)
            val cpic = pref.getString(CPIC, null)
            return if (TextUtils.isEmpty(uid))
                null
            else
                MyUser(uid, username, name, pic, cpic)
        }

        fun delete(context: Context) {
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            sp.edit()
                    .remove(UID)
                    .remove(USERNAME)
                    .remove(NAME)
                    .remove(PIC)
                    .remove(CPIC)
                    .apply()
        }
    }


}
