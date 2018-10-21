package com.innov8.memeit.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.innov8.memeit.R
import com.innov8.memeit.snack
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.dataclasses.Feedback
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        feedback_submit.setOnClickListener { view ->
            val message = feedback_message.text.toString()
            if (message.isEmpty()) {
                view.snack("Please Insert feedback first")
            } else {
                feedback_submit.startAnimation()

                MemeItUsers.postFeedback(Feedback(message)).call({
                    feedback_submit.revertAnimation()
                    view.snack("Thank you for your feedback!")

                }, {
                    feedback_submit.revertAnimation()
                    view.snack("Sending feedback failed: $it")
                })
            }
        }
    }
}
