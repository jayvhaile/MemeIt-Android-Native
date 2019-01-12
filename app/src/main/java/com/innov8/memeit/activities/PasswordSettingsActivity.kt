package com.innov8.memeit.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.innov8.memeit.commons.SuperActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.innov8.memeit.R
import com.innov8.memeit.utils.snack
import com.innov8.memeit.utils.text
import com.innov8.memeit.utils.validateLength
import com.innov8.memeit.utils.validateMatch
import com.innov8.memeit.commons.toast
import com.memeit.backend.MemeItClient
import com.memeit.backend.models.ChangePasswordRequest
import kotlinx.android.synthetic.main.password_settings.*

class PasswordSettingsActivity : SuperActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.password_settings)
        setSupportActionBar(toolbar_password_settings)
        supportActionBar?.title = "Change Password"
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
        val oldPassword = settings_password_current.text
        val newPassword = settings_password_new.text
        val validate = booleanArrayOf(settings_password_current.validateLength(8, 32, "Password"),
                (settings_password_new to settings_password_confirm).validateMatch("Password")).all { it }

        if (validate) {
            val dialog = MaterialDialog.Builder(this)
                    .title("Changing password...")
                    .progress(true, 0)
                    .show()
            MemeItClient.Auth.changePassword(ChangePasswordRequest(oldPassword, newPassword), {
                dialog.dismiss()
                toast("Password Changed")
                finish()
            }) {
                dialog.dismiss()
                settings_password_current.snack(it, duration = Snackbar.LENGTH_LONG)
            }
        }
    }

}