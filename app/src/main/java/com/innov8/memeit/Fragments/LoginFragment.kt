package com.innov8.memeit.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.innov8.memeit.Activities.AuthActivity
import com.innov8.memeit.Activities.MainActivity
import com.innov8.memeit.R
import android.content.Context.INPUT_METHOD_SERVICE
import com.memeit.backend.dataclasses.UsernameAuthRequest
import com.memeit.backend.MemeItClient
import com.memeit.backend.dataclasses.User
import kotlinx.android.synthetic.main.fragment_login2.*

class LoginFragment : AuthFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionButton = login_btn
        actionButton.setOnClickListener { loginWithUserName() }
        login_google.setOnClickListener { MemeItClient.Auth.requestGoogleInfo(authActivity) }
        login_facebook.setOnClickListener {}
        rel.setOnClickListener { authActivity.setCurrentFragment(AuthActivity.FRAGMENT_SIGNUP) }
        login_password.setOnEditorActionListener { v, _, _ ->
            val mgr = context!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            mgr.hideSoftInputFromWindow(v.windowToken, 0)
            loginWithUserName()
            true
        }
    }

    private val onSignedIn = { _: User ->
        startActivity(Intent(context, MainActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MemeItClient.Auth.GOOGLE_SIGN_IN_REQUEST_CODE) {
            MemeItClient.Auth.extractGoogleInfo(data!!,{
                MemeItClient.Auth.signInWithGoogle(it.toSignInReq(), onSignedIn, onError)
            },onError)
        }
    }

    private fun loginWithUserName() {
        if (mLoading) return
        val username = login_username.text.toString()
        val password = login_password.text.toString()

        val req = UsernameAuthRequest(username, password)
        val errors = req.validate()
        if (errors.isNotEmpty()) {
            authActivity.showError(errors.joinToString(", "))
        } else {
            setLoading(true)
            MemeItClient.Auth.signInWithUsername(req, onSignedIn, onError)
        }
    }
}
