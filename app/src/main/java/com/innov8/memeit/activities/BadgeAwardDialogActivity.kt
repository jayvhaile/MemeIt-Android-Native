package com.innov8.memeit.activities

import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.core.text.toSpannable
import com.innov8.memeit.R
import com.innov8.memeit.utils.color
import com.memeit.backend.models.Badge

class BadgeAwardDialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = layoutInflater.inflate(R.layout.dialog_badge_award, null, false)

        intent.getParcelableExtra<Badge>("badge")?.let { badge ->
            val badgeImage = view.findViewById<ImageView>(R.id.badge_image)
            val badgeText = view.findViewById<TextView>(R.id.badge_text)
            val badgeDescription = view.findViewById<TextView>(R.id.badge_desc)
            val badgeDismiss = view.findViewById<TextView>(R.id.badge_dismiss)

            badgeImage.setImageResource(badge.getDrawableId(this))

            badgeText.text = "Congratulations! you have achieved the ${badge.label} badge.".toSpannable().apply {
                val i = this.indexOf(badge.label)
                this[i..i + badge.label.length] = ForegroundColorSpan(R.color.colorAccent.color(this@BadgeAwardDialogActivity))
            }
            badgeDescription.text = badge.description


            val dialog = AlertDialog.Builder(this)
                    .setView(view)
                    .show()

            badgeDismiss.setOnClickListener {
                dialog.dismiss()
                finish()
            }
        } ?: finish()


    }
}
