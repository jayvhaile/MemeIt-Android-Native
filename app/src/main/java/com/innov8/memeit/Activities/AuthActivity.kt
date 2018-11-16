package com.innov8.memeit.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.facebook.CallbackManager
import com.facebook.imagepipeline.request.ImageRequest
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.iid.FirebaseInstanceId
import com.innov8.memegenerator.utils.ViewAdapter
import com.innov8.memeit.*
import com.innov8.memeit.Adapters.IntroTransformer
import com.innov8.memeit.CustomClasses.CustomMethods
import com.innov8.memeit.CustomClasses.ScrollingImageDrawable
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItUsers
import com.memeit.backend.dataclasses.FacebookInfo
import com.memeit.backend.dataclasses.GoogleInfo
import com.memeit.backend.dataclasses.User
import com.memeit.backend.dataclasses.UsernameAuthRequest
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    private var mLoading: Boolean = false
    private fun setLoading(loading: Boolean, then: () -> Unit = {}) {
        mLoading = loading
        if (loading) {
            action_btn.startAnimation()
        } else {
            action_btn.revertAnimation(then)
        }
    }

    private val introMode = IntroAuthMode()
    private val signupMode = SignUpAuthMode()
    private val googleSignupMode = GoogleSignupMode()
    private val facebookSignupMode = FacebookSignupMode()
    private val loginMode = LogInAuthMode()
    private val personalizeMode = PersonalizeAuthMode()
    private var currentMode: AuthMode = introMode
    private val onError = { message: String ->
        setLoading(false)
        showError(message)
    }
    private val callbackManager by lazy { CallbackManager.Factory.create() }

    private lateinit var constraintSet: ConstraintSet
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CustomMethods.makeWindowSeamless(this)
        setContentView(R.layout.activity_auth)
        auth_root.background = ScrollingImageDrawable(this, R.drawable.many_pics_optimized,
                screenWidth,
                screenHeight)
        constraintSet = ConstraintSet()
        constraintSet.clone(auth_root)
        introMode.init()
        signupMode.init()
        loginMode.init()
        personalizeMode.init()

        dots_indicator.setViewPager(intro_pager)
        action_btn.setOnClickListener {
            execOnAction()
        }
        auth_question_action.setOnClickListener {
            if (mLoading) {
                showError("Please wait a While")
                return@setOnClickListener
            }
            currentMode.onGoto()
        }


        signup_facebook.setOnClickListener {
            if (mLoading) {
                showError("Please wait a While")
                return@setOnClickListener
            }
            currentMode.onFacebook()
        }
        signup_google.setOnClickListener {
            if (mLoading) {
                showError("Please wait a While")
                return@setOnClickListener
            }
            currentMode.onGoogle()
        }
        val function: (TextView?, Int?, KeyEvent?) -> Boolean = { v, _, _ ->
            if (v == currentMode.getLastField()?.editText) {
                val mgr = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                mgr.hideSoftInputFromWindow(v?.windowToken, 0)
                execOnAction()
                true
            } else false
        }
        username_field.editText!!.setOnEditorActionListener(function)
        email_field.editText!!.setOnEditorActionListener(function)
        password_field.editText!!.setOnEditorActionListener(function)
        confirm_password_field.editText!!.setOnEditorActionListener(function)
        val modeInt = intent?.getIntExtra(STARTING_MODE_PARAM, MODE_INTRO) ?: MODE_INTRO
        setMode(getModeForInt(modeInt), withAnimation = false)
    }

    private fun execOnAction() {
        if (mLoading) {
            showError("Please wait a While")
            return
        }
        if (currentMode.verifable()) {
            if (currentMode.onVerify()) currentMode.onAction()
        } else currentMode.onAction()
    }

    private fun getModeForInt(modeInt: Int) = when (modeInt) {
        MODE_LOGIN -> loginMode
        MODE_SIGNUP -> signupMode
        MODE_PERSONALIZE -> personalizeMode
        else -> introMode
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        currentMode.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (!currentMode.onBackPressed()) {
            setLoading(false)
            super.onBackPressed()
        }
    }

    fun showError(error: String) {
        val sb = Snackbar.make(auth_root, error, Snackbar.LENGTH_LONG)
        sb.show()
    }

    companion object {
        const val STARTING_MODE_PARAM = "frag"
        const val MODE_INTRO = 0
        const val MODE_LOGIN = 1
        const val MODE_SIGNUP = 2
        const val MODE_PERSONALIZE = 3
    }

    fun setMode(mode: AuthMode, bundle: Bundle? = null, withAnimation: Boolean = true) {
        mode.applyContraint(constraintSet)
        if (withAnimation) TransitionManager.beginDelayedTransition(auth_root)
        constraintSet.applyTo(auth_root)
        mode.updateElements()
        if (bundle != null) mode.withBundle(bundle)
        currentMode = mode
    }


    inner class IntroAuthMode : AuthMode {

        private val pager by lazy {
            object : ViewAdapter(this@AuthActivity) {
                val titles = resources.getStringArray(R.array.intro_slide_title)
                val descriptions = resources.getStringArray(R.array.intro_slide_description)
                val drawables = resources.obtainTypedArray(R.array.intro_drawables)
                private val count = intArrayOf(titles.size, descriptions.size, drawables.length()).min()!!

                val views = List<View>(count) {
                    val view = LayoutInflater.from(this@AuthActivity).inflate(R.layout.pager_item_intro, intro_pager, false)
                    val iv = view.findViewById<ImageView>(R.id.intro_image)
                    val titleV = view.findViewById<TextView>(R.id.intro_login)
                    val descV = view.findViewById<TextView>(R.id.intro_description)

                    titleV.text = titles[it]
                    descV.text = descriptions[it]
                    iv.setImageResource(drawables.getResourceId(it, -1))

                    view
                }

                override fun getItem(position: Int): View {
                    return views[position]
                }

                override fun getCount(): Int = count

            }
        }

        override fun applyContraint(constraintSet: ConstraintSet) {
            AuthMode.makeIntroVisible(constraintSet)
            AuthMode.makeGoogleFacebookGone(constraintSet)
            constraintSet.makeGone(
                    R.id.app_title,
                    R.id.username_field,
                    R.id.email_field,
                    R.id.password_field,
                    R.id.confirm_password_field,
                    R.id.or_continue_with,
                    R.id.setup_personalize,
                    R.id.profile_pic
            )
            constraintSet.makeVisible(
                    R.id.intro_pager,
                    R.id.auth_question,
                    R.id.auth_question_action,
                    R.id.action_holder
            )
        }


        override fun verifable(): Boolean = false

        private val transformer by lazy { IntroTransformer() }
        override fun updateElements() {
            intro_pager.adapter = pager
            intro_pager.setPageTransformer(false, transformer)
            action_btn.text = "Create Account"
            auth_question.text = "Already has an account?"
            auth_question_action.text = "Login"
        }

        override fun onAction() {
            setMode(signupMode)
        }

        override fun onVerify(): Boolean = false

        override fun onGoto() {
            setMode(loginMode)
        }
    }

    inner class SignUpAuthMode : AuthMode {
        override fun applyContraint(constraintSet: ConstraintSet) {
            AuthMode.makeIntroGone(constraintSet)
            AuthMode.makeGoogleFacebookVisible(constraintSet)
            constraintSet.makeGone(
                    R.id.setup_personalize,
                    R.id.profile_pic
            )
            constraintSet.makeVisible(
                    R.id.app_title,
                    R.id.username_field,
                    R.id.email_field,
                    R.id.password_field,
                    R.id.confirm_password_field,
                    R.id.or_continue_with,
                    R.id.auth_question,
                    R.id.auth_question_action,
                    R.id.action_holder
            )
        }

        override fun verifable(): Boolean = true

        override fun updateElements() {
            action_btn.text = "Sign Up"
            auth_question.text = "Already has an account?"
            auth_question_action.text = "Login"
            username_field.editText!!.hint = "Username"
            username_field.clear()
            email_field.clear()
            password_field.clear()
            confirm_password_field.clear()
        }

        override fun onVerify(): Boolean {
            return booleanArrayOf(
                    username_field.validateLength(2, 32, "Username"),
                    email_field.validateEmailorEmpty(),
                    password_field.validateLength(8, 32, "Password"),
                    (password_field to confirm_password_field).validateMatch("Password")
            ).all { it }
        }

        override fun onAction() {
            setLoading(true)
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                val req = UsernameAuthRequest(username_field.text,
                        password_field.text,
                        email_field.text,
                        it.token)
                MemeItClient.Auth.signUpWithUsername(req, { _ ->
                    setLoading(false) {
                        setMode(personalizeMode)
                    }
                }, onError)
            }.addOnFailureListener {
                val req = UsernameAuthRequest(username_field.text,
                        password_field.text,
                        email_field.text,
                        "")
                MemeItClient.Auth.signUpWithUsername(req, { _ ->
                    setLoading(false) {
                        setMode(personalizeMode)
                    }
                }, onError)
            }

        }

        override fun onFacebook() {
            MemeItClient.Auth.requestFacebookInfo(this@AuthActivity, callbackManager, {
                setMode(facebookSignupMode, Bundle().apply { putParcelable("fbinfo", it) })
            }, onError)

        }

        override fun onGoogle() {
            MemeItClient.Auth.requestGoogleInfo(this@AuthActivity)
        }

        override fun onGoto() {
            setMode(loginMode)
        }

        override fun getLastField(): TextInputLayout? = confirm_password_field

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            callbackManager.onActivityResult(requestCode, resultCode, data)
            if (requestCode == MemeItClient.Auth.GOOGLE_SIGN_IN_REQUEST_CODE && data != null) {
                MemeItClient.Auth.extractGoogleInfo(data, {
                    setMode(googleSignupMode, Bundle().apply { putParcelable("ginfo", it) })
                }, onError)
            }
        }

        override fun onBackPressed(): Boolean {
            setMode(introMode)
            return true
        }
    }

    inner class LogInAuthMode : AuthMode {
        override fun applyContraint(constraintSet: ConstraintSet) {
            AuthMode.makeIntroGone(constraintSet)
            AuthMode.makeGoogleFacebookVisible(constraintSet)
            constraintSet.makeGone(
                    R.id.setup_personalize,
                    R.id.email_field,
                    R.id.confirm_password_field,
                    R.id.profile_pic
            )
            constraintSet.makeVisible(
                    R.id.app_title,
                    R.id.username_field,
                    R.id.password_field,
                    R.id.or_continue_with,
                    R.id.auth_question,
                    R.id.auth_question_action,
                    R.id.action_holder
            )

        }

        override fun verifable(): Boolean = true

        override fun updateElements() {
            action_btn.text = "Log In"
            auth_question.text = "Doesn't have an account?"
            auth_question_action.text = "Sign up"
            username_field.editText!!.hint = "Username"

            username_field.clear()
            password_field.clear()
        }

        override fun onAction() {
            setLoading(true)
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                val req = UsernameAuthRequest(username_field.text, password_field.text, ftoken = it.token)
                MemeItClient.Auth.signInWithUsername(req, onSignedIn, onError)
            }.addOnFailureListener {
                onError(it.message!!)
            }

        }

        override fun getLastField(): TextInputLayout? = password_field


        private val onSignedIn by lazy {
            { _: User ->
                setLoading(false)
                val i = Intent(this@AuthActivity, MainActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
            }
        }

        override fun onVerify(): Boolean {
            return booleanArrayOf(
                    username_field.validateLength(2, 32, "Username"),
                    password_field.validateLength(8, 32, "Password")
            ).all { it }
        }

        override fun onFacebook() {
            //todo only request the id and token
            MemeItClient.Auth.requestFacebookID(this@AuthActivity, callbackManager, {
                MemeItClient.Auth.signInWithFacebook(it, onSignedIn, onError)
            }, onError)
        }

        override fun onGoogle() {
            MemeItClient.Auth.requestGoogleInfo(this@AuthActivity)
        }

        override fun onGoto() {
            setMode(signupMode)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            callbackManager.onActivityResult(requestCode, resultCode, data)
            if (requestCode == MemeItClient.Auth.GOOGLE_SIGN_IN_REQUEST_CODE && data != null) {
                MemeItClient.Auth.extractGoogleInfo(data, {
                    MemeItClient.Auth.signInWithGoogle(it.toSignInReq(), onSignedIn, onError)
                }, onError)
            }
        }

        override fun onBackPressed(): Boolean {
            setMode(introMode)
            return true
        }
    }

    inner class PersonalizeAuthMode : AuthMode {
        override fun init() {
            profile_pic.setOnClickListener {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(this@AuthActivity)
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

        override fun getLastField(): TextInputLayout? = username_field


        override fun verifable(): Boolean = true
        override fun updateElements() {
            username_field.editText!!.hint = "Name"
            username_field.clear()
            action_btn.text = "Continue"
        }

        override fun withBundle(bundle: Bundle) {
            super.withBundle(bundle)
            name = bundle.getString("name")
            bundle.getString("image")?.let {
                profileImageUrl = Uri.parse(it)
                if (profileImageUrl != null) isImageFrom3rdParty = true
            }
            username_field.editText!!.text.append(name)
            profile_pic.setImageRequest(ImageRequest.fromUri(profileImageUrl))
        }

        override fun onAction() {
            val name = username_field.text
            setLoading(true)
            if (isImageFrom3rdParty) {
                uploadData(name, profileImageUrl!!.toString())
            } else {
                profileImageUrl?.let {
                    uploadImageThenData(name, it.encodedPath!!)
                } ?: uploadData(name, null)
            }
        }

        override fun onVerify(): Boolean {
            return booleanArrayOf(
                    username_field.validateLength(2, 32, "Name")
            ).all { it }
        }

        private fun uploadData(name: String, image_url: String?) {
            val user = User(name = name, imageUrl = image_url)
            MemeItUsers.setupMyUser(user, {
                val i = Intent(this@AuthActivity, TagsChooserActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                i.putExtra("choose", true)
                startActivity(i)
            }, onError)
        }

        private fun uploadImageThenData(name: String, imageUrl: String) {
            MediaManager.get().upload(imageUrl).callback(object : UploadCallback {
                override fun onStart(s: String) {
                    showError("Uploading Image")
                }

                override fun onProgress(s: String, l: Long, l1: Long) {}
                override fun onSuccess(s: String, map: Map<*, *>) {
                    uploadData(name, map["public_id"].toString())
                }

                override fun onError(s: String, errorInfo: ErrorInfo) {
                    setLoading(false)
                    showError("Image Upload Error: ${errorInfo.description}")
                }

                override fun onReschedule(s: String, errorInfo: ErrorInfo) {
                    setLoading(false)
                    showError("Image Upload Error: ${errorInfo.description}")

                }
            }).dispatch()
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    profileImageUrl = result.uri
                    isImageFrom3rdParty = false
                    profile_pic.setImageURI(profileImageUrl)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    showError(result.error.message ?: "Cant read Image File")
                }
            }
        }
    }

    abstract inner class RequestUsernameMode : AuthMode {
        override fun applyContraint(constraintSet: ConstraintSet) {
            AuthMode.makeIntroGone(constraintSet)
            AuthMode.makeGoogleFacebookGone(constraintSet)
            constraintSet.makeGone(
                    R.id.intro_pager,
                    R.id.setup_personalize,
                    R.id.email_field,
                    R.id.password_field,
                    R.id.confirm_password_field,
                    R.id.profile_pic,
                    R.id.or_continue_with,
                    R.id.auth_question,
                    R.id.auth_question_action
            )
            constraintSet.makeVisible(
                    R.id.app_title,
                    R.id.username_field,
                    R.id.action_holder
            )
        }

        override fun updateElements() {
            action_btn.text = "Continue"
        }

        override fun getLastField(): TextInputLayout? = username_field

        override fun verifable(): Boolean = true

        override fun onVerify(): Boolean {
            return booleanArrayOf(
                    username_field.validateLength(2, 32, "Username")
            ).all { it }
        }
    }

    inner class FacebookSignupMode : RequestUsernameMode() {
        lateinit var fbinfo: FacebookInfo
        override fun withBundle(bundle: Bundle) {
            fbinfo = bundle.getParcelable("fbinfo")!!
        }

        override fun onAction() {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                val freq = fbinfo.toSignUpReq(username_field.text, it.token)
                MemeItClient.Auth.signUpWithFacebook(freq, { _ ->
                    val name = "${fbinfo.firstName} ${fbinfo.lastName}"
                    val b = Bundle()
                    b.putString("name", name)
                    b.putString("image", fbinfo.imageUrl)
                    setMode(personalizeMode, b)
                }, onError)
            }.addOnFailureListener {
                onError(it.message!!)
            }

        }
    }

    inner class GoogleSignupMode : RequestUsernameMode() {
        lateinit var ginfo: GoogleInfo
        override fun withBundle(bundle: Bundle) {
            ginfo = bundle.getParcelable("ginfo")!!
        }

        override fun onAction() {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                val greq = ginfo.toSignUpReq(username_field.text, it.token)
                MemeItClient.Auth.signUpWithGoogle(greq, { _ ->
                    val b = Bundle()
                    b.putString("name", ginfo.name)
                    b.putString("image", ginfo.imageUrl)
                    setMode(personalizeMode, b)
                }, onError)
            }.addOnFailureListener {
                onError(it.message!!)
            }

        }
    }

}

interface AuthMode {
    companion object {
        fun makeIntroVisible(constraintSet: ConstraintSet) {
            constraintSet.connect(R.id.intro_pager, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            constraintSet.connect(R.id.intro_pager, ConstraintSet.BOTTOM, R.id.action_top_guideline, ConstraintSet.TOP)
        }

        fun makeIntroGone(constraintSet: ConstraintSet) {
            constraintSet.connect(R.id.intro_pager, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            constraintSet.clear(R.id.intro_pager, ConstraintSet.TOP)
        }

        fun makeTitleVisible(constraintSet: ConstraintSet) {
            constraintSet.connect(R.id.app_title, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 16.dp)
            constraintSet.clear(R.id.app_title, ConstraintSet.BOTTOM)
        }

        fun makeTitleGone(constraintSet: ConstraintSet) {
            constraintSet.connect(R.id.app_title, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            constraintSet.clear(R.id.app_title, ConstraintSet.TOP)
        }

        fun makeGoogleFacebookVisible(constraintSet: ConstraintSet) {
            constraintSet.clear(R.id.signup_google, ConstraintSet.RIGHT)
            constraintSet.clear(R.id.signup_facebook, ConstraintSet.LEFT)
            constraintSet.createHorizontalChain(R.id.left_guideline, ConstraintSet.RIGHT,
                    R.id.right_guideline, ConstraintSet.LEFT,
                    intArrayOf(R.id.signup_google, R.id.signup_facebook),
                    floatArrayOf(1f, 1f),
                    ConstraintSet.CHAIN_PACKED)
            constraintSet.setMargin(R.id.signup_google, ConstraintSet.RIGHT, 8.dp)
            constraintSet.setMargin(R.id.signup_facebook, ConstraintSet.LEFT, 8.dp)
            constraintSet.connect(R.id.auth_question, ConstraintSet.TOP, R.id.signup_google, ConstraintSet.BOTTOM, 16.dp)
        }

        fun makeGoogleFacebookGone(constraintSet: ConstraintSet) {
            constraintSet.removeFromHorizontalChain(R.id.signup_google)
            constraintSet.removeFromHorizontalChain(R.id.signup_facebook)
            constraintSet.connect(R.id.signup_google, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
            constraintSet.connect(R.id.signup_facebook, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
            constraintSet.connect(R.id.auth_question, ConstraintSet.TOP, R.id.action_holder, ConstraintSet.BOTTOM, 16.dp)

        }


    }

    fun init() {}
    fun updateElements()
    fun withBundle(bundle: Bundle) {}
    fun applyContraint(constraintSet: ConstraintSet)
    fun onAction()
    fun getLastField(): TextInputLayout? = null
    fun onVerify(): Boolean = false
    fun onGoto() {}
    fun verifable(): Boolean
    fun onFacebook() {}
    fun onGoogle() {}
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {}
    fun onBackPressed() = false
}

fun ConstraintSet.makeGone(vararg ids: Int) = ids.forEach { this.setVisibility(it, View.GONE) }
fun ConstraintSet.makeVisible(vararg ids: Int) = ids.forEach { this.setVisibility(it, View.VISIBLE) }
val TextInputLayout.text get() = this.editText!!.text.toString()
fun TextInputLayout.clear() {
    this.editText!!.text.clear()
    this.editText!!.error = null
}

fun TextInputLayout.validateLength(min: Int, max: Int, tag: String): Boolean {
    this.text.validateLength(min, max, tag)?.let {
        editText!!.error = it
        return false
    } ?: return true
}

fun TextInputLayout.validateEmailorEmpty(): Boolean {
    this.text.validateEmailOrEmpty()?.let {
        editText!!.error = it
        return false
    } ?: return true
}

fun Pair<TextInputLayout, TextInputLayout>.validateMatch(tag: String): Boolean {
    first.text.validateMatch(second.text, tag)?.let {
        second.editText!!.error = it
        return false
    } ?: return true
}