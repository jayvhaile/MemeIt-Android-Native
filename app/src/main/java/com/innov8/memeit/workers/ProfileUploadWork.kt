package com.innov8.memeit.workers

import android.content.Context
import android.preference.PreferenceManager
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItUsers
import com.memeit.backend.models.MyUser
import com.memeit.backend.models.UserReq
import java.io.File
import java.io.IOException


class ProfileImageUploadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    companion object {
        const val IMAGE_URL = "imageUrl"
    }
    override fun doWork(): Result {
        val file = File(inputData.getString(IMAGE_URL))
        return if (file.exists()) {
            try {
                val (response, name) = MemeItClient.uploadFile(file)
                if (response.isSuccessful) {
                    outputData = Data.Builder().putString(ProfileUploadWorker.UPLOADED_IMAGE_NAME, name).build()
                    Result.SUCCESS
                } else
                    Result.RETRY
            } catch (io: IOException) {
                Result.RETRY
            } catch (req: RuntimeException) {
                Result.FAILURE
            }
        } else
            Result.FAILURE
    }
}

class ProfileUploadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    companion object {
        const val UPLOADED_IMAGE_NAME = "uploadedImageName"
    }

    override fun doWork(): Result {
        val uploadedImageName = inputData.getString(UPLOADED_IMAGE_NAME)
        return try {
            val postResponse = MemeItUsers.updateUser(UserReq(imageUrl = uploadedImageName)).execute()
            if (postResponse.isSuccessful) {
                MyUser.save(PreferenceManager.getDefaultSharedPreferences(applicationContext), user = postResponse.body()!!)
                Result.SUCCESS
            } else
                Result.RETRY
        } catch (io: IOException) {
            Result.RETRY
        } catch (req: RuntimeException) {
            Result.FAILURE
        }
    }
}