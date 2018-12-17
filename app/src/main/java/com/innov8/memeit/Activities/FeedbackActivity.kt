package com.innov8.memeit.Activities

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.innov8.memeit.R
import com.innov8.memeit.Utils.snack
import com.memeit.backend.MemeItClient.context
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.models.Feedback
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        feedback_message.text?.append(loadSavedFeedback(this))
        feedback_submit.setOnClickListener {
            sendFeedback()
        }
        feedback_message.setOnEditorActionListener { _, _, _ ->
            val mgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
            mgr.hideSoftInputFromWindow(feedback_message.windowToken, 0)
            sendFeedback()
            true
        }
    }

    private fun sendFeedback() {
        val message = feedback_message.text.toString()
        if (message.isEmpty()) {
            feedback_submit.snack("Please Insert feedback first")
        } else {
            feedback_submit.startAnimation()
            MemeItUsers.postFeedback(Feedback(message)).call({
                feedback_message.text?.clear()
                clearFeedback(this)
                feedback_submit.revertAnimation()
                feedback_submit.snack("Thank you for your feedback!")
            }, {
                feedback_submit.revertAnimation()
                feedback_submit.snack("Sending feedback failed: $it")
            })
        }
    }

    override fun onStop() {
        val feedback = feedback_message.text?.toString()
        if (feedback.isNullOrBlank()) {
            clearFeedback(this)
        } else {
            saveFeedback(this, feedback)

        }
        super.onStop()

    }

    companion object {
        private const val KEY_FEEDBACK = "feedback"
        private fun saveFeedback(context: Context, feedback: String) {
            PreferenceManager.getDefaultSharedPreferences(context).apply {
                edit {
                    putString(KEY_FEEDBACK, feedback)
                }
            }
        }

        private fun loadSavedFeedback(context: Context): String {
            return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_FEEDBACK, "")!!
        }

        fun clearFeedback(context: Context) {
            PreferenceManager.getDefaultSharedPreferences(context).apply {
                edit {
                    remove(KEY_FEEDBACK)
                }
            }
        }
    }
}
