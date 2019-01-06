package com.innov8.memeit.activities.authModes

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import com.innov8.memegenerator.utils.ViewAdapter
import com.innov8.memeit.R
import com.innov8.memeit.activities.AuthActivity
import com.innov8.memeit.adapters.IntroTransformer
import com.innov8.memeit.utils.makeGone
import com.innov8.memeit.utils.makeVisible
import com.innov8.memeit.utils.toUnderlinedLinkSpan
import kotlinx.android.synthetic.main.activity_auth.*

class IntroAuthMode(private val authActivity: AuthActivity) : AuthMode {

    private val pager by lazy {
        object : ViewAdapter(authActivity) {
            val titles = authActivity.resources.getStringArray(R.array.intro_slide_title)
            val descriptions = authActivity.resources.getStringArray(R.array.intro_slide_description)
            val drawables = authActivity.resources.obtainTypedArray(R.array.intro_drawables)
            private val count = intArrayOf(titles.size, descriptions.size, drawables.length()).min()!!

            val views = List<View>(count) {
                val view = LayoutInflater.from(authActivity).inflate(R.layout.pager_item_intro, authActivity.intro_pager, false)
                val iv = view.findViewById<ImageView>(R.id.intro_image)
                val titleV = view.findViewById<TextView>(R.id.intro_login)
                val descV = view.findViewById<TextView>(R.id.intro_description)

                titleV.text = titles[it]
                descV.text = descriptions[it]
                iv.setImageResource(drawables.getResourceId(it, -1))

                view
            }

            override fun getItem(position: Int): View {
                return views[position]
            }

            override fun getCount(): Int = count

        }
    }

    override fun applyContraint(constraintSet: ConstraintSet) {
        AuthMode.makeIntroVisible(constraintSet)
        AuthMode.makeGoogleFacebookGone(constraintSet)
        constraintSet.makeGone(
                R.id.app_title,
                R.id.username_field,
                R.id.email_field,
                R.id.password_field,
                R.id.confirm_password_field,
                R.id.or_continue_with,
                R.id.setup_personalize,
                R.id.profile_pic
        )
        constraintSet.makeVisible(
                R.id.intro_pager,
                R.id.auth_question,
                R.id.action_holder
        )
    }


    override fun verifable(): Boolean = false

    private val transformer by lazy { IntroTransformer() }
    override fun updateElements() {
        authActivity.intro_pager.adapter = pager
        authActivity.intro_pager.setPageTransformer(false, transformer)
        authActivity.action_btn.text = "Create Account"
        authActivity.auth_question.text = "Already have an account? Login".toUnderlinedLinkSpan("Login") {
            authActivity.onGoto()
        }
    }

    override fun onAction() {
        authActivity.setMode(authActivity.signupMode)
    }

    override fun onVerify(): Boolean = false

    override fun onGoto() {
        authActivity.setMode(authActivity.loginMode)
    }
}