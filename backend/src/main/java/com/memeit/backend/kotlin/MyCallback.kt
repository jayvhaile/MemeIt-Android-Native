package com.memeit.backend.kotlin

import com.memeit.backend.dataclasses.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCallback<T>(val onSuccess:(T)->Unit, val onFailure:(String,Call<T>)->Unit) : Callback<T> {
    override fun onResponse(p0: Call<T>?, p1: Response<T>?) {
        onSuccess(p1?.body()!!)
    }

    override fun onFailure(p0: Call<T>?, p1: Throwable?) {
        onFailure(p1?.message!!,p0!!)

    }

}