package com.memeit.backend.kotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.net.ConnectivityManager
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.memeit.backend.MemeItMemes
import com.memeit.backend.MemeItUsers
import com.memeit.backend.dataclasses.*
import com.memeit.backend.utilis.*
import com.memeit.backend.utilis.Utils.checkAndFireError
import com.memeit.backend.utilis.Utils.checkAndFireSuccess
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
object MemeItClient {

    private const val TAG = "memeitclient"
    private const val HEADER_PRAGMA = "Pragma"
    private const val HEADER_CACHE_CONTROL = "Cache-Control"

    private lateinit var context: Context
    private lateinit var cache: Cache
    private lateinit var memeItService: MemeItService
    private var isConnected: Boolean = false
    private var isInitialized: Boolean = false
    private lateinit var sharedPref: SharedPreferences

    private val cacheFile: File by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            File(context.dataDir, "memeit-http‐cache")
        else
            File(context.cacheDir, "memeit-http‐cache")

    }

    fun init(context: Context, baseUrl: String) {
        if (isInitialized) {
            throw RuntimeException("Already Initialized")
        }
        this.context = context
        sharedPref = PreferenceManager.getDefaultSharedPreferences(MemeItClient.context)

        val cm = MemeItClient.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val connectionReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val ni = cm.activeNetworkInfo
                isConnected = ni != null && ni.isConnectedOrConnecting
            }
        }
        MemeItClient.context.registerReceiver(connectionReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        cache = Cache(cacheFile, (10 * 1024 * 1024).toLong())
        val authInterceptor = Interceptor {
            val token = Auth.getToken()
            if (token.isNullOrEmpty())
                return@Interceptor it.proceed(it.request())
            val req = it.request().newBuilder().header("authorization", token!!)
            return@Interceptor it.proceed(req.build())
        }
        val cacheInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val cacheControl: CacheControl = if (isConnected) {
                CacheControl.Builder()
                        .maxAge(0, TimeUnit.SECONDS)
                        .build()
            } else {
                CacheControl.Builder()
                        .maxStale(30, TimeUnit.DAYS)
                        .build()
            }
            response.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                    .build()
        }
        val offlineCacheInterceptor = Interceptor { chain ->
            var request = chain.request()
            if (!isConnected) {
                val cacheControl = CacheControl.Builder()
                        .maxStale(30, TimeUnit.DAYS)
                        .build()
                request = request.newBuilder()
                        .removeHeader(HEADER_PRAGMA)
                        .removeHeader(HEADER_CACHE_CONTROL)
                        .cacheControl(cacheControl)
                        .build()
            }
            chain.proceed(request)
        }
        val builder = OkHttpClient.Builder()
                .addInterceptor(offlineCacheInterceptor)
                .addNetworkInterceptor(cacheInterceptor)
                .addInterceptor(authInterceptor)
                .cache(cache)
        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        memeItService = retrofit.create(com.memeit.backend.kotlin.MemeItService::class.java)
        MemeItUsers.init()
        MemeItMemes.init()
        isInitialized = true
    }


    fun clearCache() {
        cacheFile.delete()
    }

    val calls = mutableListOf<Call<out Any>>()


    fun cancelAllCalls() {
        calls.forEach { it.cancel() }
        calls.clear()
    }

    object Auth {
        enum class SignInMethod {
            USERNAME, GOOGLE, FACEBOOK
        }

        private const val GOOGLE_SIGN_IN_REQUEST_CODE = 6598
        private const val PREFERENCE_TOKEN = "__token__"
        private const val PREFERENCE_UID = "__uid__"
        private const val PREFERENCE_SIGNIN_METHOD = "__signin_method__"
        private const val PREFERENCE_USER_DATA_SAVED = "__user_data_saved__"

        fun isSignedIn(): Boolean {
            val tokenExists = sharedPref.getString(PREFERENCE_TOKEN, null) != null
            if (!tokenExists) return false
            val method = getSignedInMethod() ?: return false
            if (method == SignInMethod.GOOGLE)
                return GoogleSignIn.getLastSignedInAccount(context) != null
            return if (method == SignInMethod.FACEBOOK) {
                //todo:jv -check if facebook sign-in status
                FacebookSdk.isInitialized()
                false
            } else
                true
        }

        fun isUserDataSaved(): Boolean {
            val name = Users.getMyUser()?.name
            return !name.isNullOrEmpty()
        }

        fun getSignedInMethod(): SignInMethod? {
            val m = sharedPref.getString(PREFERENCE_SIGNIN_METHOD, null) ?: return null
            return try {
                SignInMethod.valueOf(m)
            } catch (e: IllegalArgumentException) {
                null
            }
        }

        fun signUpWithUsername(usernameAuthRequest: UsernameAuthRequest,
                               onSignedUp: ((MyUser) -> Unit)? = null,
                               onError: ((String) -> Unit) = {}) {
            memeItService.signUpWithEmail(usernameAuthRequest).call({
                setSignedIn(SignInMethod.USERNAME, it)
                onSignedUp?.invoke(it.myUser)
            }, onError)
        }


        fun signInWithUsername(usernameAuthRequest: UsernameAuthRequest,
                               onSignedIn: ((MyUser) -> Unit)? = null,
                               onError: ((String) -> Unit) = {}) {
            memeItService.signInWithEmail(usernameAuthRequest).call({
                setSignedIn(SignInMethod.USERNAME, it)
                onSignedIn?.invoke(it.myUser)
            }, onError)
        }

        fun requestGoogleInfo(activity: Activity) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestProfile()
                    .build()
            val client = GoogleSignIn.getClient(activity, gso)
            val gc = client.signInIntent
            activity.startActivityForResult(gc, GOOGLE_SIGN_IN_REQUEST_CODE)
        }

        fun extractGoogleAuthRequest(data: Intent): GoogleAuthSignInRequest {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            return GoogleAuthSignInRequest(account.email!!, account.id!!)
        }

        fun extractGoogleAuthRequest(data: Intent, username: String): GoogleAuthSignUpRequest {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            return GoogleAuthSignUpRequest(username, account.email!!, account.id!!)
        }

        fun signUpWithGoogle(googleAuthRequest: GoogleAuthSignUpRequest,
                             onSignedUp: ((MyUser) -> Unit)?,
                             onError: ((String) -> Unit) = {}) {

            memeItService.signUpWithGoogle(googleAuthRequest).call({
                setSignedIn(SignInMethod.GOOGLE, it)
                onSignedUp?.invoke(it.myUser)
            }, onError)
        }

        fun signInWithGoogle(googleAuthRequest: GoogleAuthSignInRequest,
                             onSignedUp: ((MyUser) -> Unit)?,
                             onError: ((String) -> Unit) = {}) {
            memeItService.signInWithGoogle(googleAuthRequest).call({
                setSignedIn(SignInMethod.GOOGLE, it)
                onSignedUp?.invoke(it.myUser)
            }, onError)
        }

        fun changePassword(changePasswordRequest: ChangePasswordRequest,
                           onSuccess: (() -> Unit)?,
                           onError: ((String) -> Unit) = {}) {
            memeItService.updatePassword(changePasswordRequest).call({
                onSuccess?.invoke()
            }, onError)
        }

        fun signInWithFacebook() {
            //todo handle this
            val cb = CallbackManager.Factory.create()
            LoginManager.getInstance().registerCallback(cb, object : FacebookCallback<LoginResult?> {
                override fun onSuccess(result: LoginResult?) {
                    if (result != null) {

                    }
                }

                override fun onCancel() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onError(error: FacebookException?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }

        private fun setSignedIn(signInMethod: SignInMethod, authResponse: AuthResponse) {
            sharedPref.edit()
                    .putString(PREFERENCE_TOKEN, authResponse.token)
                    .putString(PREFERENCE_SIGNIN_METHOD, signInMethod.toString())
                    .apply()
            authResponse.myUser.save(sharedPref)
        }

        fun getToken(): String? {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(PREFERENCE_TOKEN, null)
        }


        fun signOut() {
            sharedPref.edit()
                    .remove(PREFERENCE_TOKEN)
                    .remove(PREFERENCE_UID)
                    .remove(PREFERENCE_SIGNIN_METHOD)
                    .remove(PREFERENCE_USER_DATA_SAVED)
                    .apply()
            MyUser.delete(context)
            clearCache()
        }
    }

    object Users : MemeItUser by memeItService {
        fun getMyUser(): MyUser? {
            return MyUser.createFromCache(sharedPref)
        }

        fun getMyUser(onSuccess: ((User) -> Unit)? = null, onError: (String) -> Unit = {}) {
            memeItService.getMyUser().call({
                it.toMyUser().save(context)
                onSuccess?.invoke(it)
            }, onError)
        }

        //todo update server
        fun setupMyUser(myUser: MyUser, onSuccess: (() -> Unit)? = null, onError: (String) -> Unit = {}) {
            memeItService
                    .uploadUserData(myUser)
                    .call({
                        MyUser().save(context)
                        onSuccess?.invoke()
                    }, onError)
        }

        fun updateUsername(username: String,
                           onSuccess: (() -> Unit)? = null,
                           onError: ((String) -> Unit) = {}) {
            memeItService.updateUsername(User.username(username))
                    .call({
                        getMyUser()?.setUsername(username)?.save(context)
                        onSuccess?.invoke()
                    }, onError)
        }

        fun updateName(name: String,
                       onSuccess: (() -> Unit)? = null,
                       onError: ((String) -> Unit) = {}) {
            memeItService.updateUsername(User.name(name))
                    .call({
                        getMyUser()?.setName(name)?.save(context)
                        onSuccess?.invoke()
                    }, onError)
        }

        fun updateProfilePic(pic: String,
                             onSuccess: (() -> Unit)? = null,
                             onError: ((String) -> Unit) = {}) {
            memeItService.updateUsername(User.pic(pic))
                    .call({
                        getMyUser()?.setPic(pic)?.save(context)
                        onSuccess?.invoke()
                    }, onError)
        }

        fun updateCoverPic(cpic: String,
                           onSuccess: (() -> Unit)? = null,
                           onError: ((String) -> Unit) = {}) {
            memeItService.updateUsername(User.cpic(cpic))
                    .call({
                        getMyUser()?.setCpic(cpic)?.save(context)
                        onSuccess?.invoke()
                    }, onError)
        }


    }

    object Memes : MemeItMeme by memeItService

}

//todo remove this class when all classes r in kotlin
abstract class OnCompleted<T : Any> : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) onSuccess(response.body()!!)
        else {
            val err = response.errorBody()?.string() ?: response.message()
            onError(err)
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        onError(t.localizedMessage)
    }

    abstract fun onSuccess(t: T)
    abstract fun onError(message: String)
}

fun <T : Any> Call<T>.call(onSuccess: ((T) -> Unit)): Call<T> = call(onSuccess, {})
inline fun <T : Any> Call<T>.call(crossinline onSuccess: ((T) -> Unit), crossinline onError: ((String) -> Unit)): Call<T> {
    MemeItClient.calls += this
    this.enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            MemeItClient.calls -= call
            if (response.isSuccessful) onSuccess(response.body()!!)
            else {
                val err = response.errorBody()?.string() ?: response.message()
                onError(err)
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            onError(t.localizedMessage)
            MemeItClient.calls -= call
        }
    })
    return this
}