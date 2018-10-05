package com.memeit.backend.dataclasses

interface AuthRequest

data class UsernameAuthRequest(val username: String,
                               val password: String,
                               val email: String? = null) : AuthRequest

data class GoogleAuthSignUpRequest(val username: String, val email: String, val googleID: String) : AuthRequest
data class GoogleAuthSignInRequest(val email: String, val googleID: String) : AuthRequest

data class ChangePasswordRequest(val oldPassword:String,val newPassword:String)

data class AuthResponse(val token: String, val myUser: MyUser)