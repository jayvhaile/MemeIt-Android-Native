package com.innov8.memeit.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.innov8.memeit.R
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

import android.content.Context.INPUT_METHOD_SERVICE
import com.innov8.memeit.Activities.TagsChooserActivity
import com.memeit.backend.MemeItUsers
import com.memeit.backend.dataclasses.User
import kotlinx.android.synthetic.main.fragment_setup_profile2.*

class SetupFragment : AuthFragment() {

    internal var name: String?=null
    private var image_url: Uri? = null
    internal var isFromGoogle: Boolean = false


    fun fromGoogle(name: String, pp: String?) {
        if (!TextUtils.isEmpty(pp)) isFromGoogle = true
        image_url = Uri.parse(pp)
        this.name = name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setup_profile2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profile_pic.setOnClickListener {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(authActivity)
        }
        actionButton = finish
        actionButton.setOnClickListener{finish()}

        name_setup.setOnEditorActionListener{ v, actionId, event ->
            val mgr = context!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            mgr.hideSoftInputFromWindow(v.windowToken, 0)
            finish()
            true
        }
        name_setup.setText(name)
        profile_pic.setImageURI(image_url)
    }

    private fun finish() {
        if (mLoading) return
        val name = name_setup.text.toString()
        if (TextUtils.isEmpty(name)) {
            authActivity.showError("Name cannot be empty!")
            return
        }
        setLoading(true)
        if (isFromGoogle) {
            uploadData(name, image_url?.toString())
        } else {
            if (image_url != null) {
                uploadImageThenData()
            } else {
                uploadData(name, null)
            }
        }
    }

    private fun uploadData(name: String, image_url: String?) {
        val user = User(name=name, imageUrl= image_url)
        MemeItUsers.setupMyUser(user, {
            val i = Intent(context, TagsChooserActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra("choose", true)
            startActivity(i)
        }, onError)
    }

    private fun uploadImageThenData() {
        MediaManager.get().upload(image_url).callback(object : UploadCallback {
            override fun onStart(s: String) {
                authActivity.showError("Uploading Image")
            }
            override fun onProgress(s: String, l: Long, l1: Long) {}

            override fun onSuccess(s: String, map: Map<*, *>) {
                val publicID = map["public_id"].toString()
                val name = name_setup.text.toString()
                uploadData(name, publicID)
            }

            override fun onError(s: String, errorInfo: ErrorInfo) {
                setLoading(false)
                authActivity.showError("Image Upload Error: ${errorInfo.description}")
            }
            override fun onReschedule(s: String, errorInfo: ErrorInfo) {

            }
        }).dispatch()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                image_url = result.uri
                isFromGoogle = false
                profile_pic.setImageURI(image_url)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
