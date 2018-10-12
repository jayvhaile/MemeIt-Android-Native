package com.innov8.memegenerator.MemeEngine

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RectF

abstract class MemeLayout(val maxWidth: Int, val maxHeight: Int, val images: List<Bitmap>) {

    var invalidate: (() -> Unit)? = null
    lateinit var drawingRect: RectF

    var leftMargin: Int = 0
        set(value) {
            field = value
            update()
        }
    var topMargin: Int = 0
        set(value) {
            field = value
            update()
        }
    var rightMargin: Int = 0
        set(value) {
            field = value
            update()
        }
    var bottomMargin: Int = 0
        set(value) {
            field = value
            update()
        }
    var backgroudColor: Int = Color.BLACK
        set(value) {
            field = value
            invalidate?.invoke()
        }

    fun setMargin(left: Int, top: Int = left, right: Int = left, bottom: Int = left) {
        leftMargin = left
        topMargin = top
        rightMargin = right
        bottomMargin = bottom
    }


    internal fun update() {
        val w: Float
        val h: Float
        val x: Float
        val y: Float


        val iw = innerWidth().toFloat()
        val ih = innerHeight().toFloat()
        val tw = maxWidth.toFloat()
        val th = maxHeight.toFloat()
        val cw = tw - horizontalMargin
        val ch = th - verticalMargin

        val ir = iw / ih
        val cr = cw / ch

        if (ir < cr) {
            val hr = ch / ih
            w = iw * hr + horizontalMargin
            h = ch + verticalMargin
            x = (tw / 2.0f) - (w / 2.0f)
            y = 0f
        } else {
            val wr = cw / iw
            w = cw + horizontalMargin
            h = ih * wr + verticalMargin
            x = 0f
            y = (th / 2f) - (h / 2f)
        }
        drawingRect = RectF(x, y, x + w, y + h)
        invalidate?.invoke()
    }

    open val horizontalMargin: Int
        get() = leftMargin + rightMargin
    open val verticalMargin: Int
        get() = topMargin + bottomMargin
    fun getCount(): Int = images.size

    protected abstract fun innerWidth(): Int
    protected abstract fun innerHeight(): Int

    abstract fun getDrawingRectAt(pos: Int): RectF

}

class SingleImageLayout(maxWidth: Int, maxHeight: Int, bitmap: Bitmap) : MemeLayout(maxWidth, maxHeight, listOf(bitmap)) {

    init {
        update()
    }

    override fun innerWidth(): Int = images[0].width


    override fun innerHeight(): Int = images[0].height


    override fun getDrawingRectAt(pos: Int): RectF {
        if (pos >= getCount()) throw ArrayIndexOutOfBoundsException("pos must be 0")

        val l = drawingRect.left + leftMargin
        val t = drawingRect.top + topMargin
        val r = drawingRect.right - rightMargin
        val b = drawingRect.bottom - bottomMargin
        return RectF(l, t, r, b)
    }

    fun getDrawingRectRelAt(pos: Int): RectF {
        if (pos >= getCount()) throw ArrayIndexOutOfBoundsException("pos must be 0")

        val l = leftMargin.toFloat()
        val t = topMargin.toFloat()
        val r = drawingRect.right - rightMargin - drawingRect.left
        val b = drawingRect.bottom - bottomMargin - drawingRect.top
        return RectF(l, t, r, b)
    }

}

class LinearImageLayout(spc: Int, orien: Int, maxWidth: Int, maxHeight: Int, images: List<Bitmap>) : MemeLayout(maxWidth, maxHeight, images) {
    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }

    var spacing = spc
        set(value) {
            field = value
            calcImagesTotalWidth()
            calcImagesTotalHeight()
            update()
        }
    var orientation = orien
        set(value) {
            field = value
            calcImagesTotalWidth()
            calcImagesTotalHeight()
            update()
        }

    init {
        calcImagesMaxHeight()
        calcImagesMaxWidth()
        calcImagesTotalHeight()
        calcImagesTotalWidth()
        update()
    }


    private val totalSpacing
        get() = (images.size - 1) * spacing
    private var totalIW = 0
    private var totalIH = 0
    private var maxIW = 0
    private var maxIH = 0
    private fun calcImagesTotalWidth() {
        totalIW = images.asSequence()
                .map { it.width }
                .reduce { a, b -> a + b }
    }

    private fun calcImagesTotalHeight() {
        totalIH = images.asSequence()
                .map { it.height }
                .reduce { a, b -> a + b }
    }

    private fun calcImagesMaxWidth() {
        maxIW = images.maxBy { it.width }?.width ?: 0
    }

    private fun calcImagesMaxHeight() {
        maxIH = images.maxBy { it.height }?.height ?: 0
    }

    override fun innerWidth(): Int {
        return when (orientation) {
            HORIZONTAL -> totalIW
            VERTICAL -> maxIW
            else -> 0
        }
    }

    override fun innerHeight(): Int {
        return when (orientation) {
            HORIZONTAL -> maxIH
            VERTICAL -> totalIH
            else -> 0
        }
    }

    override val horizontalMargin: Int
        get() = super.horizontalMargin +if (orientation== HORIZONTAL)spacing else 0
    override val verticalMargin: Int
        get() = super.verticalMargin+if (orientation== VERTICAL)spacing else 0




    override fun getDrawingRectAt(pos: Int): RectF {
        when (orientation) {
            HORIZONTAL -> {
                var l = drawingRect.left + leftMargin
                val t = drawingRect.top + topMargin
                val b = drawingRect.bottom - bottomMargin

                val availableTotalWidth = drawingRect.width() - horizontalMargin
                val scaleX = availableTotalWidth / (totalIW)

                for (i in 0 until pos) {
                    l += (images[i].width * scaleX) + spacing
                }
                val rectF = RectF(l, t, l + (images[pos].width * scaleX), b)
                val w = images[pos].width.toFloat()
                val h = images[pos].height.toFloat()
                return (w to h).fitCenter(rectF)
            }
            VERTICAL -> {
                val l = drawingRect.left + leftMargin
                var t = drawingRect.top + topMargin
                val r = drawingRect.right - rightMargin

                val availableTotalHeight = drawingRect.height() - verticalMargin - totalSpacing
                val scaleY = availableTotalHeight / totalIH

                for (i in 0 until pos) {
                    t += (images[i].height * scaleY) + spacing
                }
                val rectF = RectF(l, t, r, t + (images[pos].height * scaleY))
                val w = images[pos].width.toFloat()
                val h = images[pos].height.toFloat()
                return (w to h).fitCenter(rectF)
            }
        }
        return RectF()
    }
}


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