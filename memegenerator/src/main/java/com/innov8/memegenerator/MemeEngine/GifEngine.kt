package com.innov8.memegenerator.MemeEngine

import android.graphics.*
import com.innov8.memegenerator.utils.log
import com.waynejo.androidndkgif.GifDecoder
import com.waynejo.androidndkgif.GifEncoder

class GifInfo(val path:String,val rect:RectF)
fun compileGifMeme(gifi:GifInfo,overlayBitmap: Bitmap,dr: RectF, paint: Paint, destPath:String) {


    val decoder = GifDecoder()
    val encoder = GifEncoder()
    val iterator = decoder.loadUsingIterator(gifi.path)

    encoder.init(dr.width().toInt(), dr.height().toInt(), destPath, GifEncoder.EncodingType.ENCODING_TYPE_STABLE_HIGH_MEMORY)
    var c=0
    while (iterator.hasNext()) {
        log("fuuck","count",++c)
        val gifImage = iterator.next()
        encoder.encodeFrame(draw(gifImage.bitmap,dr, paint,gifi.rect, overlayBitmap), gifImage.delayMs)
    }
    encoder.close()
}

private fun draw(imageBitmap:Bitmap,dr: RectF, paint: Paint, idr: RectF, overlayBitmap: Bitmap):Bitmap {
    val b = Bitmap.createBitmap(dr.width().toInt(), dr.height().toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(b)
    canvas.drawRect(dr, paint)
    canvas.drawBitmap(imageBitmap, null, idr, null)
    canvas.drawBitmap(overlayBitmap, null, dr, null)
    return b
}