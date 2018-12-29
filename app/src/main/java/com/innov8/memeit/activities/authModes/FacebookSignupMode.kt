package com.innov8.memeit.activities.authModes

import android.os.Bundle
import com.innov8.memeit.activities.AuthActivity
import com.innov8.memeit.utils.text
import com.innov8.memeit.workers.retrieveAndUploadFirebaseToken
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
            retrieveAndUploadFirebaseToken()
        }, authActivity.onError)
    }
}