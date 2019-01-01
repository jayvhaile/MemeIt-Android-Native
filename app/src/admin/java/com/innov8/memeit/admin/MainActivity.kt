package com.innov8.memeit.admin

import android.content.Intent
import com.innov8.memeit.activities.MainActivity


class MainActivity : MainActivity() {
    override fun onFeedbackMenu() {
        startActivity(Intent(this@MainActivity, AdminMainActivity::class.java))
    }

}