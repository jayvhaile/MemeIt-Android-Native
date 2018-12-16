package com.innov8.memeit.Activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.imagepipeline.request.ImageRequest
import com.google.android.material.snackbar.Snackbar
import com.innov8.memeit.CustomClasses.SharedPrefs
import com.innov8.memeit.R
import com.innov8.memeit.Utils.*
import com.innov8.memeit.commons.toast
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItClient.context
import com.memeit.backend.MemeItUsers
import com.memeit.backend.models.UserReq
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.profile_settings.*
import java.io.File

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
        val sharedPrefs = SharedPrefs(this,null);
        val colorListener:View.OnClickListener = View.OnClickListener {
            var color = "";
            when(it){
                red->{
                    color = "red";
                }
                pink-> {
                    color = "pink";
                }
                blue-> {
                    color = "blue";
                }
                purple-> {
                    color = "purple";
                }
                black-> {
                    color = "black";
                }
                green-> {
                    color = "green";
                }
                orange-> {
                    color = "orange";
                }
                golden-> {
                    color = "golden";
                }
                else->{
                    color = "orange";
                };
            }
            MemeItUsers.updateMyUser(UserReq(coverImageUrl = color),{
                context.toast(color + " is selected.",Toast.LENGTH_SHORT)
            },{
                context.toast("An error has occurred.",Toast.LENGTH_SHORT)

            });
        }
        red.setOnClickListener(colorListener)
        green.setOnClickListener(colorListener)
        blue.setOnClickListener(colorListener)
        purple.setOnClickListener(colorListener)
        golden.setOnClickListener(colorListener)
        orange.setOnClickListener(colorListener)
        pink.setOnClickListener(colorListener)
        black.setOnClickListener(colorListener)
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
        var dialog = MaterialDialog.Builder(this).title("Updating profile")
                .progress(true,100)
                .show()
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
                    dialog.hide()
                    onBackPressed()
                }) { _ ->
                    settings_name.snack("Error updating profile", "Retry", { onSave() })
                    MaterialDialog.Builder(context).title("Error")
                            .content("An error has occured while updating your profile.")
                            .positiveText("Retry")
                            .negativeText("Cancel")
                            .onPositive { _, _ ->
                                onSave()
                            }

                    }
                }
        }
    }