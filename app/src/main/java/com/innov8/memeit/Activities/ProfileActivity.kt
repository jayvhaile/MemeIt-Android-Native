package com.innov8.memeit.Activities

import android.os.Bundle

import com.innov8.memeit.Fragments.ProfileFragment
import com.innov8.memeit.R
import com.memeit.backend.dataclasses.User

import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val uid = intent.getStringExtra("uid")
        val user = intent.getParcelableExtra<User>("user")

        val pf: ProfileFragment
        pf = if (user != null) {
            ProfileFragment.newInstance(user)
        } else {
            ProfileFragment.newInstance(uid)
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.profile_frag_holder, pf)
                .commit()
    }
}
