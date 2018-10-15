package com.memeit.backend

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.gson.Gson
import com.memeit.backend.dataclasses.*
import okhttp3.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
object MemeItClient {

    private const val TAG = "memeitclient"
    private const val HEADER_PRAGMA = "Pragma"
    private const val HEADER_CACHE_CONTROL = "Cache-Control"

    lateinit var context: Context
    private lateinit var cache: Cache
    internal lateinit var memeItService: MemeItService
    private var isConnected: Boolean = false
    private var isInitialized: Boolean = false
    internal lateinit var sharedPref: SharedPreferences


    var myUser: MUser? = null
        get() = MUser.get(sharedPref)
        private set


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
        MemeItClient.context = context
        sharedPref = PreferenceManager.getDefaultSharedPreferences(MemeItClient.context)

        initConnectivityListener()

        cache = Cache(cacheFile, (10 * 1024 * 1024).toLong())
        val authInterceptor = Interceptor {
            val token = myUser?.token
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
        memeItService = retrofit.create(MemeItService::class.java)
        isInitialized = true
    }

    private fun initConnectivityListener() {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val connectionReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val ni = cm.activeNetworkInfo
                isConnected = ni != null && ni.isConnectedOrConnecting
            }
        }
        context.registerReceiver(connectionReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
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

        const val GOOGLE_SIGN_IN_REQUEST_CODE = 6598


        fun isSignedIn(): Boolean {
            val mu = myUser ?: return false
            return when (mu.signInMethod) {
                SignInMethod.GOOGLE -> GoogleSignIn.getLastSignedInAccount(context) != null
                SignInMethod.FACEBOOK -> {
                    val token = AccessToken.getCurrentAccessToken()
                    token != null && !token.isExpired
                }
                else -> true
            }
        }

        fun isUserDataSaved(): Boolean {
            return myUser?.name?.isNotEmpty() ?: false
        }

        fun signUpWithUsername(usernameAuthRequest: UsernameAuthRequest,
                               onSignedUp: ((User) -> Unit)? = null,
                               onError: ((String) -> Unit) = {}) {
            memeItService.signUpWithEmail(usernameAuthRequest).call({
                setSignedIn(SignInMethod.USERNAME, it)
                onSignedUp?.invoke(it.user)
            }, onError)
        }


        fun signInWithUsername(usernameAuthRequest: UsernameAuthRequest,
                               onSignedIn: ((User) -> Unit)? = null,
                               onError: ((String) -> Unit) = {}) {
            memeItService.signInWithEmail(usernameAuthRequest).call({
                setSignedIn(SignInMethod.USERNAME, it)
                onSignedIn?.invoke(it.user)
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

        fun extractGoogleInfo(data: Intent, onSuccess: ((GoogleInfo) -> Unit),
                              onError: ((String) -> Unit) = {}) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                val info = GoogleInfo(account.displayName!!, account.photoUrl?.toString(), account.email!!, account.id!!)
                onSuccess(info)
            } catch (ex: ApiException) {
                onError(CommonStatusCodes.getStatusCodeString(ex.statusCode))
            }
        }

        fun signUpWithGoogle(googleAuthRequest: GoogleAuthSignUpRequest,
                             onSignedUp: ((User) -> Unit)?,
                             onError: ((String) -> Unit) = {}) {

            memeItService.signUpWithGoogle(googleAuthRequest).call({
                setSignedIn(SignInMethod.GOOGLE, it)
                onSignedUp?.invoke(it.user)
            }, onError)
        }

        fun signInWithGoogle(googleAuthRequest: GoogleAuthSignInRequest,
                             onSignedUp: ((User) -> Unit)?,
                             onError: ((String) -> Unit) = {}) {
            memeItService.signInWithGoogle(googleAuthRequest).call({
                setSignedIn(SignInMethod.GOOGLE, it)
                onSignedUp?.invoke(it.user)
            }, onError)
        }

        fun changePassword(changePasswordRequest: ChangePasswordRequest,
                           onSuccess: (() -> Unit)?,
                           onError: ((String) -> Unit) = {}) {
            memeItService.updatePassword(changePasswordRequest).call({
                onSuccess?.invoke()
            }, onError)
        }

        fun requestFacebookInfo(activity: Activity, callbackManager: CallbackManager,
                                onSuccess: ((FacebookInfo) -> Unit),
                                onError: ((String) -> Unit) = {}) {
            val perms = listOf("email", "public_profile")
            LoginManager.getInstance().logInWithReadPermissions(activity, perms)
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                override fun onSuccess(result: LoginResult?) {
                    if (result != null) {
                        val req = GraphRequest.newMeRequest(result.accessToken) { json, _ ->
                            val facebookInfo = FacebookInfo(result.accessToken.userId,
                                    json.getString("first_name"),
                                    json.getString("last_name"),
                                    json.getString("email"))
                            onSuccess(facebookInfo)
                        }
                        val params = Bundle()
                        params.putString("fields", "id,email,first_name,last_name")
                        req.parameters = params
                        req.executeAsync()
                    } else onError("Unknown Error, Please Try Again")
                }

                override fun onCancel() {
                    onError("Canceled by User")
                }

                override fun onError(error: FacebookException) {
                    onError(error.message ?: "Unknown Error")
                }
            })
        }
        fun requestFacebookID(activity: Activity, callbackManager: CallbackManager,
                                onSuccess: ((FacebookInfo) -> Unit),
                                onError: ((String) -> Unit) = {}) {

            //todo make sure the id comes from facebook and for this application only
            val perms = listOf("email", "public_profile")
            LoginManager.getInstance().logInWithReadPermissions(activity, perms)
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                override fun onSuccess(result: LoginResult?) {
                    if (result != null) {
                        val req = GraphRequest.newMeRequest(result.accessToken) { json, _ ->
                            val facebookInfo = FacebookInfo(result.accessToken.userId,
                                    json.getString("first_name"),
                                    json.getString("last_name"),
                                    json.getString("email"))
                            onSuccess(facebookInfo)
                        }
                        val params = Bundle()
                        params.putString("fields", "id,email,first_name,last_name")
                        req.parameters = params
                        req.executeAsync()
                    } else onError("Unknown Error, Please Try Again")
                }

                override fun onCancel() {
                    onError("Canceled by User")
                }

                override fun onError(error: FacebookException) {
                    onError(error.message ?: "Unknown Error")
                }
            })
        }

        fun signUpWithFacebook(facebookAuthRequest: FacebookAuthSignUpRequest,
                               onSignedUp: ((User) -> Unit)?,
                               onError: ((String) -> Unit) = {}) {
            memeItService.signUpWithFacebook(facebookAuthRequest).call({
                setSignedIn(SignInMethod.FACEBOOK, it)
                onSignedUp?.invoke(it.user)
            }, onError)
        }

        fun signInWithFacebook(facebookAuthRequest: FacebookAuthSignInRequest,
                               onSignedUp: ((User) -> Unit)?,
                               onError: ((String) -> Unit) = {}) {
            memeItService.signInWithFacebook(facebookAuthRequest).call({
                setSignedIn(SignInMethod.FACEBOOK, it)
                onSignedUp?.invoke(it.user)
            }, onError)
        }

        private fun setSignedIn(signInMethod: SignInMethod, authResponse: AuthResponse) {
            MUser.save(sharedPref, authResponse.token,
                    signInMethod,
                    authResponse.user.uid,
                    authResponse.user.username,
                    authResponse.user.name,
                    authResponse.user.imageUrl,
                    authResponse.user.coverImageUrl)
        }


        fun signOut() {
            MUser.delete(context)
            clearCache()
        }
    }

}

object MemeItUsers : MemeItService by MemeItClient.memeItService {


    fun getMyUser(onSuccess: ((User) -> Unit)? = null, onError: (String) -> Unit = {}) {
        MemeItClient.memeItService.loadMyUser().call({
            MUser.save(MemeItClient.sharedPref, it)
            onSuccess?.invoke(it)
        }, onError)
    }

    //todo update server
    fun setupMyUser(user: User, onSuccess: (() -> Unit)? = null, onError: (String) -> Unit = {}) {
        MemeItClient.memeItService
                .uploadUserData(user)
                .call({
                    MUser.save(MemeItClient.sharedPref, user)
                    onSuccess?.invoke()
                }, onError)
    }

    fun updateUsername(username: String,
                       onSuccess: (() -> Unit)? = null,
                       onError: ((String) -> Unit) = {}) {
        MemeItClient.memeItService.updateUsername(User(username = username))
                .call({
                    MUser.save(MemeItClient.sharedPref, username = username)
                    onSuccess?.invoke()
                }, onError)
    }

    fun updateName(name: String,
                   onSuccess: (() -> Unit)? = null,
                   onError: ((String) -> Unit) = {}) {
        MemeItClient.memeItService.updateUsername(User(name = name))
                .call({
                    MUser.save(MemeItClient.sharedPref, name = name)
                    onSuccess?.invoke()
                }, onError)
    }

    fun updateProfilePic(pic: String,
                         onSuccess: (() -> Unit)? = null,
                         onError: ((String) -> Unit) = {}) {
        MemeItClient.memeItService.updateUsername(User(imageUrl = pic))
                .call({
                    MUser.save(MemeItClient.sharedPref, profilePic = pic)
                    onSuccess?.invoke()
                }, onError)
    }

    fun updateCoverPic(cpic: String,
                       onSuccess: (() -> Unit)? = null,
                       onError: ((String) -> Unit) = {}) {
        MemeItClient.memeItService.updateUsername(User(coverImageUrl = cpic))
                .call({
                    MUser.save(MemeItClient.sharedPref, coverPic = cpic)
                    onSuccess?.invoke()
                }, onError)
    }


}

object MemeItMemes : MemeItService by MemeItClient.memeItService

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

