package com.innov8.memeit.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import com.facebook.CallbackManager
import com.google.android.material.snackbar.Snackbar
import com.innov8.memeit.activities.authModes.*
import com.innov8.memeit.utils.CustomMethods
import com.innov8.memeit.utils.ScrollingImageDrawable
import com.innov8.memeit.R
import com.innov8.memeit.utils.*
import com.innov8.memeit.activities.authModes.*
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    private var mLoading: Boolean = false
    fun setLoading(loading: Boolean, then: () -> Unit = {}) {
        mLoading = loading
        if (loading) {
            action_btn.startAnimation()
        } else {
            action_btn.revertAnimation(then)
        }
    }

    val introMode = IntroAuthMode(this)
    val signupMode = SignUpAuthMode(this)
    val googleSignupMode = GoogleSignupMode(this)
    val facebookSignupMode = FacebookSignupMode(this)
    val loginMode = LogInAuthMode(this)
    val personalizeMode = PersonalizeAuthMode(this)
    var currentMode: AuthMode = introMode
    val onError = { message: String ->
        setLoading(false)
        showError(message)
    }
    val callbackManager by lazy { CallbackManager.Factory.create() }

    private lateinit var constraintSet: ConstraintSet
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CustomMethods.makeWindowSeamless(this)
        setContentView(R.layout.activity_auth)
        auth_root.background = ScrollingImageDrawable(this, R.drawable.many_memes,
                screenWidth,
                screenHeight)
        constraintSet = ConstraintSet()
        constraintSet.clone(auth_root)
        introMode.init()
        signupMode.init()
        loginMode.init()
        personalizeMode.init()

        dots_indicator.setViewPager(intro_pager)
        action_btn.setOnClickListener {
            execOnAction()
        }
        auth_question.movementMethod = LinkMovementMethod()

        signup_facebook.setOnClickListener {
            if (mLoading) {
                showError("Please wait a bit")
                return@setOnClickListener
            }
            currentMode.onFacebook()
        }
        signup_google.setOnClickListener {
            if (mLoading) {
                showError("Please wait a bit")
                return@setOnClickListener
            }
            currentMode.onGoogle()
        }
        val function: (TextView?, Int?, KeyEvent?) -> Boolean = { v, _, _ ->
            if (v == currentMode.getLastField()?.editText) {
                val mgr = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                mgr.hideSoftInputFromWindow(v?.windowToken, 0)
                execOnAction()
                true
            } else false
        }
        username_field.editText!!.setOnEditorActionListener(function)
        email_field.editText!!.setOnEditorActionListener(function)
        password_field.editText!!.setOnEditorActionListener(function)
        confirm_password_field.editText!!.setOnEditorActionListener(function)
        val modeInt = intent?.getIntExtra(STARTING_MODE_PARAM, MODE_INTRO) ?: MODE_INTRO
        setMode(getModeForInt(modeInt), withAnimation = false)
    }

    fun onGoto() {
        if (mLoading) {
            showError("Please wait a bit")
            return
        }
        currentMode.onGoto()
    }

    private fun execOnAction() {
        if (mLoading) {
            showError("Please wait a bit")
            return
        }
        if (currentMode.verifable()) {
            if (currentMode.onVerify()) currentMode.onAction()
        } else currentMode.onAction()
    }

    private fun getModeForInt(modeInt: Int) = when (modeInt) {
        MODE_LOGIN -> loginMode
        MODE_SIGNUP -> signupMode
        MODE_PERSONALIZE -> personalizeMode
        else -> introMode
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        currentMode.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        setLoading(false)
        if (!currentMode.onBackPressed()) {
            super.onBackPressed()
        }
    }

    fun showError(error: String) {
        val sb = Snackbar.make(auth_root, error, Snackbar.LENGTH_LONG)
        sb.show()
    }

    companion object {
        const val STARTING_MODE_PARAM = "frag"
        const val MODE_INTRO = 0
        const val MODE_LOGIN = 1
        const val MODE_SIGNUP = 2
        const val MODE_PERSONALIZE = 3
    }

    fun setMode(mode: AuthMode, bundle: Bundle? = null, withAnimation: Boolean = true) {
        mode.applyContraint(constraintSet)
        if (withAnimation) TransitionManager.beginDelayedTransition(auth_root)
        constraintSet.applyTo(auth_root)
        mode.updateElements()
        if (bundle != null) mode.withBundle(bundle)
        currentMode = mode
    }


}

