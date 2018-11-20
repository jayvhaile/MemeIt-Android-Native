package com.innov8.memegenerator.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.PNG
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.innov8.memegenerator.R
import com.innov8.memeit.commons.loadBitmap
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import java.io.ByteArrayOutputStream


fun TabLayout.onTabSelected(onSelected: (TabLayout.Tab) -> Unit) {

    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {

        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {

        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            onSelected(tab!!)
        }
    })
}

fun Bitmap.toByteArray(format: Bitmap.CompressFormat = PNG, quality: Int = 100): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(format, quality, stream)
    return stream.toByteArray()
}

fun RectF.enlarge(x: Float, y: Float = x): RectF {
    return RectF(this.left - x, this.top - y, this.right + x, this.bottom + y)
}

fun RectF.origin(): RectF = RectF(0f, 0f, width(), height())
operator fun RectF.contains(event: MotionEvent): Boolean = this.contains(event.x, event.y)
fun RectF.toRect(): Rect {
    return Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
}

fun Rect.toRectF(): RectF {
    return RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
}

fun View.capture(rect: Rect? = null): Bitmap {
    this.isDrawingCacheEnabled = true
    val rb: Bitmap = if (rect != null)
        Bitmap.createBitmap(this.getDrawingCache(true), rect.left, rect.top,
                rect.right - rect.left, rect.bottom - rect.top)
    else
        Bitmap.createBitmap(this.getDrawingCache(true))
    this.destroyDrawingCache()
    this.isDrawingCacheEnabled = false
    return rb
}

fun SeekBar.onProgressChanged(change: (progress: Int, fromUser: Boolean) -> Unit) {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            change(i, b)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {

        }
    })
}

fun IndicatorSeekBar.listener(onSeek: ((SeekParams) -> Unit)? = null,
                              onStart: ((IndicatorSeekBar) -> Unit)? = null,
                              onStop: ((IndicatorSeekBar) -> Unit)? = null) {
    this.onSeekChangeListener = object : OnSeekChangeListener {
        override fun onSeeking(seekParams: SeekParams) {
            onSeek?.invoke(seekParams)
        }

        override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {
            onStart?.invoke(seekBar)
        }

        override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {
            onStop?.invoke(seekBar)
        }
    }

}

fun RecyclerView.initWithGrid(spanCount: Int, orientation: Int = RecyclerView.VERTICAL, rev: Boolean = false) {
    val glm = androidx.recyclerview.widget.GridLayoutManager(this.context, spanCount, orientation, rev)
    this.layoutManager = glm
    this.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
}

fun RecyclerView.initWithStagger(spanCount: Int, orientation: Int = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
    val glm = androidx.recyclerview.widget.StaggeredGridLayoutManager(spanCount, orientation)
    this.layoutManager = glm
    this.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
}

private infix fun Float.min(i: Float): Float = Math.max(this, i)
private infix fun Float.max(i: Float): Float = Math.min(this, i)

fun Bitmap.addWaterMark(context: Context): Bitmap {
    val canvas = Canvas(this)
    val ratio = 100f / 24f
    val w = canvas.width * 0.2f min 150f
    val h = w / ratio
    val watermark = context.loadBitmap(R.drawable.watermark, w.toInt(), h.toInt())
    val endMargin = 8
    val left = canvas.width - w - endMargin
    val top = canvas.height - h - endMargin
    val destRect = RectF(left, top, left + w, top + h)
    canvas.drawBitmap(watermark, null, destRect, null)
    return this
}