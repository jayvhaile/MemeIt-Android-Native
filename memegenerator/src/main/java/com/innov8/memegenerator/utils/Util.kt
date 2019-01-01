package com.innov8.memegenerator.utils

import android.graphics.*
import android.graphics.Bitmap.CompressFormat.PNG
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.core.graphics.withRotation
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.memeit.backend.models.GridImageLayoutProperty
import com.memeit.backend.models.LayoutProperty
import com.memeit.backend.models.LinearImageLayoutProperty
import com.memeit.backend.models.SingleImageLayoutProperty
import com.memeit.backend.utils.RuntimeTypeAdapterFactory
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

fun Bitmap.addWaterMark(tf: Typeface): Bitmap {
    return this.copy(Bitmap.Config.ARGB_8888, true).apply {
        Canvas(this).apply {
            val w = width.toFloat()
            val h = height.toFloat()
            withRotation(-90f, w / 2, h / 2) {
                val offset = (w - h) / 2
                val av = (h + w) / 2
                translate(0f, offset)
                val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#ccffffff")
                    textSize = 0.045f * av
                    typeface = tf
                    this.setShadowLayer(0.025f * av, 5f, 5f, Color.parseColor("#cc000000"))

                }
                val a = paint.fontMetrics.descent
                drawText("MemeItApp.com", offset + 10, h - a, paint)
            }
        }
    }
}

infix fun Pair<Float, Float>.maxBy(max: Float): Pair<Float, Float> {
    if (first > second) {
        if (first <= max) return this

        val x = (max * second) / first
        return max to x
    } else {
        if (second <= max) return this

        val x = (max * first) / second
        return x to max
    }
}

fun RectF.scale(x: Float, y: Float) = RectF(left * x, top * y, right * x, bottom * y)

fun calcReqSize(lp: LayoutProperty, count: Int): Pair<Int, Int> {
    val maxW = 400
    val maxH = 800

    val w = when (lp) {
        is SingleImageLayoutProperty -> maxW
        is LinearImageLayoutProperty -> if (lp.orientation == 0) maxW / count else maxW / 2
        is GridImageLayoutProperty -> if (lp.orientation == 0) maxW / lp.span else maxW / (count / lp.span)
    }
    val h = when (lp) {
        is SingleImageLayoutProperty -> maxH
        is LinearImageLayoutProperty -> if (lp.orientation == 0) maxH / 2 else maxH / count
        is GridImageLayoutProperty -> if (lp.orientation == 0) maxH / (count / lp.span) else maxH / lp.span
    }

    return w to h
}

fun Pair<Int, Int>.fitCenter(w: Int, h: Int) = (first.toFloat() to second.toFloat()).fitCenter(RectF(0f, 0f, w.toFloat(), h.toFloat()))

fun Pair<Float, Float>.fitCenter(w: Float, h: Float) = fitCenter(RectF(0f, 0f, w, h))
fun Pair<Float, Float>.fitCenter(rect: RectF): RectF {
    val w: Float
    val h: Float
    val x: Float
    val y: Float
    val cw = rect.width()
    val ch = rect.height()
    val ir = first / second
    val cr = cw / ch
    if (ir < cr) {  //fit the height and adjust the width
        val hr = ch / second
        w = first * hr
        h = ch
        x = rect.left + (cw / 2.0f) - (w / 2.0f)
        y = rect.top
    } else {//fit the width and adjust the height
        val wr = cw / first
        w = cw
        h = second * wr
        x = rect.left
        y = rect.top + (ch / 2f) - (h / 2f)
    }
    return RectF(x, y, x + w, y + h)
}

fun RectF.padded(leftP: Float = 0f, topP: Float = 0f, rightP: Float = 0f, bottomP: Float = 0f): RectF {
    return RectF(left + leftP, top + topP, right - rightP, bottom - bottomP)
}

fun RectF.margined(leftP: Float = 0f, topP: Float = 0f, rightP: Float = 0f, bottomP: Float = 0f): RectF {
    return RectF(left - leftP, top - topP, right + rightP, bottom + bottomP)
}
