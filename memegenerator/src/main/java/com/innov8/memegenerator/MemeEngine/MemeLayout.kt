package com.innov8.memegenerator.MemeEngine

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RectF
import android.os.Parcel
import android.os.Parcelable

abstract class MemeLayout(var maxWidth: Int, var maxHeight: Int, val images: List<Bitmap>) {


    class LayoutInfo(var type: Int, var span: Int = 2, var orientation: Int = 0) : Parcelable {


        constructor(parcel: Parcel) : this(
                parcel.readInt(),
                parcel.readInt(),
                parcel.readInt())

        fun create(maxWidth: Int, maxHeight: Int, images: List<Bitmap>): MemeLayout {
            return when (type) {
                TYPE_SINGLE -> SingleImageLayout(maxWidth, maxHeight, images[0])
                TYPE_LINEAR -> LinearImageLayout(orientation, maxWidth, maxHeight, images)
                TYPE_GRID -> GridImageLayout(span, orientation, maxWidth, maxHeight, images)
                else -> throw IllegalStateException("illegal meme layout type")
            }

        }

        companion object CREATOR : Parcelable.Creator<LayoutInfo> {
            const val TYPE_SINGLE: Int = 0
            const val TYPE_LINEAR: Int = 1
            const val TYPE_GRID: Int = 2
            override fun createFromParcel(parcel: Parcel): LayoutInfo {
                return LayoutInfo(parcel)
            }

            override fun newArray(size: Int): Array<LayoutInfo?> {
                return arrayOfNulls(size)
            }
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(type)
            parcel.writeInt(span)
            parcel.writeInt(orientation)
        }

        override fun describeContents(): Int {
            return 0
        }

    }

    var invalidate: (() -> Unit)? = null
    lateinit var drawingRect: RectF

    fun updateSize(w: Int, h: Int) {
        maxWidth = w
        maxHeight = h
        update()
    }

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

    val count: Int
        get() = images.size

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
        if (pos >= count) throw ArrayIndexOutOfBoundsException("pos must be 0")

        val l = drawingRect.left + leftMargin
        val t = drawingRect.top + topMargin
        val r = drawingRect.right - rightMargin
        val b = drawingRect.bottom - bottomMargin
        return RectF(l, t, r, b)
    }

    fun getDrawingRectRelAt(pos: Int): RectF {
        if (pos >= count) throw ArrayIndexOutOfBoundsException("pos must be 0")

        val l = leftMargin.toFloat()
        val t = topMargin.toFloat()
        val r = drawingRect.right - rightMargin - drawingRect.left
        val b = drawingRect.bottom - bottomMargin - drawingRect.top
        return RectF(l, t, r, b)
    }

}

class LinearImageLayout(orien: Int, maxWidth: Int, maxHeight: Int, images: List<Bitmap>) : MemeLayout(maxWidth, maxHeight, images) {
    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }

    var spacing = 0
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
        calcImagesAverageWidth()
        calcImagesAverageHeight()
        calcImagesTotalHeight()
        calcImagesTotalWidth()
        update()
    }


    private val totalSpacing
        get() = (images.size - 1) * spacing
    private var totalIW = 0
    private var totalIH = 0
    private var averageIW = 0
    private var averageIH = 0
    private fun calcImagesTotalWidth() {
        totalIW = images.asSequence()
                .map {
                    averageIH * it.width / it.height
                }.reduce { a, b -> a + b }
    }

    private fun calcImagesTotalHeight() {
        totalIH = images.asSequence()
                .map {
                    averageIW * it.height / it.width
                }
                .reduce { a, b -> a + b }
    }


    private fun calcImagesAverageWidth() {
        averageIW = images.map { it.width }.average().toInt()
    }

    private fun calcImagesAverageHeight() {
        averageIH = images.map { it.height }.average().toInt()
    }

    override fun innerWidth(): Int {
        return when (orientation) {
            HORIZONTAL -> totalIW
            VERTICAL -> averageIW
            else -> 0
        }
    }

    override fun innerHeight(): Int {
        return when (orientation) {
            HORIZONTAL -> averageIH
            VERTICAL -> totalIH
            else -> 0
        }
    }

    override val horizontalMargin: Int
        get() = super.horizontalMargin + if (orientation == HORIZONTAL) totalSpacing else 0
    override val verticalMargin: Int
        get() = super.verticalMargin + if (orientation == VERTICAL) totalSpacing else 0


    override fun getDrawingRectAt(pos: Int): RectF {
        when (orientation) {
            HORIZONTAL -> {
                var l = drawingRect.left + leftMargin
                val t = drawingRect.top + topMargin
                val b = drawingRect.bottom - bottomMargin


                val hh = b - t

                for (i in 0 until pos) {
                    val iw = hh * images[i].width / images[i].height
                    l += iw + spacing
                }
                val rectF = RectF(l, t, l + (hh * images[pos].width / images[pos].height), b)
                val w = images[pos].width.toFloat()
                val h = images[pos].height.toFloat()
                return (w to h).fitCenter(rectF)
            }
            VERTICAL -> {
                val l = drawingRect.left + leftMargin
                var t = drawingRect.top + topMargin
                val r = drawingRect.right - rightMargin

                val ww = r - l
                for (i in 0 until pos) {
                    val ih = ww * images[i].height / images[i].width
                    t += ih + spacing
                }
                val rectF = RectF(l, t, r, t + (ww * images[pos].height / images[pos].width))
                val w = images[pos].width.toFloat()
                val h = images[pos].height.toFloat()
                return (w to h).fitCenter(rectF)
            }
        }
        return RectF()
    }
}

class GridImageLayout(var span: Int, orientation: Int, maxWidth: Int, maxHeight: Int, images: List<Bitmap>) : MemeLayout(maxWidth, maxHeight, images) {
    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }

    var hSpacing = 0
        set(value) {
            field = value
            calcImagesTotalWidth()
            update()
        }

    var vSpacing = 0
        set(value) {
            field = value
            calcImagesTotalHeight()
            update()
        }
    var orientation = orientation
        set(value) {
            field = value
            calcImagesTotalWidth()
            calcImagesTotalHeight()
            update()
        }

    init {
        calcImagesAverageWidth()
        calcImagesAverageHeight()
        calcImagesTotalHeight()
        calcImagesTotalWidth()
        update()
    }


    private var averageIW = 0
    private var averageIH = 0
    private fun calcImagesAverageWidth() {
        averageIW = images.map { it.width }.average().toInt()
    }

    private fun calcImagesAverageHeight() {
        averageIH = images.map { it.height }.average().toInt()
    }


    private val totalHSpacing
        get() = (columnCount - 1) * hSpacing

    private val totalVSpacing
        get() = (rowCount - 1) * vSpacing

    private var totalIW = 0
    private var totalIH = 0

    val rowCount: Int
        get() = when (orientation) {
            HORIZONTAL -> Math.ceil(count / span.toDouble()).toInt()
            VERTICAL -> span
            else -> 0
        }

    val columnCount: Int
        get() = when (orientation) {
            HORIZONTAL -> span
            VERTICAL -> Math.ceil(count / span.toDouble()).toInt()
            else -> 0
        }


    fun throwError(): Nothing = throw IllegalStateException("Orientation should be 0 or 1")
    infix fun Int.max(max: Int) = if (this < max) this else max


    fun calcImagesTotalWidth() {
        totalIW = averageIW * columnCount
    }

    private fun calcImagesTotalHeight() {
        totalIH = averageIH * rowCount
    }

    override fun innerWidth(): Int = totalIW

    override fun innerHeight(): Int = totalIH

    override val horizontalMargin: Int
        get() = super.horizontalMargin + totalHSpacing
    override val verticalMargin: Int
        get() = super.verticalMargin + totalVSpacing


    fun getRowAndColumnFor(pos: Int) =
            when (orientation) {
                HORIZONTAL -> pos / span to pos % span
                VERTICAL -> pos % span to pos / span
                else -> throwError()
            }


    override fun getDrawingRectAt(pos: Int): RectF {
        var l = drawingRect.left + leftMargin
        var t = drawingRect.top + topMargin
        val (row, column) = getRowAndColumnFor(pos)

        val availableTotalWidth = drawingRect.width() - horizontalMargin
        val availableTotalHeight = drawingRect.height() - verticalMargin
        val scaleX = availableTotalWidth / totalIW
        val scaleY = availableTotalHeight / totalIH

        l += (averageIW * scaleX + hSpacing) * column
        t += (averageIH * scaleY + vSpacing) * row

        val rectF = RectF(l, t, l + averageIW * scaleX, t + averageIH * scaleY)
        val w = images[pos].width.toFloat()
        val h = images[pos].height.toFloat()
        return (w to h).fitCenter(rectF)

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