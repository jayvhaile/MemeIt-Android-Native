package com.innov8.memegenerator.Models

import android.content.Context
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

class StickerPack(val name: String, val urls: List<String>) {
    companion object {
        private val stickers = mapOf("Emojis" to "emoji_stickers",
                "Meme Faces" to "meme_stickers",
                "Chat Bubbles" to "bubbles")

        fun load(context: Context, onLoaded: (List<StickerPack>) -> Unit) {
            launch(UI) {
               onLoaded(withContext(CommonPool){
                   val list = MutableList(stickers.size) { index: Int ->
                       val (name, path) = stickers.toList()[index]
                       val urls = context.assets.list(path)?.map { "asset:///$path/$it" }
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