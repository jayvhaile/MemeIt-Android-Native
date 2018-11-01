package com.innov8.memeit.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.ListPreference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.innov8.memeit.R
import com.innov8.memeit.commons.toast
import com.innov8.memeit.loadImage
import com.innov8.memeit.prefix
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItUsers
import com.memeit.backend.dataclasses.MUser
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {


    lateinit var user: MUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        load()
        initListenrs()
    }

    fun load() {
        user = MemeItClient.myUser!!
        namesettings.text = user.name
        usernamesettings.text = "@${user.username}"
        settings_pp.text = user.name.prefix()
        settings_pp.loadImage(user.profilePic)
    }

    fun onError(message:String,action:(()->Unit)?=null):(String)->Unit={
        toast("$message: $it")
        action?.invoke()
    }



    fun initListenrs() {
        namesettings.setOnClickListener { it ->
            MaterialDialog.Builder(this)
                    .input("Name", user.name, false) { _, input ->
                        MemeItUsers.updateName(input.toString(), {load()},onError("Error Updating Name"))
                    }.show()
        }
        usernamesettings.setOnClickListener { it ->
            MaterialDialog.Builder(this)
                    .input("Username", user.username, false) { dialog, input ->
                        //todo validate
                        MemeItUsers.updateUsername(input.toString(),  {load()},onError("Error Updating Username"))
                    }.show()
        }
        change_pp.setOnClickListener {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val image_url = result.uri
                if (result.uri == null) return
                val dialog = MaterialDialog.Builder(this)
                        .title("Uploading Image")
                        .progress(true, 100)
                        .build()
                dialog.show()
                MediaManager.get().upload(image_url).callback(object : UploadCallback {
                    override fun onStart(s: String) {

                    }

                    override fun onProgress(s: String, l: Long, l1: Long) {}

                    override fun onSuccess(s: String, map: Map<*, *>) {
                        val public_id = map["public_id"].toString()
                        MemeItUsers.updateProfilePic(public_id, {
                            this@SettingsActivity.toast("Profile Picture Updated")
                            dialog.dismiss()
                        }, onError("Error updating Profile pic") {dialog.dismiss()})
                    }

                    override fun onError(s: String, errorInfo: ErrorInfo) {
                        this@SettingsActivity.toast("failed image ${errorInfo.description}")
                        dialog.dismiss()
                    }

                    override fun onReschedule(s: String, errorInfo: ErrorInfo) {

                    }
                }).dispatch()


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }


    companion object {
        const val PREF_KEY_IMAGE_QUALITY = "pref_image_quality"
        val quality = listOf(10, 25, 50, 75, 100)
        val factor = listOf(.4f, .6f, .8f, .9f, 1f)

        fun getImageQualityLevel(context: Context): Int {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            val values = context.resources.getStringArray(R.array.pref_image_quality)
            val value = pref.getString(PREF_KEY_IMAGE_QUALITY, "")
            return values.indexOf(value)
        }

    }
}

class PrefFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)

        for (i in 0 until preferenceScreen.preferenceCount) {
            val p = preferenceScreen.getPreference(i)
            when (p) {
                is ListPreference -> p.summary = p.value
                is EditTextPreference -> p.summary = p.text
            }
        }

    }
}


