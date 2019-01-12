package com.innov8.memeit.admin

import android.app.Activity
import android.content.Intent
import com.innov8.memeit.commons.SuperActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.innov8.memeit.R
import kotlinx.android.synthetic.admin.activity_admin_main.*

class AdminMainActivity : SuperActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        home_template_manager.setOnClickListener {
            startActivity(Intent(this, TemplateManagerActivity::class.java))
        }

    }



}
