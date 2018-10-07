package com.innov8.memeit.Fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import com.innov8.memeit.Activities.AuthActivity
import com.memeit.backend.dataclasses.UsernameAuthRequest
import com.memeit.backend.MemeItClient
import kotlinx.android.synthetic.main.fragment_signup2.*
import com.innov8.memeit.R
import com.memeit.backend.dataclasses.GoogleInfo

class SignUpFragment : AuthFragment() {

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
        signup_facebook.setOnClickListener {}
        rel.setOnClickListener { authActivity.setCurrentFragment(AuthActivity.FRAGMENT_LOGIN) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MemeItClient.Auth.GOOGLE_SIGN_IN_REQUEST_CODE && data != null) {
            MemeItClient.Auth.extractGoogleInfo(data, {
                setGoogleMode(it)
            }, onError)
        }
    }

    var googleMode = false

    var ginfo: GoogleInfo? = null
    private fun setGoogleMode(info: GoogleInfo) {
        googleMode = true
        this.ginfo = info
        val set1 = ConstraintSet()
        set1.clone(signup_contraint)
        val set2 = ConstraintSet()
        set2.clone(context, R.layout.fragment_signup2_username)
        TransitionManager.beginDelayedTransition(signup_contraint)
        set2.applyTo(signup_contraint)
        actionButton.text = "Continue"
    }


    private fun signupWithUsername() {
        if (mLoading) return
        val username = signup_username.text.toString()
        if (googleMode) {
            ginfo ?: return
            val greq = ginfo!!.toSignUpReq(username)
            MemeItClient.Auth.signUpWithGoogle(greq, {
                authActivity.setupFragment.fromGoogle(ginfo!!.name, ginfo!!.imageUrl)
                authActivity.setCurrentFragment(AuthActivity.FRAGMENT_SETUP)
            }, onError)
        } else {
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
