package com.memeit.backend.dataclasses

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.memeit.backend.Utils

interface AuthRequest {
    fun validate(): List<String>
}

data class UsernameAuthRequest(var username: String,
                               var password: String,
                               var email: String? = null) : AuthRequest {
    init {
        username = username.trim()
        email = if (email.isNullOrEmpty()) null else email
    }

    override fun validate(): List<String> {
        val errors = mutableListOf<String>()
        if (username.trim().length < 2) {
            errors.add("Username should at least be 4 in length!")
        }
        if (!Utils.isEmailValidOptional(email)) {
            errors.add("Invalid Email!")

        }
        if (password.length <= 8) {
            errors.add("Password should at least be 8 in length!")
        }
        return errors
    }
}

data class GoogleAuthSignUpRequest(val username: String, val email: String, @SerializedName("gid") val googleID: String, val token: String) : AuthRequest {
    override fun validate(): List<String> {
        return listOf()
    }
}

data class GoogleAuthSignInRequest(@SerializedName("gid") val googleID: String, val token: String) : AuthRequest {
    override fun validate(): List<String> {
        return listOf()
    }
}

data class GoogleInfo(val name: String, val imageUrl: String?, val email: String, val gid: String, val token: String) : Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!)

    fun toSignInReq() = GoogleAuthSignInRequest(gid, token)
    fun toSignUpReq(username: String) = GoogleAuthSignUpRequest(username, email, gid, token)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(imageUrl)
        parcel.writeString(email)
        parcel.writeString(gid)
        parcel.writeString(token)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GoogleInfo> {
        override fun createFromParcel(parcel: Parcel): GoogleInfo {
            return GoogleInfo(parcel)
        }

        override fun newArray(size: Int): Array<GoogleInfo?> {
            return arrayOfNulls(size)
        }
    }

}

data class FacebookInfo(val id: String, val token: String, val firstName: String, val lastName: String, val email: String?) : Parcelable {

    val imageUrl = "https://graph.facebook.com/$id/picture?type=large"

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString())

    fun toSignInReq() = FacebookAuthSignInRequest(id, token)
    fun toSignUpReq(username: String) = FacebookAuthSignUpRequest(username, id, token, email)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(token)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(email)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FacebookInfo> {
        override fun createFromParcel(parcel: Parcel): FacebookInfo {
            return FacebookInfo(parcel)
        }

        override fun newArray(size: Int): Array<FacebookInfo?> {
            return arrayOfNulls(size)
        }
    }


}

data class FacebookAuthSignUpRequest(val username: String,
                                     @SerializedName("fid") val facebookID: String,
                                     val token: String,
                                     val email: String?) : AuthRequest {
    override fun validate(): List<String> {
        return listOf()
    }
}

data class FacebookAuthSignInRequest(@SerializedName("fid") val facebookID: String,
                                     val token: String) : AuthRequest {
    override fun validate(): List<String> {
        return listOf()
    }
}


data class ChangePasswordRequest(val oldPassword: String, val newPassword: String)

data class AuthResponse(val token: String, val user: User)