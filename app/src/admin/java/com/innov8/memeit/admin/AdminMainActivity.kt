package com.innov8.memeit.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.innov8.memeit.R
import kotlinx.android.synthetic.admin.activity_admin_main.*

class AdminMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        home_template_manager.setOnClickListener {
            startActivity(Intent(this, TemplateManagerActivity::class.java))
        }

    }
}
