package com.innov8.memeit.Utils

import com.memeit.backend.MemeItClient
import com.memeit.backend.models.MemeTemplate
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import okhttp3.ResponseBody
import java.io.*

fun downloadMemeTemplate(memeTemplate: MemeTemplate) {

    GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
        memeTemplate.memeTemplateProperty.images
                .map {
                    async {
                        MemeItClient.fileService.downloadObject(it).execute()

                    }
                }
                .mapNotNull { it.await() }
                .filter { it.isSuccessful }


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