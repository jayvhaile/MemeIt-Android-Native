package com.innov8.memegenerator.workers

import android.content.Context
import androidx.work.*
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
    }

    override fun doWork(): Result {
        val name = inputData.getString(PARAM_URL)!!
        val url = "${MemeItClient.STORAGE_URL}$name"
        return try {
            MemeItClient.fileService.downloadObject(url).execute().run {
                if (isSuccessful) {

                    val file = File(MemeTemplate.getSavedDir(applicationContext), "${UUID.randomUUID()}.${File(url).extension}")
                    if (saveResponseBodyToFile(body()!!, file)) {
                        outputData = Data.Builder()
                                .putString(TemplateSaver.PARAM_URLS, file.absolutePath)
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
    }

    override fun doWork(): Result {
        val localUrls = inputData.getStringArray(PARAM_URLS)!!
        val id = inputData.getStringArray("id")!![0]

        val temp = File(MemeTemplate.getTempDownloadJsonDir(applicationContext), "$id.json")

        return if (temp.exists()) {
            MemeTemplate.readFromFile(temp)?.let {
                it.memeTemplateProperty.apply {
                    images.clear()
                    images.addAll(localUrls)
                }
                it.saveToFile(File(MemeTemplate.getSavedJsonDir(applicationContext), "$id.json"))
                temp.delete()
                Result.SUCCESS
            } ?: Result.FAILURE
        } else
            Result.FAILURE
    }
}

fun startTemplateDownloadWork(template: MemeTemplate) {
    val id = template._id!!
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
    }
    val s = OneTimeWorkRequest.Builder(TemplateSaver::class.java)
            .addTag("template saver")
            .addTag("" +
                    "template download")
            .addTag(id)
            .setInputMerger(ArrayCreatingInputMerger::class.java)
            .setInputData(Data.Builder().putString("id", id).build())
            .build()


    val file = File(MemeTemplate.getTempDownloadJsonDir(context), "${template._id!!}.json")
    GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
        withContext(Dispatchers.Default) {
            template.saveToFile(file)
        }
        WorkManager.getInstance().beginUniqueWork(id, ExistingWorkPolicy.KEEP, w)
                .then(s)
                .enqueue()
    }


}