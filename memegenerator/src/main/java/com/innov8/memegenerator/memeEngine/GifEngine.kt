package com.innov8.memegenerator.memeEngine

import android.content.Context
import android.graphics.*
import android.net.Uri
import androidx.core.graphics.*
import com.innov8.memegenerator.utils.maxBy
import com.innov8.memegenerator.utils.scale
import com.innov8.memeit.commons.log
import com.waynejo.androidndkgif.GifDecoder
import com.waynejo.androidndkgif.GifEncoder
import java.io.File


data class GifInfo(val gifPath: String, val overlayBitmap: Bitmap, val margin: RectF, val paint: Paint, val destPath: String)

fun recompileGif(srcPath: String, destPath: String) {

    val decoder = GifDecoder()
    val iterator = decoder.loadUsingIterator(srcPath)
    val encoder = GifEncoder()
    encoder.setThreadCount(4)


    var init = false
    var c = 0
    var dur = 0
    while (iterator.hasNext()) {
        val gifImage = iterator.next()
        val image = gifImage.bitmap
        dur += gifImage.delayMs
        if (c++ % 3 != 0) continue
        val w = (image.width)
        val h = (image.height)
        val (ww, hh) = (w to h) maxBy 450f
        val bit = image.scale(ww.toInt(), hh.toInt())
        if (!init) {
            encoder.init(bit.width, bit.height, destPath, GifEncoder.EncodingType.ENCODING_TYPE_SIMPLE_FAST)
            init = true
        }
        encoder.encodeFrame(bit, dur)
        dur = 0
    }

}

fun compileGifMeme(gifInfo: GifInfo) {
    val (gifPath, overlayBitmap, margin, paint, destPath) = gifInfo
    val decoder = GifDecoder()
    val iterator = decoder.loadUsingIterator(gifPath)
    val encoder = GifEncoder()
    encoder.setThreadCount(4)
    var init = false
    var c = 0
    var dur = 0
    while (iterator.hasNext()) {
        val gifImage = iterator.next()
        val image = gifImage.bitmap
        dur += gifImage.delayMs
        if (c++ % 3 != 0) continue
        val (l, t, r, b) = margin.actual(image.width, image.height)
        val w = (l + r + image.width)
        val h = (t + b + image.height)
        val (ww, hh) = (w to h) maxBy 450f
        val bit = Bitmap.createBitmap(ww.toInt(), hh.toInt(), Bitmap.Config.ARGB_8888)
        val idr = RectF(l, t, l + image.width, t + image.height).scale(ww / w, hh / h)
        val dd = draw(bit, gifImage.bitmap, overlayBitmap, idr, paint)
        if (!init) {
            encoder.init(bit.width, bit.height, destPath, GifEncoder.EncodingType.ENCODING_TYPE_SIMPLE_FAST)
            init = true
        }
        encoder.encodeFrame(dd, dur)
        dur = 0
    }

}

/*fun gifToWebp(gifPath: String, overlayBitmap: Bitmap, margin: RectF, paint: Paint, destPath: String) {
    *//*val decoder = GifDecoder()
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
    encoder.close()*//*
}*/


fun RectF.actual(width: Int, height: Int) = RectF(
        width.percent(left),
        height.percent(top),
        width.percent(right),
        height.percent(bottom)
)

fun Int.percent(percent: Float) = (percent / 100f) * this
fun Int.percentOf(total: Float) = (this / total) * 100


var ms = 0L
fun pt(message: String = "") {
    val n = System.currentTimeMillis()
    log("fcko", message, n, n - ms)
    ms = n
}

private fun draw(b: Bitmap, imageBitmap: Bitmap, overlayBitmap: Bitmap, idr: RectF, paint: Paint): Bitmap {
    val canvas = Canvas(b)
    val dr = Rect(0, 0, b.width, b.height)
    canvas.drawRect(dr, paint)
    canvas.drawBitmap(imageBitmap, null, idr, null)
    canvas.drawBitmap(overlayBitmap, null, dr, null)
    return b
}
/*

fun gifToMp4(gifpath: String, destpath: String, context: Context) {
    val fFmpeg = FFmpeg.getInstance(context)
    val a = Uri.fromFile(File(gifpath)).toString()
    val b = Uri.fromFile(File(destpath)).toString()

    fFmpeg.loadBinary(object : FFmpegLoadBinaryResponseHandler {
        override fun onFinish() {

        }

        override fun onSuccess() {
            fFmpeg.execute(arrayOf("ffmpeg -i $a -movflags faststart -pix_fmt yuv420p -vf \"scale=trunc(iw/2)*2:trunc(ih/2)*2\" $b"), object : FFmpegExecuteResponseHandler {
                override fun onFinish() {
                }

                override fun onSuccess(message: String?) {
                }

                override fun onFailure(message: String?) {
                }

                override fun onProgress(message: String?) {
                }

                override fun onStart() {
                }
            })
        }

        override fun onFailure() {
        }

        override fun onStart() {

        }
    })

}*/
