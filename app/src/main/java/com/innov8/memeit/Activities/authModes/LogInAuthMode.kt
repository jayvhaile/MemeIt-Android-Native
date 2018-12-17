package com.innov8.memeit.Activities.authModes

import android.content.Intent
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.iid.FirebaseInstanceId
import com.innov8.memeit.Activities.AuthActivity
import com.innov8.memeit.Activities.MainActivity
import com.innov8.memeit.R
import com.innov8.memeit.Utils.*
import com.innov8.memeit.Workers.retrieveAndUploadFirebaseToken
import com.memeit.backend.MemeItClient
import com.memeit.backend.models.User
import com.memeit.backend.models.UsernameAuthRequest
import kotlinx.android.synthetic.main.activity_auth.*

class LogInAuthMode(private val authActivity: AuthActivity) : AuthMode {
    override fun applyContraint(constraintSet: ConstraintSet) {
        AuthMode.makeIntroGone(constraintSet)
        AuthMode.makeGoogleFacebookVisible(constraintSet)
        constraintSet.makeGone(
                R.id.setup_personalize,
                R.id.email_field,
                R.id.confirm_password_field,
                R.id.profile_pic
        )
        constraintSet.makeVisible(
                R.id.app_title,
                R.id.username_field,
                R.id.password_field,
                R.id.or_continue_with,
                R.id.auth_question,
                R.id.auth_question_action,
                R.id.action_holder
        )

    }

    override fun verifable(): Boolean = true

    override fun updateElements() {
        authActivity.action_btn.text = "Log In"
        authActivity.auth_question.text = "Don't have an account?"
        authActivity.auth_question_action.text = "Sign up"
        authActivity.username_field.editText!!.hint = "Username"

        authActivity.username_field.clear()
        authActivity.password_field.clear()
    }

    override fun onAction() {
        authActivity.setLoading(true)
        retrieveAndUploadFirebaseToken()

        val req = UsernameAuthRequest(authActivity.username_field.text, authActivity.password_field.text)
        MemeItClient.Auth.signInWithUsername(req, onSignedIn, authActivity.onError)

    }

    override fun getLastField(): TextInputLayout? = authActivity.password_field


    private val onSignedIn by lazy {
        { _: User ->
            authActivity.setLoading(false)
            val i = Intent(authActivity, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            authActivity.startActivity(i)
        }
    }

    override fun onVerify(): Boolean {
        return booleanArrayOf(
                authActivity.username_field.validateLength(2, 32, "Username"),
                authActivity.password_field.validateLength(8, 32, "Password")
        ).all { it }
    }

    override fun onFacebook() {
        //todo only request the id and token
        MemeItClient.Auth.requestFacebookID(authActivity, authActivity.callbackManager, {
            MemeItClient.Auth.signInWithFacebook(it, onSignedIn, authActivity.onError)
        }, authActivity.onError)
    }

    override fun onGoogle() {
        MemeItClient.Auth.requestGoogleInfo(authActivity)
    }

    override fun onGoto() {
        authActivity.setMode(authActivity.signupMode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        authActivity.callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MemeItClient.Auth.GOOGLE_SIGN_IN_REQUEST_CODE && data != null) {
            MemeItClient.Auth.extractGoogleInfo(data, {
                MemeItClient.Auth.signInWithGoogle(it.toSignInReq(), onSignedIn, authActivity.onError)
            }, authActivity.onError)
        }
    }

    override fun onBackPressed(): Boolean {
        authActivity.setMode(authActivity.introMode)
        return true
    }
}