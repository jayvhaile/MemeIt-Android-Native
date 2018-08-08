package com.memeit.backend.kotlin

import com.memeit.backend.dataclasses.User
import retrofit2.Call

class MemeItAuth {

    val memeInterface = MemeItClient.getInstance().memeInterface
    val defOnFailure: (String,Call<User>) -> Unit = { s: String, call: Call<User> -> }
    fun getUser(onSuccess: (User) -> Unit={},
                onFailure: (String, Call<User>) -> Unit = defOnFailure,
                loadFromCache:Boolean=false){
        memeInterface.getMyUser()
                .enqueue(MyCallback(onSuccess, onFailure))
    }

}
