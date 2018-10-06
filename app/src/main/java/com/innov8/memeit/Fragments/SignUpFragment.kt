package com.innov8.memeit.Fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.innov8.memeit.Activities.AuthActivity
import com.memeit.backend.MemeItAuth
import com.memeit.backend.dataclasses.UsernameAuthRequest
import com.memeit.backend.kotlin.MemeItClient
import kotlinx.android.synthetic.main.fragment_signup2.*

class SignUpFragment : AuthFragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_signup2, container, false)

        actionButton = signup_btn

        signup_confirm.setOnEditorActionListener { v, _, _ ->
            val mgr = context!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            mgr.hideSoftInputFromWindow(v.windowToken, 0)
            signupWithUsername()
            true
        }
        /* This sets the bottom padding that makes the views not go underneath the navigation bar */
        rel.setPadding(
                0,
                (16 * resources.displayMetrics.density + 0.5f).toInt(),
                0,
                (10 * resources.displayMetrics.density + 0.5f).toInt() + AuthActivity.getSoftButtonsBarHeight(activity))
        rel.setOnClickListener(View.OnClickListener { })
        actionButton.setOnClickListener{signupWithUsername()}
        signup_google.setOnClickListener {
            MemeItClient.Auth.requestGoogleInfo(authActivity)
        }
        signup_facebook.setOnClickListener{}
        rel.setOnClickListener{authActivity.setCurrentFragment(AuthActivity.FRAGMENT_LOGIN)}
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MemeItAuth.GOOGLE_SIGNIN_REQUEST_CODE && data!=null) {
            val req=MemeItClient.Auth.extractGoogleAuthRequest(data,"")//todo request username here
            MemeItClient.Auth.signUpWithGoogle(req,{
                val (name,imageUrl)=MemeItClient.Auth.extractGoogleInfo(data)
                authActivity.setupFragment.fromGoogle(name,imageUrl)
                authActivity.setCurrentFragment(AuthActivity.FRAGMENT_SETUP)
            },onError)
        }
    }


    private fun signupWithUsername() {
        if (mLoading) return

        setLoading(true)
        val username = signup_username.text.toString()
        val email=signup_email.text.toString()
        val password = signup_password.text.toString()
        val passwordConfrim = signup_confirm.text.toString()

        val req=UsernameAuthRequest(username,password,email)
        val errors=req.validate()
        when {
            errors.isNotEmpty() -> authActivity.showError(errors.joinToString(", ") )
            password != passwordConfrim -> authActivity.showError("Password does not match!")
            else -> {
                setLoading(true)
                MemeItClient.Auth.signUpWithUsername(req,{
                    setLoading(false)
                    authActivity.setCurrentFragment(AuthActivity.FRAGMENT_SETUP)
                },onError)
            }
        }
    }
}
