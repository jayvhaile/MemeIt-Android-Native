package com.memeit.backend.kotlin

import android.content.Context
import android.os.Build
import com.memeit.backend.MemeItAuth
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class MemeItClient private constructor(val context: Context, val baseUrl: String) {
    companion object {
        var memeItClient: MemeItClient? = null
        fun init(context: Context, baseUrl: String) {
            if (memeItClient == null)
                memeItClient = MemeItClient(context, baseUrl)
            else
                throw RuntimeException("MemeItClient Already Initialized!")
        }

        fun getInstance(): MemeItClient {
            return memeItClient ?: throw RuntimeException("Initializr it first!")
        }
    }

    internal val memeInterface: MemeInterface

    init {
        val builder = OkHttpClient.Builder()
                //.addInterceptor(provideOfflineCacheInterceptor())
                //.addNetworkInterceptor(provideCacheInterceptor())
                .addInterceptor(provideAuthInterceptor())
                .cache(provideCache())
        val retrofit = Retrofit.Builder()
                .baseUrl(this.baseUrl)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        memeInterface = retrofit.create(MemeInterface::class.java)
    }

    private fun provideCache(): Cache =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                Cache(File(context.dataDir, "memeit-http‐cache"),(10L * 1024 * 1024))
            else
                Cache(File(context.cacheDir, "memeit-http‐cache"),(10L * 1024 * 1024))

    private fun provideAuthInterceptor(): Interceptor {
        return Interceptor {
            val token = MemeItAuth.getInstance().token
            if (token.isEmpty())
                return@Interceptor it.proceed(it.request())
            val req = it.request().newBuilder().header("authorization", token)
            return@Interceptor it.proceed(req.build())
        }
    }
}