package com.memeit.backend.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.memeit.backend.utils.generateFactory
import java.io.File
import java.io.FileInputStream


sealed class Sticker(val name: String) {
    abstract fun load(context: Context): Bitmap
    abstract fun getUrl(context: Context): String
    companion object {
        fun getRuntimeTypeAdapterFactory() =
                generateFactory(Sticker::class.java,
                        listOf(
                                PreShippedSticker::class.java,
                                UserSticker::class.java
                        ))
    }

}

class PreShippedSticker(name: String, val packPath: String) : Sticker(name) {
    override fun getUrl(context: Context): String {
        return "asset:///$packPath/$name"
    }

    override fun load(context: Context): Bitmap {
        return BitmapFactory.decodeStream(context.assets.open("$packPath/$name"))
    }

}

class UserSticker(name: String) : Sticker(name) {
    override fun getUrl(context: Context): String {
        return Uri.fromFile(File(myStickersDir(context), name)).toString()
    }

    override fun load(context: Context): Bitmap {
        return BitmapFactory.decodeStream(FileInputStream(File(myStickersDir(context), name)))
    }

    companion object {
        fun myStickersDir(context: Context): File {
            return File(context.filesDir, "stickers/mine/").apply {
                mkdirs()
            }
        }
    }
}