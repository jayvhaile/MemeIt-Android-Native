package com.innov8.memeit.loaders

import android.content.Context
import com.google.gson.stream.JsonReader
import com.innov8.memeit.MemeItApp
import com.innov8.memeit.commons.Loader
import com.innov8.memeit.models.Draft
import com.memeit.backend.models.MemeTemplate
import com.memeit.backend.models.SavedMemeTemplateProperty
import com.memeit.backend.models.buildGson
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import java.io.FileReader

class DraftLoader : Loader<Draft> {
    private val context: Context
        get() = MemeItApp.instance
    override var skip: Int = 0
    override fun load(limit: Int, onSuccess: (List<Draft>) -> Unit, onError: (String) -> Unit) {
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {

            val result = withContext(Dispatchers.Default) {
                val files = MemeTemplate.getDraftsJsonDir(context)
                        .takeIf { it.exists() }
                        ?.listFiles()
                        ?.takeIf { it.isNotEmpty() }

                files?.mapNotNull {
                    val jr = JsonReader(FileReader(it))
                    val sp = buildGson().fromJson<SavedMemeTemplateProperty>(jr, SavedMemeTemplateProperty::class.java)
                    jr.close()
                    sp?.run { Draft(it.absolutePath, this) }
                } ?: listOf()
            }
            onSuccess(result)
        }
    }
}