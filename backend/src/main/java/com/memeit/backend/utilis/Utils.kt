package com.memeit.backend.utilis

import android.net.Uri
import android.text.TextUtils

import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback

import java.util.regex.Matcher
import java.util.regex.Pattern

object Utils {

    fun isEmailValid(email: String): Boolean {
        if (TextUtils.isEmpty(email)) return false
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun isEmailValidOptional(email: String?): Boolean {
        if (email.isNullOrEmpty()) return true
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
}
