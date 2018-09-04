package com.memeit.backend.utilis

import android.content.Context
import android.widget.Toast
import com.memeit.backend.utilis.OnCompleteListener.Error

class Listener<T>(val onsuccess: (T) -> Unit = {}, val onfailure: (Error) -> Unit = {}) : OnCompleteListener<T> {
    constructor(context: Context?,message:String,onSuccess: (T) -> Unit = {}) : this(onSuccess, {
        if (context != null)
            Toast.makeText(context,message+"\t"+ it.message, Toast.LENGTH_SHORT).show()
    })
    constructor(onSuccess: (T) -> Unit = {}) : this(onSuccess,{})

    override fun onSuccess(t: T) {
        onsuccess(t)
    }

    override fun onFailure(error: Error) {
        onfailure(error)
    }
}