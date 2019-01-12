package com.innov8.memeit.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import com.innov8.memeit.commons.SuperActivity
import com.innov8.memeit.R
import com.innov8.memeit.fragments.ProfileFragment
import com.memeit.backend.MemeItClient
import com.memeit.backend.models.User

class ProfileActivity : SuperActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.RED
        }
        setContentView(R.layout.activity_profile)
        intent?.data?.lastPathSegment?.let {
            if (MemeItClient.myUser != null) {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.profile_frag_holder, ProfileFragment.byUsername(it))
                        .commit()
            } else {
                startActivity(Intent(this, AuthActivity::class.java))
            }
        } ?: let {
            val uid = intent.getStringExtra(PARAM_USER_ID)
            val user = intent.getParcelableExtra<User>(PARAM_USER)
            val username = intent.getStringExtra(PARAM_USERNAME)

            val pf = when {
                user != null -> ProfileFragment.byUser(user)
                username != null -> ProfileFragment.byUsername(username)
                else -> ProfileFragment.byID(uid)
            }
            supportFragmentManager.beginTransaction()
                    .replace(R.id.profile_frag_holder, pf)
                    .commit()
        }


    }

    companion object {
        const val PARAM_USER_ID = "uid"
        const val PARAM_USER = "user"
        const val PARAM_USERNAME = "username"

        fun startWithUserId(context: Context, uid: String) {
            context.startActivity(Intent(context, ProfileActivity::class.java).apply {
                putExtra(PARAM_USER_ID, uid)
            })
        }

        fun startWithUsername(context: Context, username: String) {
            context.startActivity(Intent(context, ProfileActivity::class.java).apply {
                putExtra(PARAM_USERNAME, username)
            })
        }

        fun startWithUser(context: Context, user: User) {
            context.startActivity(Intent(context, ProfileActivity::class.java).apply {
                putExtra(PARAM_USER, user)
            })
        }
    }
}
