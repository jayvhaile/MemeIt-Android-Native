package com.innov8.memegenerator.workers

import android.content.Context
import com.memeit.backend.MemeItClient
import com.memeit.backend.models.MemeTemplate
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import java.io.*

fun startTemplateDownload(context: Context, template: MemeTemplate, onSuccess: (MemeTemplate) -> Unit, onFailure: (String) -> Unit) {
    GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
        try {
            template.memeTemplateProperty.images.map {
                async(Dispatchers.IO) { downloadImage(context, it) }
            }.map {
                it.await()
            }.requireNoNulls().let { savedImages ->
                template.memeTemplateProperty.apply {
                    images.clear()
                    images.addAll(savedImages)
                }
                template.saveToFile(File(MemeTemplate.getSavedJsonDir(context), "${template._id!!}.json"))
                onSuccess(template)
            }
        } catch (e: IllegalArgumentException) {
            onFailure("Failed downloading image")
        }
    }
}

private fun downloadImage(context: Context, name: String): String? {
    val url = "${MemeItClient.STORAGE_URL}$name"
    return try {
        val file = File(MemeTemplate.getSavedDir(context), name)
        if (file.exists())
            file.absolutePath
        else {
            val response = MemeItClient.fileService.downloadObject(url).execute()
            if (response.isSuccessful) {
                if (saveResponseBodyToFile(response.body()!!, file))
                    file.absolutePath
                else
                    null
            } else
                null
        }
    } catch (e: Exception) {
        null
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