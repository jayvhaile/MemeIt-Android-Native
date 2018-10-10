package com.innov8.memegenerator.Models

import android.content.Context
import com.innov8.memegenerator.utils.AsyncLoader

class StickerPack(val name: String, val urls: List<String>) {
    companion object {
        private val stickers = mapOf("Emojis" to "emoji_stickers",
                "Meme Faces" to "meme_stickers",
                "Chat Bubbles" to "bubbles")

        fun load(context: Context, onLoaded: (List<StickerPack>) -> Unit) {
            AsyncLoader {
                val list = MutableList(stickers.size) { index: Int ->
                    val (name, path) = stickers.toList()[index]
                    val urls = context.assets.list(path)?.map { "asset:///$path/$it" }
                            ?: listOf()
                    StickerPack(name, urls)
                }
                list.add(StickerPack("My Stickers", listOf()))//todo load my stickers
                list
            }.load(onLoaded)
        }
    }


}