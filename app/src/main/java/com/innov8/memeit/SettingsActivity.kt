package com.innov8.memeit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.innov8.memegenerator.utils.log
import com.innov8.memegenerator.utils.toast
import com.innov8.memeit.CustomClasses.ImageUtils
import com.memeit.backend.MemeItUsers
import com.memeit.backend.dataclasses.MyUser
import com.memeit.backend.utilis.Listener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_settings2.*
import okhttp3.ResponseBody

val PREF_NAME = "pref_name"
val PREF_USERNAME = "pref_username"

class SettingsActivity : AppCompatActivity() {
    lateinit var user: MyUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings2)
        load()
        initListenrs()
    }

    fun load() {
        user = MemeItUsers.getInstance().getMyUser(this)
        namesettings.text=user.name
        usernamesettings.text="@${user.username}"
        ImageUtils.loadImageFromCloudinaryTo(settings_pp, user.imageUrl)
    }
    fun initListenrs(){
        namesettings.setOnClickListener { it ->
            MaterialDialog.Builder(this)
                    .input("Name",user.name,false){ dialog, input ->
                        MemeItUsers.getInstance().updateName(this, input.toString(), Listener<ResponseBody>(onsuccess = {
                            load()
                        },onfailure = {
                            this.toast("error name ${it.message}")
                        }))
                    }.show()
        }
        usernamesettings.setOnClickListener { it ->
            MaterialDialog.Builder(this)
                    .input("Username",user.username,false){ dialog, input ->
                        //todo validate
                        MemeItUsers.getInstance().updateUsername(this, input.toString(), Listener<ResponseBody>(onSuccess = {
                            load()
                        }))
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
                log("yihu",image_url.toString())
                if (result.uri==null)return
                val dialog=MaterialDialog.Builder(this)
                        .title("Uploading Image")
                        .progress(true,100)
                        .build()
                dialog.show()
                MediaManager.get().upload(image_url).callback(object : UploadCallback {
                    override fun onStart(s: String) {

                    }

                    override fun onProgress(s: String, l: Long, l1: Long) {}

                    override fun onSuccess(s: String, map: Map<*, *>) {
                        val public_id = map["public_id"].toString()
                        MemeItUsers.getInstance().updateProfilePic(this@SettingsActivity,public_id,Listener<ResponseBody>({
                            this@SettingsActivity.toast("Uploaded")
                            dialog.dismiss()
                        },{
                            this@SettingsActivity.toast("failed ${it.message}")
                            dialog.dismiss()
                        }))
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

}

class Prefheaders : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_headers)
    }
}

class AccountPref : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_account)
    }

}

class NotificationPref : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_notifications)
    }
}

class DataAndStoragePref : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_data_and_storage)
    }
}

class PrivacyAndSecurityPref : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_privacy_and_security)
    }

}


