package com.innov8.memeit.workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.innov8.memeit.activities.CommentsActivity
import com.innov8.memeit.commons.PushNotification
import com.innov8.memeit.commons.sendUserNotification
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItMemes
import com.memeit.backend.models.Meme
import retrofit2.Response
import java.io.File
import java.io.IOException

class MemeImageUploadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    companion object {
        const val FILE_PATH = "path"
        const val STATUS = "status"
    }

    override fun doWork(): Result {
        val file = File(inputData.getString(FILE_PATH))
        return if (file.exists()) {
            try {
                this.outputData = Data.Builder().putString(STATUS, "Uploading Image").build()
                val (response, name) = MemeItClient.uploadFile(file)
                if (response.isSuccessful) {
                    this.outputData = Data.Builder()
                            .putString(STATUS, "Image Uploaded,Posting Meme")
                            .putString(MemeUploadWorker.UPLOADED_MEME_NAME, name)
                            .build()
                    file.delete()
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

class MemeUploadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    companion object {
        const val UPLOADED_MEME_NAME = "uploaded_meme_name"
        const val STATUS = "status"
        const val IMAGE_RATIO = "ratio"
        const val DESCRIPTION = "desc"
        const val TEXTS = "texts"
        const val TYPE = "type"
        const val TEMPLATE_ID = "tid"

    }

    override fun doWork(): Result {
        val name = inputData.getString(UPLOADED_MEME_NAME)!!
        return try {
            val postResponse = postMeme(name)
            if (postResponse.isSuccessful) {
                this.outputData = Data.Builder().putString(STATUS, "Meme Posted").build()
                notify(postResponse)
                Result.SUCCESS
            } else
                Result.RETRY

        } catch (io: IOException) {
            Result.RETRY
        } catch (req: RuntimeException) {
            Result.FAILURE
        }
    }

    private fun postMeme(name: String): Response<Meme> {
        val ratio = inputData.getFloat(IMAGE_RATIO, 1f)
        val description = inputData.getString(DESCRIPTION)
        val texts = inputData.getStringArray(TEXTS)
        val type = inputData.getString(TYPE) ?: "IMAGE"
        val templateID = inputData.getString(TEMPLATE_ID)

        return MemeItMemes.postMeme(Meme(imageId = name,
                imageRatio = ratio.toDouble(),
                type = type,
                description = description,
                texts = texts?.toList() ?: listOf(),
                tid = templateID
        )).execute()
    }

    private fun notify(postResponse: Response<Meme>) {
        applicationContext.sendUserNotification(
                PushNotification(
                        21,
                        null,
                        "Your Meme has Been Uploaded",
                        "Click here to check it out",
                        intent = CommentsActivity.startWithMemeIdIntent(applicationContext, postResponse.body()!!.id!!))
        )
    }
}