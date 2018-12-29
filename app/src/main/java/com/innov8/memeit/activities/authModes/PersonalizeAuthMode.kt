package com.innov8.memeit.activities.authModes

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintSet
import com.facebook.drawee.generic.RoundingParams
import com.facebook.imagepipeline.request.ImageRequest
import com.google.android.material.textfield.TextInputLayout
import com.innov8.memeit.activities.AuthActivity
import com.innov8.memeit.activities.TagsChooserActivity
import com.innov8.memeit.R
import com.innov8.memeit.utils.*
import com.memeit.backend.MemeItUsers
import com.memeit.backend.models.UserReq
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_auth.*

class PersonalizeAuthMode(private val authActivity: AuthActivity) : AuthMode {
    override fun init() {
        authActivity.profile_pic.setOnClickListener {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(authActivity)
        }
    }


    var name: String? = null
    var profileImageUrl: Uri? = null
    var isImageFrom3rdParty: Boolean = false

    override fun applyContraint(constraintSet: ConstraintSet) {
        AuthMode.makeIntroGone(constraintSet)
        AuthMode.makeGoogleFacebookGone(constraintSet)
        constraintSet.makeGone(
                R.id.intro_pager,
                R.id.email_field,
                R.id.confirm_password_field,
                R.id.password_field,
                R.id.or_continue_with,
                R.id.auth_question,
                R.id.auth_question_action
        )
        constraintSet.makeVisible(
                R.id.app_title,
                R.id.setup_personalize,
                R.id.profile_pic,
                R.id.username_field,
                R.id.action_holder
        )
    }

    override fun getLastField(): TextInputLayout? = authActivity.username_field


    override fun verifable(): Boolean = true
    override fun updateElements() {
        authActivity.username_field.editText!!.hint = "Name"
        authActivity.username_field.clear()
        authActivity.action_btn.text = "Continue"
    }

    override fun withBundle(bundle: Bundle) {
        super.withBundle(bundle)
        name = bundle.getString("name")
        bundle.getString("image")?.let {
            profileImageUrl = Uri.parse(it)
            if (profileImageUrl != null) isImageFrom3rdParty = true
        }
        authActivity.username_field.editText!!.text.append(name)
        authActivity.profile_pic.setImageRequest(ImageRequest.fromUri(profileImageUrl))
    }

    override fun onAction() {
        val name = authActivity.username_field.text
        authActivity.setLoading(true)
        if (isImageFrom3rdParty) {
            uploadData(UserReq(name = name, imageUrl = profileImageUrl!!.toString()))
        } else {
            profileImageUrl?.let {
                enqueueProfileImageUpload(it.encodedPath!!)
            }
            uploadData(UserReq(name = name))
        }
    }

    override fun onVerify(): Boolean {
        return booleanArrayOf(
                authActivity.username_field.validateLength(2, 32, "Name")
        ).all { it }
    }

    private fun uploadData(user: UserReq) {
        MemeItUsers.updateMyUser(user, {
            val i = Intent(authActivity, TagsChooserActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra("choose", true)
            authActivity.startActivity(i)
        }, authActivity.onError)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                profileImageUrl = result.uri
                isImageFrom3rdParty = false
                authActivity.profile_pic.setImageURI(profileImageUrl)
                authActivity.profile_pic.hierarchy.roundingParams = RoundingParams().apply {
                    borderColor = Color.WHITE
                    borderWidth = 2f.dp
                    roundAsCircle = true
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                authActivity.showError(result.error.message ?: "Cant read Image File")
            }
        }
    }

}