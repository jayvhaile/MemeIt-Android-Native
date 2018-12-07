package com.innov8.memegenerator.MemeEngine

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.waynejo.androidndkgif.GifDecoder
import com.waynejo.androidndkgif.GifEncoder

class GifInfo(val path:String,val rect:RectF)
fun compileGifMeme(gifi:GifInfo,overlayBitmap: Bitmap,dr: RectF, paint: Paint, destPath:String) {
    val decoder = GifDecoder()
    val encoder = GifEncoder()
    val iterator = decoder.loadUsingIterator(gifi.path)
    encoder.init(dr.width().toInt(), dr.height().toInt(), destPath, GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY)
    val b = Bitmap.createBitmap(dr.width().toInt(), dr.height().toInt(), Bitmap.Config.ARGB_8888)
    while (iterator.hasNext()) {
        val gifImage = iterator.next()
        encoder.encodeFrame(draw(b,gifImage.bitmap,dr, paint,gifi.rect, overlayBitmap), gifImage.delayMs)
    }
    encoder.close()
}
private fun draw(b:Bitmap,imageBitmap:Bitmap,dr: RectF, paint: Paint, idr: RectF, overlayBitmap: Bitmap):Bitmap {
    val canvas = Canvas(b)
    canvas.drawRect(dr, paint)
    canvas.drawBitmap(imageBitmap, null, idr, null)
    canvas.drawBitmap(overlayBitmap, null, dr, null)
    return b
}