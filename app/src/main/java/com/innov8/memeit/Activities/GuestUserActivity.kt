package com.innov8.memeit.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.innov8.memeit.R
import com.innov8.memeit.commons.toast

class GuestUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_user)
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener {
            toast(it.link.toString())
        }.addOnFailureListener {
            toast("error ${it.message}")
        }
    }
}
