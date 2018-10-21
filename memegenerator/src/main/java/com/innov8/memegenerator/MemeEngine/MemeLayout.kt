package com.innov8.memegenerator.MemeEngine

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RectF
import android.os.Parcel
import android.os.Parcelable

abstract class MemeLayout(var maxWidth: Int, var maxHeight: Int, val images: List<Bitmap>) {




    class LayoutInfo(var type:Int) : Parcelable {

        var span: Int = 2
        var spacing: Int = 20
        var orientation: Int = 0

        constructor(parcel: Parcel) : this(parcel.readInt()) {
            span = parcel.readInt()
            spacing = parcel.readInt()
            orientation = parcel.readInt()
        }


        fun create(maxWidth: Int,maxHeight: Int,images:List<Bitmap>):MemeLayout{
            return when(type){
                TYPE_SINGLE->SingleImageLayout(maxWidth,maxHeight,images[0])
                TYPE_LINEAR->LinearImageLayout(spacing, orientation,maxWidth,maxHeight,images)
                TYPE_GRID->GridImageLayout(span, orientation, spacing,maxWidth,maxHeight,images)
                else->throw IllegalStateException("illegal meme layout type")
            }

        }

        companion object CREATOR : Parcelable.Creator<LayoutInfo> {
            const val TYPE_SINGLE:Int=0
            const val TYPE_LINEAR:Int=1
            const val TYPE_GRID:Int=2
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
            parcel.writeInt(spacing)
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
        get() = super.horizontalMargin + if (orientation == HORIZONTAL) spacing else 0
    override val verticalMargin: Int
        get() = super.verticalMargin + if (orientation == VERTICAL) spacing else 0


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

                val availableTotalHeight = drawingRect.height() - verticalMargin
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

class GridImageLayout(spn: Int, orien: Int, spc: Int, maxWidth: Int, maxHeight: Int, images: List<Bitmap>) : MemeLayout(maxWidth, maxHeight, images) {
    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }

    var span = spn

    var hSpacing = spc
        set(value) {
            field = value
            calcImagesTotalWidth()
            update()
        }

    var vSpacing = spc
        set(value) {
            field = value
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
        calcImagesTotalHeight()
        calcImagesTotalWidth()
        update()
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

    fun getImagesInRow(row: Int): List<Bitmap> {

        return when (orientation) {
            HORIZONTAL -> {
                images.subList(row * columnCount, ((row + 1) * columnCount) max count)
            }
            VERTICAL -> {
                val l = mutableListOf<Bitmap>()
                for (i in row until count step span) {
                    l.add(images[i])
                }
                return l
            }
            else -> throw IllegalStateException("Orientation should be 0 or 1")
        }
    }

    fun getImagesInColumn(column: Int): List<Bitmap> {
        return when (orientation) {
            HORIZONTAL -> {
                val l = mutableListOf<Bitmap>()
                for (i in column until count step span) {
                    l.add(images[i])
                }
                return l
            }
            VERTICAL -> {
                images.subList(column * rowCount, ((column + 1) * rowCount) max count)
            }
            else -> throwError()
        }
    }

    fun throwError(): Nothing = throw IllegalStateException("Orientation should be 0 or 1")
    infix fun Int.max(max: Int) = if (this < max) this else max


    fun calcImagesTotalWidth() {
        totalIW = (0 until columnCount).map { getColumnWidth(it) }.reduce { a, b -> a + b }
    }

    private fun calcImagesTotalHeight() {
        totalIH = (0 until rowCount).map { getRowHeight(it) }.reduce { a, b -> a + b }
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

    fun getRowHeight(row: Int) = getImagesInRow(row).maxBy { it.height }?.height ?: 0
    fun getColumnWidth(column: Int) = getImagesInColumn(column).maxBy { it.width }?.width ?: 0


    override fun getDrawingRectAt(pos: Int): RectF {

        var l = drawingRect.left + leftMargin
        var t = drawingRect.top + topMargin

        val availableTotalWidth = drawingRect.width() - horizontalMargin
        val availableTotalHeight = drawingRect.height() - verticalMargin
        val scaleX = availableTotalWidth / totalIW
        val scaleY = availableTotalHeight / totalIH

        val (row, column) = getRowAndColumnFor(pos)

        for (i in 0 until column) {
            l += (getColumnWidth(i) * scaleX) + hSpacing
        }
        for (i in 0 until row) {
            t += (getRowHeight(i) * scaleY) + vSpacing
        }

        val rectF = RectF(l, t, l + getColumnWidth(column) * scaleX, t + (getRowHeight(row) * scaleY))
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