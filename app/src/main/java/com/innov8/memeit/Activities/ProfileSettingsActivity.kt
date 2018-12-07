package com.innov8.memeit.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.facebook.imagepipeline.request.ImageRequest
import com.google.android.material.snackbar.Snackbar
import com.innov8.memegenerator.MemePosterActivity.Companion.bitmap
import com.innov8.memegenerator.utils.toByteArray
import com.innov8.memeit.R
import com.innov8.memeit.Utils.loadImage
import com.innov8.memeit.Utils.snack
import com.innov8.memeit.Utils.text
import com.innov8.memeit.Utils.validateLength
import com.innov8.memeit.commons.toast
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItUsers
import com.memeit.backend.models.UserReq
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.profile_settings.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream

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
                uploadImage(result.uri)
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
        settings_pp.snack("Updating Profile", duration = Snackbar.LENGTH_LONG)
        val muser = MemeItClient.myUser!!
        val newName = settings_name.text
        val newBio = settings_bio.text

        val user = UserReq()
        if (settings_name.validateLength(2, 32, "Name")) {
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
                    toast("Profile Updated")
                }) { _ ->
                    settings_name.snack("Error updating profile", "Retry", { onSave() })
                }
        }

    }

    private fun uploadImage(imageUrl: Uri) {
        settings_pp.snack("Uploading Image", duration = Snackbar.LENGTH_INDEFINITE)
        val file = File(imageUrl.path)
        MemeItClient.uploadFile(file, true, {
            postImageUrl(it)
        }
        ) {
            settings_pp.snack("Failed to upload Image", "Retry", { uploadImage(imageUrl) })
        }
    }

    fun postImageUrl(id: String) {
        MemeItUsers.updateMyUser(UserReq(imageUrl = id), {
            settings_pp.snack("Profile Picture updated!")
        }) { err ->
            settings_pp.snack("Failed to update profile picture\n$err", "Retry", { postImageUrl(id) },Snackbar.LENGTH_LONG)
        }
    }

}