package com.innov8.memeit.Models

import com.memeit.backend.models.SavedMemeTemplateProperty
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import java.io.File

data class Draft(val filePath: String, val savedMemeTemplateProperty: SavedMemeTemplateProperty) {

    fun delete() {
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            withContext(Dispatchers.Default) {
                savedMemeTemplateProperty.images.forEach { path ->
                    File(path).takeIf { it.exists() }?.delete()
                }
                File(filePath).takeIf { it.exists() }?.delete()
            }

        }
    }
}