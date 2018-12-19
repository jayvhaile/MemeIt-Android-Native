package com.innov8.memeit.Workers

import android.content.Context
import androidx.work.*
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import com.google.firebase.iid.FirebaseInstanceId
import com.memeit.backend.MemeItUsers
import com.memeit.backend.models.FirebaseToken
import java.io.IOException
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

fun retrieveAndUploadFirebaseToken() {
    val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

    val w1 = OneTimeWorkRequest.Builder(FirebaseTokenRetrieveWorker::class.java)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.HOURS)
            .addTag("firebase")
            .addTag("ftoken")
            .setConstraints(constraints)
            .build()
    val w2 = OneTimeWorkRequest.Builder(FirebaseTokenUploadWorker::class.java)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.HOURS)
            .addTag("ftoken")
            .setConstraints(constraints)
            .build()

    WorkManager.getInstance()
            .beginUniqueWork("ftoken", ExistingWorkPolicy.REPLACE, w1)
            .then(w2)
            .enqueue()
}

fun uploadFirebaseToken(token: String) {
    val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    val w2 = OneTimeWorkRequest.Builder(FirebaseTokenUploadWorker::class.java)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.HOURS)
            .addTag("ftoken")
            .setInputData(Data.Builder().putString("token", token).build())
            .setConstraints(constraints)
            .build()

    WorkManager.getInstance()
            .enqueueUniqueWork("ftoken", ExistingWorkPolicy.REPLACE, w2)
}


class FirebaseTokenRetrieveWorker(context: Context, params: WorkerParameters) : ListenableWorker(context, params) {
    override fun startWork(): ListenableFuture<Payload> {
        val future = SettableFuture.create<Payload>()

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if (it.isSuccessful) {
                future.set(
                        Payload(
                                Result.SUCCESS,
                                Data.Builder().putString("token", it.result!!.token).build()
                        )
                )
            } else
                future.set(Payload(Result.RETRY))
        }

        return future
    }

}

class FirebaseTokenUploadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val token = inputData.getString("token")!!
        return try {
            val response = MemeItUsers.updateUserToken(FirebaseToken(token)).execute()
            if (response.isSuccessful)
                Result.SUCCESS
            else
                Result.RETRY
        } catch (e: IOException) {
            Result.RETRY
        } catch (e: RuntimeException) {
            Result.FAILURE
        }


    }
}
