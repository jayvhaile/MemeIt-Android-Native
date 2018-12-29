package com.innov8.memeit.activities.authModes

import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.material.textfield.TextInputLayout
import com.innov8.memeit.activities.AuthActivity
import com.innov8.memeit.R
import com.innov8.memeit.utils.makeGone
import com.innov8.memeit.utils.makeVisible
import com.innov8.memeit.utils.validateLength
import kotlinx.android.synthetic.main.activity_auth.*

abstract class RequestUsernameMode(private val authActivity: AuthActivity) : AuthMode {
    override fun applyContraint(constraintSet: ConstraintSet) {
        AuthMode.makeIntroGone(constraintSet)
        AuthMode.makeGoogleFacebookGone(constraintSet)
        constraintSet.makeGone(
                R.id.intro_pager,
                R.id.setup_personalize,
                R.id.email_field,
                R.id.password_field,
                R.id.confirm_password_field,
                R.id.profile_pic,
                R.id.or_continue_with,
                R.id.auth_question,
                R.id.auth_question_action
        )
        constraintSet.makeVisible(
                R.id.app_title,
                R.id.username_field,
                R.id.action_holder
        )
    }

    override fun updateElements() {
        authActivity.action_btn.text = "Continue"
    }

    override fun getLastField(): TextInputLayout? = authActivity.username_field

    override fun verifable(): Boolean = true

    override fun onVerify(): Boolean {
        return booleanArrayOf(
                authActivity.username_field.validateLength(2, 32, "Username")
        ).all { it }
    }
}