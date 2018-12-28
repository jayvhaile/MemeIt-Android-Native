package com.innov8.memegenerator.Models

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main

class StickerPack(val name: String, val urls: List<Sticker>) {
    companion object {
        private val stickers = mapOf("Emojis" to "emoji_stickers",
                "Meme Faces" to "meme_stickers",
                "Chat Bubbles" to "bubbles")

        fun load(context: Context, onLoaded: (List<StickerPack>) -> Unit) {
            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                onLoaded(withContext(Dispatchers.Default) {
                    val list = MutableList(stickers.size) { index: Int ->
                        val (name, path) = stickers.toList()[index]
                        val urls = context.assets.list(path)?.map { Sticker("asset:///$path/$it") }
                                ?: listOf()
                        StickerPack(name, urls)
                    }
                    //todo load my stickers
                    list.add(StickerPack("My Stickers", listOf()))
                    list
                })
            }
        }
    }

}

data class Sticker(val path: String)