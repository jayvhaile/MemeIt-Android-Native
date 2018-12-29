package com.innov8.memegenerator.memeEngine

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.media.MediaMetadataRetriever
import com.innov8.memegenerator.utils.maxBy
import com.innov8.memegenerator.utils.scale
import com.innov8.memeit.commons.log
import com.waynejo.androidndkgif.GifDecoder
import com.innov8.memegenerator.videoProcessors.BitmapToVideoEncoder.IBitmapToVideoEncoderCallback
import com.innov8.memegenerator.videoProcessors.BitmapToVideoEncoder
import java.io.File


class GifInfo(val path: String, val rect: RectF)

@SuppressLint("NewApi")
fun compileGifMeme(gifi: GifInfo, overlayBitmap: Bitmap, dr: RectF, paint: Paint, destPath: String) {
    val decoder = GifDecoder()
    val iterator = decoder.loadUsingIterator(gifi.path)

    val (w, h) = (dr.width() to dr.height()) maxBy 500f
    val rr = gifi.rect.scale(w / dr.width(), h / dr.height())

    val bitmapToVideoEncoder = BitmapToVideoEncoder(object : IBitmapToVideoEncoderCallback {
        override fun onEncodingComplete(outputFile: File?) {
        }
    })
    bitmapToVideoEncoder.startEncoding(w.toInt(), h.toInt(), File(destPath))
    pt("started")
    while (iterator.hasNext()) {
        val gifImage = iterator.next()
        val b = Bitmap.createBitmap(w.toInt(), h.toInt(), Bitmap.Config.ARGB_8888)

        val dd = draw(b, gifImage.bitmap, dr, paint, rr, overlayBitmap)
        pt("encode a")
        bitmapToVideoEncoder.queueFrame(dd)
        pt("encode b")

    }
    pt("finished")
    bitmapToVideoEncoder.stopEncoding()
    pt("closed")
}

var ms = 0L
fun pt(message: String = "") {
    val n = System.currentTimeMillis()
    log("fcko", message, n, n - ms)
    ms = n
}

@SuppressLint("NewApi")
fun compileGifFromVideo(path: String, rect: RectF, overlayBitmap: Bitmap, dr: RectF, paint: Paint, destPath: String) {
    val ret = MediaMetadataRetriever()
    ret.setDataSource(path)

    val dur = ret.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()

    val bitmapToVideoEncoder = BitmapToVideoEncoder(object : IBitmapToVideoEncoderCallback {
        override fun onEncodingComplete(outputFile: File?) {

        }
    })



    val (w, h) = (dr.width() to dr.height()) maxBy 500f

    val rr = rect.scale(w / dr.width(), h / dr.height())

    bitmapToVideoEncoder.startEncoding(w.toInt(), h.toInt(), File(destPath))

    for (i in 0 until dur step 32) {
        val b = Bitmap.createBitmap(w.toInt(), h.toInt(), Bitmap.Config.ARGB_8888)

        val bmp = ret.getFrameAtTime(i * 1000)


        val dd = draw(b, bmp, dr, paint, rr, overlayBitmap)
        bitmapToVideoEncoder.queueFrame(dd)
    }
    ret.release()


}

private fun draw(b: Bitmap, imageBitmap: Bitmap, dr: RectF, paint: Paint, idr: RectF, overlayBitmap: Bitmap): Bitmap {
    val canvas = Canvas(b)
    canvas.drawRect(dr, paint)
    canvas.drawBitmap(imageBitmap, null, idr, null)
    canvas.drawBitmap(overlayBitmap, null, dr, null)
    return b
}