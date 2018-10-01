package com.memeit.backend.kotlin

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import com.memeit.backend.MemeItMemes
import com.memeit.backend.MemeItUsers
import com.memeit.backend.utilis.PrefUtils
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
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

    private lateinit var context: Context
    private lateinit var cache: Cache
    lateinit var memeItService: MemeItService
    internal var isConnected: Boolean = false
    var isInitialized: Boolean = false

    fun init(context: Context, baseUrl: String) {
        if (isInitialized) {
            throw RuntimeException("Already Initialized")
        }

        val cm = MemeItClient.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val connectionReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val ni = cm.activeNetworkInfo
                isConnected = ni != null && ni.isConnectedOrConnecting
            }
        }
        MemeItClient.context.registerReceiver(connectionReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        try {
            cache = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Cache(File(context.dataDir, "memeit-http‐cache"),
                        (10 * 1024 * 1024).toLong()) // 10 MB
            } else {
                Cache(File(context.cacheDir, "memeit-http‐cache"),
                        (10 * 1024 * 1024).toLong()) // 10 MB
            }
        } catch (e: Exception) {
            Log.e(TAG, "Could not create Cache!")
        }

        val authInterceptor = Interceptor {
            val token = "" //MemeItAuth.getInstance().token
            if (token.isEmpty())
                return@Interceptor it.proceed(it.request())
            val req = it.request().newBuilder().header("authorization", token)
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
        PrefUtils.init(context)
        isInitialized = true
    }
    object Auth{

    }


    fun clearCache() {
        //todo implement this
    }

    val calls = mutableListOf<Call<out Any>>()
    fun <T : Any> Call<T>.call(onSuccess: ((T) -> Unit)) = call(onSuccess, {})
    inline fun <T : Any> Call<T>.call(crossinline onSuccess: ((T) -> Unit), onError: (() -> Unit)) {
        calls += this
        this.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                calls -= call
                if (response.isSuccessful) onSuccess(response.body()!!)
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                calls -= call
            }
        })
    }
    fun cancelAllCalls() {
        calls.forEach { it.cancel() }
        calls.clear()
    }

}