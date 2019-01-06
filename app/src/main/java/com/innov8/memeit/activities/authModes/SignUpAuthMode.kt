package com.innov8.memeit.activities.authModes

import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.material.textfield.TextInputLayout
import com.innov8.memeit.activities.AuthActivity
import com.innov8.memeit.R
import com.innov8.memeit.utils.*
import com.innov8.memeit.workers.retrieveAndUploadFirebaseToken
import com.memeit.backend.MemeItClient
import com.memeit.backend.models.UsernameAuthRequest
import kotlinx.android.synthetic.main.activity_auth.*

class SignUpAuthMode(private val authActivity: AuthActivity) : AuthMode {
    override fun applyContraint(constraintSet: ConstraintSet) {
        AuthMode.makeIntroGone(constraintSet)
        AuthMode.makeGoogleFacebookVisible(constraintSet)
        constraintSet.makeGone(
                R.id.setup_personalize,
                R.id.profile_pic
        )
        constraintSet.makeVisible(
                R.id.app_title,
                R.id.username_field,
                R.id.email_field,
                R.id.password_field,
                R.id.confirm_password_field,
                R.id.or_continue_with,
                R.id.auth_question,
                R.id.action_holder
        )
    }

    override fun verifable(): Boolean = true

    override fun updateElements() {
        authActivity.action_btn.text = "Sign Up"
        authActivity.auth_question.text = "Already have an account? Login".toUnderlinedLinkSpan("Login") {
            authActivity.onGoto()
        }
        authActivity.username_field.editText!!.hint = "Username"
        authActivity.username_field.clear()
        authActivity.email_field.clear()
        authActivity.password_field.clear()
        authActivity.confirm_password_field.clear()
    }

    override fun onVerify(): Boolean {
        return booleanArrayOf(
                authActivity.username_field.validateLength(2, 32, "Username"),
                authActivity.email_field.validateEmailorEmpty(),
                authActivity.password_field.validateLength(8, 32, "Password"),
                (authActivity.password_field to authActivity.confirm_password_field).validateMatch("Password")
        ).all { it }
    }

    override fun onAction() {
        authActivity.setLoading(true)
        val req = UsernameAuthRequest(authActivity.username_field.text,
                authActivity.password_field.text,
                authActivity.email_field.text)
        MemeItClient.Auth.signUpWithUsername(req, {
            authActivity.setLoading(false) {
                authActivity.setMode(authActivity.personalizeMode)
            }
            retrieveAndUploadFirebaseToken()

        }, authActivity.onError)
    }

    override fun onFacebook() {
        MemeItClient.Auth.requestFacebookInfo(authActivity, authActivity.callbackManager, {
            authActivity.setMode(authActivity.facebookSignupMode, Bundle().apply { putParcelable("fbinfo", it) })
        }, authActivity.onError)

    }

    override fun onGoogle() {
        MemeItClient.Auth.requestGoogleInfo(authActivity)
    }

    override fun onGoto() {
        authActivity.setMode(authActivity.loginMode)
    }

    override fun getLastField(): TextInputLayout? = authActivity.confirm_password_field

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        authActivity.callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MemeItClient.Auth.GOOGLE_SIGN_IN_REQUEST_CODE && data != null) {
            MemeItClient.Auth.extractGoogleInfo(data, {
                authActivity.setMode(authActivity.googleSignupMode, Bundle().apply { putParcelable("ginfo", it) })
            }, authActivity.onError)
        }
    }

    override fun onBackPressed(): Boolean {
        authActivity.setMode(authActivity.introMode)
        return true
    }
}