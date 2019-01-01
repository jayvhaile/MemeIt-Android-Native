package com.innov8.memegenerator.memeEngine

import android.annotation.SuppressLint
import android.graphics.*
import android.media.MediaMetadataRetriever
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4
import com.innov8.memegenerator.utils.maxBy
import com.innov8.memegenerator.utils.scale
import com.innov8.memeit.commons.log
import com.waynejo.androidndkgif.GifDecoder
import com.innov8.memegenerator.videoProcessors.BitmapToVideoEncoder.IBitmapToVideoEncoderCallback
import com.innov8.memegenerator.videoProcessors.BitmapToVideoEncoder
import com.innov8.memegenerator.webp.graphics.WebpBitmapEncoder
import kotlinx.coroutines.async
import java.io.File
import java.util.*


class GifInfo(val path: String, val rect: RectF)


fun gifToWebp(gifPath: String, overlayBitmap: Bitmap, margin: RectF, paint: Paint, destPath: String) {
    val decoder = GifDecoder()
    val iterator = decoder.loadUsingIterator(gifPath)
    val encoder = WebpBitmapEncoder(File(destPath))
    encoder.setLoops(0) // 0 = infinity.
    var c = 0
    var dur = 0
    while (iterator.hasNext()) {
        val gifImage = iterator.next()
        val image = gifImage.bitmap
        dur += gifImage.delayMs
        if (c++ % 5 != 0) continue
        val (l, t, r, b) = margin.actual(image.width, image.height)
        val w = (l + r + image.width)
        val h = (t + b + image.height)
        val (ww, hh) = (w to h) maxBy 500f
        val bit = Bitmap.createBitmap(ww.toInt(), hh.toInt(), Bitmap.Config.ARGB_8888)
        val idr = RectF(l, t, l + image.width, t + image.height).scale(ww / w, hh / h)
        val dd = draw(bit, gifImage.bitmap, overlayBitmap, idr, paint)
        encoder.setDuration(dur)
        encoder.writeFrame(dd, 90)
        dur = 0
    }
    encoder.close()
}

fun mp4ToWebp(mp4Path: String, overlayBitmap: Bitmap, margin: RectF, paint: Paint, destPath: String) {
    val ret = MediaMetadataRetriever()
    ret.setDataSource(mp4Path)

    val dur = ret.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()


    val encoder = WebpBitmapEncoder(File(destPath))
    encoder.setLoops(0) // 0 = infinity.

    for (i in 1 until dur step 100) {

        val image = ret.getFrameAtTime(i, MediaMetadataRetriever.OPTION_CLOSEST)
        val (l, t, r, b) = margin.actual(image.width, image.height)
        val w = (l + r + image.width)
        val h = (t + b + image.height)
        val (ww, hh) = (w to h) maxBy 500f
        val bit = Bitmap.createBitmap(ww.toInt(), hh.toInt(), Bitmap.Config.ARGB_8888)
        val idr = RectF(l, t, l + image.width, t + image.height).scale(ww / w, hh / h)
        val dd = draw(bit, image, overlayBitmap, idr, paint)

        encoder.setDuration(100)
        encoder.writeFrame(dd, 90)
        log("qwerty", i)

//        val dd = draw(b, bmp, dr, paint, rr, overlayBitmap)
//        bitmapToVideoEncoder.queueFrame(dd)
    }
    ret.release()


    encoder.close()
}


fun RectF.actual(width: Int, height: Int) = RectF(
        width.percent(left),
        height.percent(top),
        width.percent(right),
        height.percent(bottom)
)

fun Int.percent(percent: Float) = (percent / 100f) * this

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

//        val dd = draw(b, gifImage.bitmap, dr, paint, rr, overlayBitmap)
        pt("encode a")
//        bitmapToVideoEncoder.queueFrame(dd)
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


//        val dd = draw(b, bmp, dr, paint, rr, overlayBitmap)
//        bitmapToVideoEncoder.queueFrame(dd)
    }
    ret.release()


}

private fun draw(b: Bitmap, imageBitmap: Bitmap, overlayBitmap: Bitmap, idr: RectF, paint: Paint): Bitmap {
    val canvas = Canvas(b)
    val dr = Rect(0, 0, b.width, b.height)
    canvas.drawRect(dr, paint)
    canvas.drawBitmap(imageBitmap, null, idr, null)
    canvas.drawBitmap(overlayBitmap, null, dr, null)
    return b
}