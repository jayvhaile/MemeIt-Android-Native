package com.innov8.memegenerator.utils

import android.content.Context
import android.graphics.Bitmap
import com.innov8.memeit.commons.loadBitmapfromStream
import com.memeit.backend.models.*
import com.waynejo.androidndkgif.GifDecoder
import java.io.File
import java.io.FileInputStream


fun SavedMemeTemplateProperty.loadImages(context: Context): LoadedMemeTemplateProperty {
    val (w, h) = calcReqSize(layoutProperty, images.size)
    return when (this) {
        is SavedImageMemeTemplateProperty -> LoadedImageMemeTemplateProperty(
                layoutProperty,
                memeItemsProperty,
                images.map { loadImage(context, it, w, h) }
        )
        is SavedGifMemeTemplateProperty -> LoadedGifMemeTemplateProperty(
                layoutProperty as SingleImageLayoutProperty,
                memeItemsProperty,
                loadGif(images[0]),
                images[0])

    }
}

private fun loadImage(context: Context, path: String, w: Int, h: Int): Bitmap {
    return context.loadBitmapfromStream(FileInputStream(path), w, h)!!
}

private fun loadGif(path: String): Bitmap {
    val x = GifDecoder()
    val i = x.loadUsingIterator(path)
    i.hasNext()
    return i.next().bitmap
}

fun LoadedMemeTemplateProperty.saveImages(dir: File): SavedMemeTemplateProperty {
    return when (this) {
        is LoadedImageMemeTemplateProperty -> {
            val urls = images.map { it.save(dir, 90, 1000) }.toMutableList()
            SavedImageMemeTemplateProperty(
                    layoutProperty,
                    memeItemsProperty,
                    urls,
                    previewImageBitmap!!.save(dir, 75, 500)
            )
        }
        is LoadedGifMemeTemplateProperty -> SavedGifMemeTemplateProperty(
                layoutProperty as SingleImageLayoutProperty,
                memeItemsProperty,
                originalPath,
                previewImageBitmap!!.save(dir, 75, 500)
        )
    }
}

fun LoadedMemeTemplateProperty.asLoadedGifMemeTemplateProperty(gifPath: String): LoadedGifMemeTemplateProperty {
    return LoadedGifMemeTemplateProperty(layoutProperty as SingleImageLayoutProperty, memeItemsProperty, images[0], gifPath, previewImageBitmap)
}


