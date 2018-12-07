package com.innov8.memeit.Activities.authModes

import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.material.textfield.TextInputLayout
import com.innov8.memeit.R
import com.innov8.memeit.Utils.dp

interface AuthMode {
    companion object {
        fun makeIntroVisible(constraintSet: ConstraintSet) {
            constraintSet.connect(R.id.intro_pager, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            constraintSet.connect(R.id.dots_indicator, ConstraintSet.BOTTOM, R.id.action_top_guideline, ConstraintSet.TOP,8.dp)
        }

        fun makeIntroGone(constraintSet: ConstraintSet) {
            constraintSet.connect(R.id.dots_indicator, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP,8.dp)
            constraintSet.clear(R.id.intro_pager, ConstraintSet.TOP)
        }

        fun makeTitleVisible(constraintSet: ConstraintSet) {
            constraintSet.connect(R.id.app_title, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 16.dp)
            constraintSet.clear(R.id.app_title, ConstraintSet.BOTTOM)
        }

        fun makeTitleGone(constraintSet: ConstraintSet) {
            constraintSet.connect(R.id.app_title, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            constraintSet.clear(R.id.app_title, ConstraintSet.TOP)
        }

        fun makeGoogleFacebookVisible(constraintSet: ConstraintSet) {
            constraintSet.clear(R.id.signup_google, ConstraintSet.RIGHT)
            constraintSet.clear(R.id.signup_facebook, ConstraintSet.LEFT)
            constraintSet.createHorizontalChain(R.id.left_guideline, ConstraintSet.RIGHT,
                    R.id.right_guideline, ConstraintSet.LEFT,
                    intArrayOf(R.id.signup_google, R.id.signup_facebook),
                    floatArrayOf(1f, 1f),
                    ConstraintSet.CHAIN_PACKED)
            constraintSet.setMargin(R.id.signup_google, ConstraintSet.RIGHT, 8.dp)
            constraintSet.setMargin(R.id.signup_facebook, ConstraintSet.LEFT, 8.dp)
            constraintSet.connect(R.id.auth_question, ConstraintSet.TOP, R.id.signup_google, ConstraintSet.BOTTOM, 16.dp)
        }

        fun makeGoogleFacebookGone(constraintSet: ConstraintSet) {
            constraintSet.removeFromHorizontalChain(R.id.signup_google)
            constraintSet.removeFromHorizontalChain(R.id.signup_facebook)
            constraintSet.connect(R.id.signup_google, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
            constraintSet.connect(R.id.signup_facebook, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
            constraintSet.connect(R.id.auth_question, ConstraintSet.TOP, R.id.action_holder, ConstraintSet.BOTTOM, 16.dp)

        }


    }

    fun init() {}
    fun updateElements()
    fun withBundle(bundle: Bundle) {}
    fun applyContraint(constraintSet: ConstraintSet)
    fun onAction()
    fun getLastField(): TextInputLayout? = null
    fun onVerify(): Boolean = false
    fun onGoto() {}
    fun verifable(): Boolean
    fun onFacebook() {}
    fun onGoogle() {}
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {}
    fun onBackPressed() = false
}