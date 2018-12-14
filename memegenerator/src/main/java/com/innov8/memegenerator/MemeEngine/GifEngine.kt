package com.innov8.memegenerator.MemeEngine

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.media.MediaMetadataRetriever
import com.innov8.memegenerator.utils.maxBy
import com.innov8.memegenerator.utils.scale
import com.innov8.memeit.commons.log
import com.waynejo.androidndkgif.GifDecoder
import com.waynejo.androidndkgif.GifEncoder

class GifInfo(val path: String, val rect: RectF)

fun compileGifMeme(gifi: GifInfo, overlayBitmap: Bitmap, dr: RectF, paint: Paint, destPath: String) {
    val decoder = GifDecoder()
    val encoder = GifEncoder()
    encoder.setThreadCount(4)
    val iterator = decoder.loadUsingIterator(gifi.path)

    val (w,h) = (dr.width() to dr.height()) maxBy 500f

    val rr=gifi.rect.scale(w/dr.width(),h/dr.height())
    encoder.init(w.toInt(), h.toInt(), destPath, GifEncoder.EncodingType.ENCODING_TYPE_FAST)
    val b = Bitmap.createBitmap(w.toInt(), h.toInt(), Bitmap.Config.ARGB_8888)
    while (iterator.hasNext()) {
        val gifImage = iterator.next()
        val dd = draw(b, gifImage.bitmap, dr, paint,rr , overlayBitmap)
        encoder.encodeFrame(dd, gifImage.delayMs)
    }
    pt("finished")
    encoder.close()
    pt("closed")
}

var ms = 0L
fun pt(message: String = "") {
    val n = System.currentTimeMillis()
    log("fcko", message, n, n - ms)
    ms = n
}
fun compileGifFromVideo(path: String, rect: RectF, overlayBitmap: Bitmap, dr: RectF, paint: Paint, destPath: String) {
    val ret = MediaMetadataRetriever()
    ret.setDataSource(path)

    val dur = ret.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
    val encoder = GifEncoder()

    val (w,h) = (dr.width() to dr.height()) maxBy 500f

    val rr=rect.scale(w/dr.width(),h/dr.height())

    encoder.init(w.toInt(), h.toInt(), destPath, GifEncoder.EncodingType.ENCODING_TYPE_FAST)
    val b = Bitmap.createBitmap(w.toInt(), h.toInt(), Bitmap.Config.ARGB_8888)
    for (i in 0 until dur step 32) {
        val bmp = ret.getFrameAtTime(i*1000)
        encoder.encodeFrame(draw(b, bmp, dr, paint, rr, overlayBitmap), 32)
    }
    encoder.close()
    ret.release()


}

private fun draw(b: Bitmap, imageBitmap: Bitmap, dr: RectF, paint: Paint, idr: RectF, overlayBitmap: Bitmap): Bitmap {
    val canvas = Canvas(b)
    canvas.drawRect(dr, paint)
    canvas.drawBitmap(imageBitmap, null, idr, null)
    canvas.drawBitmap(overlayBitmap, null, dr, null)
    return b
}