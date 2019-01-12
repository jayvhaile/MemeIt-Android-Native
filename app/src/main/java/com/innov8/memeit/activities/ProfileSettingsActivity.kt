package com.innov8.memeit.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.innov8.memeit.commons.SuperActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.imagepipeline.request.ImageRequest
import com.innov8.memeit.R
import com.innov8.memeit.utils.*
import com.innov8.memeit.commons.toast
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItUsers
import com.memeit.backend.models.UserReq
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.profile_settings.*

class ProfileSettingsActivity : SuperActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_settings)
        initView()
    }

    private fun initView() {
        val user = MemeItClient.myUser!!
        settings_pp.loadImage(user.profilePic)
        settings_pp.setOnClickListener {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this)
        }

        settings_name.editText?.text?.append(user.name)
        settings_bio.editText?.text?.append(user.bio ?: "")
        setSupportActionBar(toolbar_profile_settings)
        supportActionBar?.title = "Edit Profile"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val imageUrl = result.uri
                if (result.uri == null) return
                settings_pp.setImageRequest(ImageRequest.fromUri(imageUrl))
                enqueueProfileImageUpload(result.uri.encodedPath!!)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                settings_pp.snack(result.error.message ?: "Something went wrong.")
            }
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
        val dialog = MaterialDialog.Builder(this).title("Updating profile")
                .progress(true, 100)
                .show()
        val muser = MemeItClient.myUser!!
        val newName = settings_name.text
        val newBio = settings_bio.text

        val user = UserReq()
        if (settings_name.validateLength(2, 32, "Name") &&
                settings_bio.validateLength(0, 70, "Bio")) {
            var changed = false
            if (newName != muser.name) {
                user.name = newName
                changed = true
            }
            if (newBio != muser.bio) {
                user.bio = newBio
                changed = true
            }
            if (changed)
                MemeItUsers.updateMyUser(user, {
                    dialog.hide()
                    toast("Profile Updated")
                    onBackPressed()
                }) {
                    dialog.hide()
                    settings_name.snack("Error updating profile", "Retry", { onSave() })
                }
        }
    }
}