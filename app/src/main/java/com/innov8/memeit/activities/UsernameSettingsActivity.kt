package com.innov8.memeit.activities

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.innov8.memeit.R
import com.innov8.memeit.utils.snack
import com.innov8.memeit.utils.text
import com.innov8.memeit.commons.addOnTextChanged
import com.innov8.memeit.commons.toast
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import kotlinx.android.synthetic.main.username_settings.*

class UsernameSettingsActivity : AppCompatActivity() {
    private val usernameMap = mutableMapOf<String, Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.username_settings)
        setSupportActionBar(toolbar_username_settings)
        supportActionBar?.title = "Change Username"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val user = MemeItClient.myUser!!
        settings_username.editText?.append(user.username)
        username_link.text ="https://memeit.innov8.io/un/${user.username}"
        settings_username.editText?.addOnTextChanged {
            username_link.text ="https://memeitapp.com/link/$it"
            if (it == user.username) status = STATUS_ORIGINAL
            if (isValid(it))
                checkOnLocal()
            else {
                status = STATUS_INVALID
            }
        }

    }

    companion object {
        const val STATUS_ORIGINAL = 0
        const val STATUS_INVALID = 1
        const val STATUS_TAKEN = 2
        const val STATUS_AVAILABLE = 3
        const val STATUS_CHECKING = 4
    }

    var status = STATUS_ORIGINAL
        set(value) {
            field = value
            when (field) {
                STATUS_ORIGINAL -> username_status.visibility = View.GONE
                STATUS_AVAILABLE -> {
                    username_status.visibility = View.VISIBLE
                    username_status.text = "Username Available"
                    username_status.setTextColor(Color.parseColor("#00aa00"))
                }
                STATUS_INVALID -> {
                    username_status.visibility = View.VISIBLE
                    username_status.text = "Username Invalid"
                    username_status.setTextColor(Color.RED)
                }

                STATUS_TAKEN -> {
                    username_status.visibility = View.VISIBLE
                    username_status.text = "Username Taken"
                    username_status.setTextColor(Color.RED)
                }
                STATUS_CHECKING -> {
                    username_status.visibility = View.VISIBLE
                    username_status.text = "Checking username..."
                    username_status.setTextColor(Color.parseColor("#aaaaaa"))
                }
            }
        }

    private fun isValid(username: String): Boolean {
        return username.length > 1 /*&&
                username.matches(Regex(""))*/
    }

    private fun checkOnLocal() {
        val name = settings_username.text
        val available = usernameMap[name]
        if (available != null) {
            if (available) {
                status = STATUS_AVAILABLE
            } else {
                status = STATUS_TAKEN
            }
        } else {
            status = STATUS_CHECKING
            checkOnServer(name)
        }
    }

    private fun checkOnServer(username: String) {
        MemeItUsers.isUsernameAvailable(username).call {
            usernameMap[it.username] = it.available
            checkOnLocal()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings_save) {
            onSave()
            return true
        }
        return false
    }

    private fun onSave() {
        when (status) {
            STATUS_ORIGINAL -> finish()
            STATUS_INVALID -> toast("Please choose a valid username first")
            STATUS_TAKEN -> toast("Username is Taken,Please choose another username")
            STATUS_CHECKING -> toast("Please wait while checking username")
            STATUS_AVAILABLE -> {
                MemeItUsers.updateUsername(settings_username.text, {
                    toast("Username updated")
                    finish()
                }) {
                    settings_username.snack("Couldn't update username", "Retry", { onSave() })
                }
            }
        }
    }
}
