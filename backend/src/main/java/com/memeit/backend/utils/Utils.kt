package com.memeit.backend.utils

import android.text.TextUtils

import java.util.regex.Pattern


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


fun <T> generateFactory(clazz: Class<T>, subClasses: List<Class<out T>>): RuntimeTypeAdapterFactory<T> {
    val r = RuntimeTypeAdapterFactory.of(clazz)
    subClasses.forEach {
        r.registerSubtype(it, it.simpleName)
    }
    return r
}

