package com.innov8.memeit.Activities.authModes

import android.os.Bundle
import com.google.firebase.iid.FirebaseInstanceId
import com.innov8.memeit.Activities.AuthActivity
import com.innov8.memeit.Utils.text
import com.memeit.backend.MemeItClient
import com.memeit.backend.models.FacebookInfo
import kotlinx.android.synthetic.main.activity_auth.*

class FacebookSignupMode(private val authActivity: AuthActivity) : RequestUsernameMode(authActivity) {
    lateinit var fbinfo: FacebookInfo
    override fun withBundle(bundle: Bundle) {
        fbinfo = bundle.getParcelable("fbinfo")!!
    }

    override fun onAction() {
        authActivity.setLoading(true)
        val freq = fbinfo.toSignUpReq(authActivity.username_field.text)
        MemeItClient.Auth.signUpWithFacebook(freq, { _ ->
            val name = "${fbinfo.firstName} ${fbinfo.lastName}"
            val b = Bundle()
            b.putString("name", name)
            b.putString("image", fbinfo.imageUrl)
            authActivity.setLoading(false) {
                authActivity.setMode(authActivity.personalizeMode, b)
            }
        }, authActivity.onError)
    }
}