package com.innov8.memeit.Fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import com.facebook.CallbackManager
import com.innov8.memeit.Activities.AuthActivity
import com.memeit.backend.dataclasses.UsernameAuthRequest
import com.memeit.backend.MemeItClient
import kotlinx.android.synthetic.main.fragment_signup2.*
import com.innov8.memeit.R
import com.memeit.backend.dataclasses.FacebookInfo
import com.memeit.backend.dataclasses.GoogleInfo
import com.memeit.backend.dataclasses.SignInMethod

class SignUpFragment : AuthFragment() {
    lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callbackManager= CallbackManager.Factory.create()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup2, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionButton = signup_btn

        signup_confirm.setOnEditorActionListener { v, _, _ ->
            val mgr = context!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            mgr.hideSoftInputFromWindow(v.windowToken, 0)
            signupWithUsername()
            true
        }
        rel.setPadding(
                0,
                (16 * resources.displayMetrics.density + 0.5f).toInt(),
                0,
                (10 * resources.displayMetrics.density + 0.5f).toInt() + AuthActivity.getSoftButtonsBarHeight(activity))
        rel.setOnClickListener(View.OnClickListener { })
        actionButton.setOnClickListener { signupWithUsername() }
        signup_google.setOnClickListener {
            MemeItClient.Auth.requestGoogleInfo(authActivity)
        }
        signup_facebook.setOnClickListener {
            MemeItClient.Auth.requestFacebookInfo(authActivity,callbackManager,{
                setFacebookMode(it)
            },onError)

        }
        rel.setOnClickListener { authActivity.setCurrentFragment(AuthActivity.FRAGMENT_LOGIN) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MemeItClient.Auth.GOOGLE_SIGN_IN_REQUEST_CODE && data != null) {
            MemeItClient.Auth.extractGoogleInfo(data, {
                setGoogleMode(it)
            }, onError)
        }
    }


    var signInMethod=SignInMethod.USERNAME

    var ginfo: GoogleInfo? = null
    var fbinfo: FacebookInfo? = null
    private fun setAcceptUsernameMode() {
        val set2 = ConstraintSet()
        set2.clone(context, R.layout.fragment_signup2_username)
        TransitionManager.beginDelayedTransition(signup_contraint)
        set2.applyTo(signup_contraint)
        actionButton.text = "Continue"
    }
    private fun setGoogleMode(info:GoogleInfo){
        signInMethod=SignInMethod.GOOGLE
        this.ginfo = info
        setAcceptUsernameMode()
    }
    private fun setFacebookMode(info:FacebookInfo){
        signInMethod=SignInMethod.FACEBOOK
        this.fbinfo =info
        signup_username.text?.append("${info.firstName}${info.lastName}")
        setAcceptUsernameMode()
    }


    private fun signupWithUsername() {
        if (mLoading) return
        val username = signup_username.text.toString()
        when (signInMethod) {
            SignInMethod.GOOGLE -> {
                ginfo ?: return
                val greq = ginfo!!.toSignUpReq(username)
                MemeItClient.Auth.signUpWithGoogle(greq, {
                    authActivity.setupFragment.fromGoogle(ginfo!!.name, ginfo!!.imageUrl)
                    authActivity.setCurrentFragment(AuthActivity.FRAGMENT_SETUP)
                }, onError)
            }
            SignInMethod.FACEBOOK->{
                fbinfo ?: return
                val freq = fbinfo!!.toSignUpReq(username)
                MemeItClient.Auth.signUpWithFacebook(freq, {
                    val name="${fbinfo!!.firstName} ${fbinfo!!.lastName}"
                    authActivity.setupFragment.fromGoogle(name, fbinfo!!.imageUrl)
                    authActivity.setCurrentFragment(AuthActivity.FRAGMENT_SETUP)
                }, onError)
            }
            SignInMethod.USERNAME -> {
                val email = signup_email.text.toString()
                val password = signup_password.text.toString()
                val passwordConfrim = signup_confirm.text.toString()

                val req = UsernameAuthRequest(username, password, email)
                val errors = req.validate()
                when {
                    errors.isNotEmpty() -> authActivity.showError(errors.joinToString(", "))
                    password != passwordConfrim -> authActivity.showError("Password does not match!")
                    else -> {
                        setLoading(true)
                        MemeItClient.Auth.signUpWithUsername(req, {
                            setLoading(false)
                            authActivity.setCurrentFragment(AuthActivity.FRAGMENT_SETUP)
                        }, onError)
                    }
                }
            }
        }
    }
}
