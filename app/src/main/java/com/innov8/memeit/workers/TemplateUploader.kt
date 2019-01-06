package com.innov8.memeit.workers

import android.content.Context
import android.content.Intent
import androidx.work.*
import com.innov8.memeit.activities.MemeChooserActivity
import com.innov8.memeit.MemeItApp
import com.innov8.memeit.R
import com.innov8.memeit.commons.NotifData
import com.innov8.memeit.commons.sendUserNotification
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItMemes
import com.memeit.backend.models.MemeTemplate
import com.memeit.backend.models.SavedState
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import java.io.File
import java.io.IOException
import java.util.*

class TemplateImageUploader(context: Context, params: WorkerParameters) : Worker(context, params) {
    companion object {
        const val PARAM_FILE_PATH = "filepath"
        const val PARAM_INDEX = "index"
        const val PARAM_IS_PREVIEW_IMAGE = "is preview"
        const val OUTPUT_IMAGE_NAME = "uploaded image name"
        const val OUTPUT_PREVIEW_IMAGE_NAME = "uploaded preview image name"

    }

    override fun doWork(): Result {
        val file = File(inputData.getString(PARAM_FILE_PATH))
        val index = inputData.getInt(PARAM_INDEX, 0)
        val preview = inputData.getBoolean(PARAM_IS_PREVIEW_IMAGE, false)
        return if (file.exists()) {
            try {
                this.outputData = Data.Builder().putString(MemeImageUploadWorker.STATUS, "Uploading Image").build()
                val (response, name) = MemeItClient.uploadFile(file)
                if (response.isSuccessful) {
                    val b = Data.Builder()
                    if (preview)
                        b.putString(OUTPUT_PREVIEW_IMAGE_NAME, name)
                    else
                        b.putString(OUTPUT_IMAGE_NAME, "$index:$name")

                    this.outputData = b.build()
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

class TemplateUploader(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val name = inputData.getStringArray("name")!![0]
        val previewImage = inputData.getStringArray(TemplateImageUploader.OUTPUT_PREVIEW_IMAGE_NAME)!![0]
        val imageNames = inputData.getStringArray(TemplateImageUploader.OUTPUT_IMAGE_NAME)!!
        //template temp location

        val temp = File(MemeTemplate.getTempUploadJsonDir(applicationContext), "$name.json")
        return if (temp.exists()) {
            MemeTemplate.readFromFile(temp)?.run {
                try {
                    this.memeTemplateProperty.apply {
                        images.clear()
                        images.addAll(
                                imageNames
                                        .map {
                                            val s = it.split(":")
                                            s[0] to s[1]
                                        }
                                        .sortedBy { it.first }
                                        .map { it.second }
                        )
                        previewImageUrl = previewImage
                    }
                    val response = MemeItMemes.postTemplate(this).execute()
                    if (response.isSuccessful) {
                        temp.delete()
                        notifyFinished()
                        Result.SUCCESS
                    } else
                        Result.RETRY

                } catch (e: IOException) {
                    Result.RETRY
                } catch (e: RuntimeException) {
                    Result.FAILURE
                }
            } ?: Result.FAILURE
        } else return Result.FAILURE
    }

    private fun notifyFinished() {
        applicationContext.sendUserNotification(
                NotifData("Your Meme Template has Been Uploaded.",
                        "We will let you know when it is approved.",
                        R.mipmap.icon,
                        Intent(applicationContext, MemeChooserActivity::class.java))
                , 579)
    }
}

fun startTemplateUploadWork(context: Context, template: MemeTemplate) {
    if (template.memeTemplateProperty.getState() == SavedState.LOCAL) {
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        val w = template.memeTemplateProperty.images.mapIndexed { i, it ->
            OneTimeWorkRequest.Builder(TemplateImageUploader::class.java)
                    .setConstraints(constraints)
                    .addTag("template image upload")
                    .addTag("template upload")
                    .setInputData(
                            Data.Builder()
                                    .putString(TemplateImageUploader.PARAM_FILE_PATH, it)
                                    .putInt(TemplateImageUploader.PARAM_INDEX, i)
                                    .build()
                    )
                    .build()
        }.toMutableList().apply {
            add(
                    OneTimeWorkRequest.Builder(TemplateImageUploader::class.java)
                            .setConstraints(constraints)
                            .addTag("template image upload")
                            .addTag("template upload")
                            .setInputData(Data.Builder()
                                    .putString(TemplateImageUploader.PARAM_FILE_PATH, template.memeTemplateProperty.previewImageUrl)
                                    .putBoolean(TemplateImageUploader.PARAM_IS_PREVIEW_IMAGE, true)
                                    .build())
                            .build()
            )
        }


        val name = UUID.randomUUID().toString()
        val s = OneTimeWorkRequest.Builder(TemplateUploader::class.java)
                .addTag("template json upload")
                .addTag("template upload")
                .setInputData(Data.Builder().putString("name", name).build())
                .setInputMerger(ArrayCreatingInputMerger::class.java)
                .build()

        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            withContext(Dispatchers.Default) {
                val file = File(MemeTemplate.getTempUploadJsonDir(context), "$name.json")
                template.saveToFile(file)
            }
            if (MemeItApp.USE_LOCAL_SERVER) {
                val d = Data.Builder()
                        .putString("name", name)
                        .putString(TemplateImageUploader.OUTPUT_PREVIEW_IMAGE_NAME, "preview.jpg")
                        .putString(TemplateImageUploader.OUTPUT_IMAGE_NAME, "image1.jpg")
                        .build()
                WorkManager.getInstance().enqueue(
                        OneTimeWorkRequest.Builder(TemplateUploader::class.java)
                                .addTag("template json upload")
                                .addTag("template upload")
                                .setInputData(d)
                                .setInputMerger(ArrayCreatingInputMerger::class.java)
                                .build()
                )
            } else {
                WorkManager.getInstance().beginWith(w)
                        .then(s)
                        .enqueue()
            }
        }
    }
}
//5c2623d2b2c55c00096a8571