package com.innov8.memeit.Activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.android.material.snackbar.Snackbar
import com.innov8.memeit.R
import com.innov8.memeit.commons.toast
import com.innov8.memeit.loadImage
import com.innov8.memeit.snack
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItUsers
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.profile_settings.*

class ProfileSettingsActivity : AppCompatActivity() {
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
        settings_bio.editText?.text?.append(user.bio?:"")
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
                uploadImage(imageUrl)
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

    fun onSave() {
        settings_pp.snack("Updating Profile", duration = Snackbar.LENGTH_LONG)
        val user = MemeItClient.myUser!!
        val newName = settings_name.text
        val newBio = settings_bio.text
        if (newName != user.name || newBio != user.bio) {
            if (newName != user.name && settings_name.validateLength(2, 32, "Name"))
                MemeItUsers.updateName(newName, {
                    if (newBio != user.bio) {
                        MemeItUsers.updateBio(newBio, {
                            toast("Profile Updated")
                            finish()
                        }) {
                            settings_pp.snack(it)
                        }
                    } else {
                        toast("Profile Updated")
                        finish()
                    }
                }) {
                    settings_pp.snack(it)
                }

        } else {
            finish()
        }
    }

    fun uploadImage(imageUrl: Uri) {
        settings_pp.snack("Uploading Image", duration = Snackbar.LENGTH_LONG)
        MediaManager.get().upload(imageUrl).callback(object : UploadCallback {
            override fun onStart(s: String) {

            }

            override fun onProgress(s: String, l: Long, l1: Long) {}

            override fun onSuccess(s: String, map: Map<*, *>) {
                postImageUrl(map["public_id"].toString())
            }

            override fun onError(s: String, errorInfo: ErrorInfo) {
                settings_pp.snack("Failed to upload Image", "Retry", { uploadImage(imageUrl) })
            }

            override fun onReschedule(s: String, errorInfo: ErrorInfo) {

            }
        }).dispatch()
    }

    fun postImageUrl(id: String) {
        MemeItUsers.updateProfilePic(id, {
            settings_pp.snack("Profile Picture updated!")
        }) { err ->
            settings_pp.snack("Failed to update profile picture\n$err", "Retry", { postImageUrl(id) })
        }
    }

}