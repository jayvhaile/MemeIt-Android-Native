package com.innov8.memeit.Workers

import android.content.Context
import androidx.work.*
import com.innov8.memeit.MemeItApp
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItClient.context
import com.memeit.backend.models.MemeTemplate
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import okhttp3.ResponseBody
import java.io.*
import java.util.*

class TemplateImageDownloader(context: Context, params: WorkerParameters) : Worker(context, params) {
    companion object {
        const val PARAM_URL = "url"
        const val PARAM_IS_PREVIEW = "is preview"
    }

    override fun doWork(): Result {
        val name = inputData.getString(PARAM_URL)!!
        val url = "${MemeItApp.STORAGE_URL}$name"

        val isPreview = inputData.getBoolean(PARAM_IS_PREVIEW, false)
        return try {
            MemeItClient.fileService.downloadObject(url).execute().run {
                if (isSuccessful) {
                    val dir = File(applicationContext.filesDir, "templates")
                    dir.mkdirs()
                    val file = File(dir, "${UUID.randomUUID()}.${File(url).extension}")
                    if (saveResponseBodyToFile(body()!!, file)) {
                        outputData = Data.Builder()
                                .putString(if (isPreview) TemplateSaver.PARAM_PREVIEW_URL
                                else TemplateSaver.PARAM_URLS,
                                        file.absolutePath)
                                .build()
                        Result.SUCCESS
                    } else Result.RETRY
                } else Result.RETRY
            }
        } catch (e: IOException) {
            Result.RETRY
        } catch (e: RuntimeException) {
            Result.FAILURE
        }
    }

    private fun saveResponseBodyToFile(responseBody: ResponseBody, file: File): Boolean {
        var input: InputStream? = null
        var output: OutputStream? = null
        return try {
            input = responseBody.byteStream()
            output = FileOutputStream(file)
            val byteArray = ByteArray(4096)
            while (true) {
                val read = input.read(byteArray)
                if (read == -1) break
                output.write(byteArray, 0, read)
            }
            output.flush()
            true
        } catch (e: IOException) {
            false
        } finally {
            input?.close()
            output?.close()
        }

    }
}

class TemplateSaver(context: Context, params: WorkerParameters) : Worker(context, params) {
    companion object {
        const val PARAM_URLS = "urls"
        const val PARAM_PREVIEW_URL = "preview url"
    }

    override fun doWork(): Result {
        val localUrls = inputData.getStringArray(PARAM_URLS)!!
        val localpreviewUrl = inputData.getStringArray(PARAM_PREVIEW_URL)!![0]
        val id = inputData.getStringArray("id")!![0]

        val temp = File(applicationContext.filesDir, "templates/temp/download/$id.json")

        return if (temp.exists()) {
            MemeTemplate.readFromFile(temp)?.let {
                it.memeTemplateProperty.apply {
                    images.clear()
                    images.addAll(localUrls)
                    previewImageUrl = localpreviewUrl
                }
                val dir = File(applicationContext.filesDir, "templates/json")
                dir.mkdirs()
                it.saveToFile(File(dir, "$id.json"))
                temp.delete()
                Result.SUCCESS
            } ?: Result.FAILURE
        } else
            Result.FAILURE
    }
}

fun startTemplateDownloadWork(template: MemeTemplate) {
    val id = template.id!!
    val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    val w = template.memeTemplateProperty.images.map {
        OneTimeWorkRequest.Builder(TemplateImageDownloader::class.java)
                .setConstraints(constraints)
                .addTag("template image download")
                .addTag("template download")
                .addTag(id)
                .setInputData(Data.Builder().putString(TemplateImageDownloader.PARAM_URL, it).build())
                .build()
    }.toMutableList().apply {

    }
    val s = OneTimeWorkRequest.Builder(TemplateSaver::class.java)
            .addTag("template saver")
            .addTag("template download")
            .addTag(id)
            .setInputMerger(ArrayCreatingInputMerger::class.java)
            .setInputData(Data.Builder().putString("id", id).build())
            .build()

    val dir = File(context.filesDir, "templates/temp/download/")
    dir.mkdirs()
    val file = File(dir, "${template.id!!}.json")
    GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
        withContext(Dispatchers.Default) {
            template.saveToFile(file)
        }
        WorkManager.getInstance().beginUniqueWork(id, ExistingWorkPolicy.KEEP, w)
                .then(s)
                .enqueue()
    }


}