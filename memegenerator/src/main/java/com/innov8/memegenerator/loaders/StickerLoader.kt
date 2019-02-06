package com.innov8.memegenerator.loaders

import android.content.Context
import com.memeit.backend.models.PreShippedSticker
import com.memeit.backend.models.Sticker
import com.memeit.backend.models.UserSticker
import com.innov8.memeit.commons.Loader
import kotlinx.coroutines.*

interface StickerLoader : Loader<Sticker>

class PreShippedStickersLoader(var name: String, val context: Context) : StickerLoader {
    override var skip: Int = 0

    override fun load(limit: Int, onSuccess: (List<Sticker>) -> Unit, onError: (String) -> Unit) {
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            withContext(Dispatchers.Default) {
                context.assets.list(name)?.map { PreShippedSticker(it, name) }
            }?.let(onSuccess) ?: onError("Sticker path not found")
        }
    }
}

class UserStickersLoader(val context: Context) : StickerLoader {
    override var skip: Int = 0

    override fun load(limit: Int, onSuccess: (List<Sticker>) -> Unit, onError: (String) -> Unit) {
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            withContext(Dispatchers.Default) {
                UserSticker.myStickersDir(context).list().map { UserSticker(it) }.reversed()
            }.let(onSuccess)
        }
    }
}