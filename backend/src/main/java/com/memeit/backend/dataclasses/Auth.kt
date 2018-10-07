package com.memeit.backend.dataclasses

import com.google.gson.annotations.SerializedName
import com.memeit.backend.utilis.Utils

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
        if (username.trim().length < 4) {
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

data class GoogleAuthSignUpRequest(val username: String, val email: String, val googleID: String) : AuthRequest {
    override fun validate(): List<String> {
        return listOf()
    }
}
data class GoogleInfo(val name:String,val imageUrl:String?,val email:String,val gid:String){

    fun toSignInReq()=GoogleAuthSignInRequest(email,gid)
    fun toSignUpReq(username: String)=GoogleAuthSignUpRequest(username,email,gid)
}


data class GoogleAuthSignInRequest(val email: String, val googleID: String) : AuthRequest {
    override fun validate(): List<String> {
        return listOf()
    }
}

data class ChangePasswordRequest(val oldPassword: String, val newPassword: String)

data class AuthResponse(val token: String,val user:User)