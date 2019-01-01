package com.innov8.memeit.free

import android.content.Intent
import com.innov8.memeit.activities.FeedbackActivity
import com.innov8.memeit.activities.MainActivity


class MainActivity : MainActivity() {
    override fun onFeedbackMenu() {
        startActivity(Intent(this@MainActivity, FeedbackActivity::class.java))
    }

}