package com.innov8.memeit.activities.authModes

import android.os.Bundle
import com.innov8.memeit.activities.AuthActivity
import com.innov8.memeit.utils.text
import com.innov8.memeit.workers.retrieveAndUploadFirebaseToken
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
        val greq = ginfo.toSignUpReq(authActivity.username_field.text)
        MemeItClient.Auth.signUpWithGoogle(greq, {
            val b = Bundle()
            b.putString("name", ginfo.name)
            b.putString("image", ginfo.imageUrl)
            authActivity.setLoading(false) {
                authActivity.setMode(authActivity.personalizeMode, b)
            }
            retrieveAndUploadFirebaseToken()

        }, authActivity.onError)

    }
}