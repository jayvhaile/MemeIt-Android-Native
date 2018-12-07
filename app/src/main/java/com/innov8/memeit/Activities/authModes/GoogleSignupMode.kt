package com.innov8.memeit.Activities.authModes

import android.os.Bundle
import com.google.firebase.iid.FirebaseInstanceId
import com.innov8.memeit.Activities.AuthActivity
import com.innov8.memeit.Utils.text
import com.memeit.backend.MemeItClient
import com.memeit.backend.models.GoogleInfo
import kotlinx.android.synthetic.main.activity_auth.*

class GoogleSignupMode(private val authActivity: AuthActivity) : RequestUsernameMode(authActivity) {
    lateinit var ginfo: GoogleInfo
    override fun withBundle(bundle: Bundle) {
        ginfo = bundle.getParcelable("ginfo")!!
    }

    override fun onAction() {
        authActivity.setLoading(true)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            val greq = ginfo.toSignUpReq(authActivity.username_field.text, it.token)
            MemeItClient.Auth.signUpWithGoogle(greq, {
                val b = Bundle()
                b.putString("name", ginfo.name)
                b.putString("image", ginfo.imageUrl)
                authActivity.setLoading(false) {
                    authActivity.setMode(authActivity.personalizeMode, b)
                }
            }, authActivity.onError)
        }.addOnFailureListener {
            authActivity.onError(it.message!!)
        }

    }
}