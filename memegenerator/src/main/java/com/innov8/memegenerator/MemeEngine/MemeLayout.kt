package com.innov8.memegenerator.MemeEngine

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RectF
import com.memeit.backend.models.GridImageLayoutProperty
import com.memeit.backend.models.LayoutProperty
import com.memeit.backend.models.LinearImageLayoutProperty
import com.memeit.backend.models.SingleImageLayoutProperty


abstract class MemeLayout(val images: List<Bitmap>) {
    companion object {
        fun fromProperty(images: List<Bitmap>, layoutProperty: LayoutProperty): MemeLayout {
            return when (layoutProperty) {
                is SingleImageLayoutProperty -> SingleImageLayout(images[0], layoutProperty)
                is LinearImageLayoutProperty -> LinearImageLayout(images, layoutProperty)
                is GridImageLayoutProperty -> GridImageLayout(images, layoutProperty)
            }
        }
    }

    var maxWidth: Int = 0
        set(value) {
            field = value
            update()
        }
    var maxHeight: Int = 0
        set(value) {
            field = value
            update()
        }


    var invalidate: (() -> Unit)? = null
    val drawingRect: RectF by lazy { RectF(0f, 0f, 0f, 0f) }

    protected var lock = false
    protected inline fun withLock(block: () -> Unit) {
        lock = true
        block()
        lock = false
        update()
    }

    fun updateSize(w: Int, h: Int) {
        withLock {
            maxWidth = w
            maxHeight = h
        }
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

    var hr = 1f
    var vr = 1f


    var leftMarginCalc = 0
        get() = ((leftMargin * innerWidth() / 100) * hr).toInt()
    var rightMarginCalc = 0
        get() = ((rightMargin * innerWidth() / 100) * hr).toInt()
    var topMarginCalc = 0
        get() = ((topMargin * innerHeight() / 100) * vr).toInt()
    var bottomMarginCalc = 0
        get() = ((bottomMargin * innerHeight() / 100) * vr).toInt()

    var backgroudColor: Int = Color.WHITE
        set(value) {
            field = value
            invalidate?.invoke()
        }

    fun setMargin(left: Int, top: Int = left, right: Int = left, bottom: Int = left) {
        withLock {
            leftMargin = left
            topMargin = top
            rightMargin = right
            bottomMargin = bottom
        }
    }


    protected fun update() {
        if (lock) return
        if (maxWidth == 0 || maxHeight == 0) return
        val iw = innerWidth().toFloat()
        val ih = innerHeight().toFloat()

        val ww = iw + ((iw * (horizontalMargin)) / 100f)
        val hh = ih + ((ih * (verticalMargin)) / 100f)
        drawingRect.set((ww to hh).fitCenter(maxWidth.toFloat(), maxHeight.toFloat()))
        hr = drawingRect.width() / ww
        vr = drawingRect.height() / hh
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

    abstract fun loadPresets(): Map<String, MemeLayout>
    abstract fun copy(): MemeLayout
    abstract fun generateProperty(): LayoutProperty
}

class ImageLessLayout() : MemeLayout(listOf()) {
    override fun generateProperty(): LayoutProperty {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun copy(): MemeLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadPresets() = mapOf<String, MemeLayout>()

    override fun innerWidth(): Int = 1

    override fun innerHeight(): Int = 1

    override fun getDrawingRectAt(pos: Int): RectF = drawingRect

}

class SingleImageLayout(bitmap: Bitmap) : MemeLayout(listOf(bitmap)) {
    constructor(bitmap: Bitmap, lp: SingleImageLayoutProperty) : this(bitmap) {
        withLock {
            leftMargin = lp.leftMargin
            rightMargin = lp.rightMargin
            topMargin = lp.topMargin
            bottomMargin = lp.bottomMargin
            backgroudColor = lp.bgColor
        }
    }

    override fun loadPresets(): Map<String, MemeLayout> {
        return mapOf(
                "Classic" to SingleImageLayout(images[0]),
                "Modern" to SingleImageLayout(images[0]).apply {
                    leftMargin = 2; rightMargin = 2; topMargin = 25; bottomMargin = 2
                },
                "Top Bottom" to SingleImageLayout(images[0]).apply {
                    topMargin = 25
                    bottomMargin = 25
                },
                "Left" to SingleImageLayout(images[0]).apply {
                    leftMargin = 100
                },
                "Top" to SingleImageLayout(images[0]).apply {
                    topMargin = 100
                },
                "Right" to SingleImageLayout(images[0]).apply {
                    rightMargin = 100
                },
                "Bottom" to SingleImageLayout(images[0]).apply {
                    bottomMargin = 100
                }
        )
    }

    override fun copy(): MemeLayout {
        return SingleImageLayout(images[0]).apply {
            leftMargin = this@SingleImageLayout.leftMargin
            rightMargin = this@SingleImageLayout.rightMargin
            topMargin = this@SingleImageLayout.topMargin
            bottomMargin = this@SingleImageLayout.bottomMargin
        }
    }

    override fun generateProperty(): LayoutProperty {
        return SingleImageLayoutProperty(leftMargin, rightMargin, topMargin, bottomMargin, backgroudColor)
    }

    init {
        update()
    }

    override fun innerWidth(): Int = images[0].width


    override fun innerHeight(): Int = images[0].height


    override fun getDrawingRectAt(pos: Int): RectF {
        if (pos >= count) throw ArrayIndexOutOfBoundsException("pos must be 0")

        val l = drawingRect.left + leftMarginCalc
        val t = drawingRect.top + topMarginCalc
        val r = drawingRect.right - rightMarginCalc
        val b = drawingRect.bottom - bottomMarginCalc
        return RectF(l, t, r, b)
    }

    fun getDrawingRectRelAt(pos: Int): RectF {
        if (pos >= count) throw ArrayIndexOutOfBoundsException("pos must be 0")

        val l = leftMarginCalc.toFloat()
        val t = topMarginCalc.toFloat()
        val r = drawingRect.right - rightMarginCalc - drawingRect.left
        val b = drawingRect.bottom - bottomMarginCalc - drawingRect.top
        return RectF(l, t, r, b)
    }
}

class LinearImageLayout(orien: Int, images: List<Bitmap>) : MemeLayout(images) {

    constructor(images: List<Bitmap>, lp: LinearImageLayoutProperty) : this(lp.orientation, images) {
        withLock {
            leftMargin = lp.leftMargin
            rightMargin = lp.rightMargin
            topMargin = lp.topMargin
            bottomMargin = lp.bottomMargin
            backgroudColor = lp.bgColor
            spacing = lp.spacing
        }
    }

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
    val spacingCalc
        get() = if (orientation == HORIZONTAL)
            ((spacing * innerWidth() / 100) * hr).toInt()
        else
            ((spacing * innerHeight() / 100) * vr).toInt()

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
                var l = drawingRect.left + leftMarginCalc
                val t = drawingRect.top + topMarginCalc
                val b = drawingRect.bottom - bottomMarginCalc


                val hh = b - t

                for (i in 0 until pos) {
                    val iw = hh * images[i].width / images[i].height
                    l += iw + spacingCalc
                }
                val rectF = RectF(l, t, l + (hh * images[pos].width / images[pos].height), b)
                val w = images[pos].width.toFloat()
                val h = images[pos].height.toFloat()
                return (w to h).fitCenter(rectF)
            }
            VERTICAL -> {
                val l = drawingRect.left + leftMarginCalc
                var t = drawingRect.top + topMarginCalc
                val r = drawingRect.right - rightMarginCalc

                val ww = r - l
                for (i in 0 until pos) {
                    val ih = ww * images[i].height / images[i].width
                    t += ih + spacingCalc
                }
                val rectF = RectF(l, t, r, t + (ww * images[pos].height / images[pos].width))
                val w = images[pos].width.toFloat()
                val h = images[pos].height.toFloat()
                return (w to h).fitCenter(rectF)
            }
        }
        return RectF()
    }

    override fun loadPresets(): Map<String, MemeLayout> {
        return mapOf(
                "Horizontal" to LinearImageLayout(HORIZONTAL, images).apply {
                    spacing = 2
                },
                "Vertical" to LinearImageLayout(VERTICAL, images).apply {
                    spacing = 2
                },
                "Horizontal Top" to LinearImageLayout(HORIZONTAL, images).apply {
                    spacing = 2
                    topMargin = 100
                },
                "Horizontal Bottom" to LinearImageLayout(HORIZONTAL, images).apply {
                    spacing = 2
                    bottomMargin = 100
                },
                "Vertical Left" to LinearImageLayout(VERTICAL, images).apply {
                    spacing = 2
                    leftMargin = 100
                },
                "Vertical Right" to LinearImageLayout(VERTICAL, images).apply {
                    spacing = 2
                    rightMargin = 100
                }
        )
    }

    override fun copy(): MemeLayout {
        return LinearImageLayout(orientation, images).apply {
            leftMargin = this@LinearImageLayout.leftMargin
            rightMargin = this@LinearImageLayout.rightMargin
            topMargin = this@LinearImageLayout.topMargin
            bottomMargin = this@LinearImageLayout.bottomMargin
            spacing = this@LinearImageLayout.spacing
        }
    }

    override fun generateProperty(): LayoutProperty {
        return LinearImageLayoutProperty(leftMargin, rightMargin, topMargin, bottomMargin, backgroudColor, orientation, spacing)
    }

}

class GridImageLayout(var span: Int, orientation: Int, images: List<Bitmap>) : MemeLayout(images) {
    constructor(images: List<Bitmap>, lp: GridImageLayoutProperty) : this(lp.span, lp.orientation, images) {
        withLock {
            leftMargin = lp.leftMargin
            rightMargin = lp.rightMargin
            topMargin = lp.topMargin
            bottomMargin = lp.bottomMargin
            backgroudColor = lp.bgColor
            hSpacing = lp.hSpacing
            vSpacing = lp.vSpacing
        }
    }

    override fun loadPresets(): Map<String, MemeLayout> {
        return mapOf(

                "Normal " to GridImageLayout(span, orientation, images),
                "Normal Spaced" to GridImageLayout(span, orientation, images).apply {
                    hSpacing = 2
                    vSpacing = 2
                    leftMargin = 2
                    rightMargin = 2
                    topMargin = 2
                    bottomMargin = 2
                },
                "Left" to GridImageLayout(span, orientation, images).apply {
                    hSpacing = 2
                    vSpacing = 2
                    leftMargin = 100 / columnCount
                },
                "Top" to GridImageLayout(span, orientation, images).apply {
                    hSpacing = 2
                    vSpacing = 2
                    topMargin = 100 / rowCount
                },
                "Right" to GridImageLayout(span, orientation, images).apply {
                    hSpacing = 2
                    vSpacing = 2
                    rightMargin = 100 / columnCount
                },
                "Bottom" to GridImageLayout(span, orientation, images).apply {
                    hSpacing = 2
                    vSpacing = 2
                    bottomMargin = 100 / rowCount
                }
        )

    }

    override fun copy(): MemeLayout {
        return GridImageLayout(span, orientation, images).apply {
            leftMargin = this@GridImageLayout.leftMargin
            rightMargin = this@GridImageLayout.rightMargin
            topMargin = this@GridImageLayout.topMargin
            bottomMargin = this@GridImageLayout.bottomMargin
            hSpacing = this@GridImageLayout.hSpacing
            vSpacing = this@GridImageLayout.vSpacing
        }
    }

    override fun generateProperty(): LayoutProperty {
        return GridImageLayoutProperty(leftMargin, rightMargin, topMargin, bottomMargin,
                backgroudColor, orientation, span, hSpacing, vSpacing)
    }

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
    // ((leftMargin * innerWidth() / 100) * hr).toInt()

    private val hSpacingCalc: Int
        get() = ((hSpacing * innerWidth() / 100) * hr).toInt()
    private val vSpacingCalc: Int
        get() = ((vSpacing * innerHeight() / 100) * vr).toInt()
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

    val horizontalMarginCalc: Int
        get() = ((horizontalMargin / 100f) * innerWidth() * hr).toInt()

    override val verticalMargin: Int
        get() = super.verticalMargin + totalVSpacing

    val verticalMarginCalc: Int
        get() = ((verticalMargin / 100f) * innerHeight() * vr).toInt()

    fun getRowAndColumnFor(pos: Int) =
            when (orientation) {
                HORIZONTAL -> pos / span to pos % span
                VERTICAL -> pos % span to pos / span
                else -> throwError()
            }


    override fun getDrawingRectAt(pos: Int): RectF {
        var l = drawingRect.left + leftMarginCalc
        var t = drawingRect.top + topMarginCalc
        val (row, column) = getRowAndColumnFor(pos)

        val availableTotalWidth = drawingRect.width() - horizontalMarginCalc
        val availableTotalHeight = drawingRect.height() - verticalMarginCalc
        val scaleX = availableTotalWidth / totalIW
        val scaleY = availableTotalHeight / totalIH

        l += (averageIW * scaleX + hSpacingCalc) * column
        t += (averageIH * scaleY + vSpacingCalc) * row

        val rectF = RectF(l, t, l + averageIW * scaleX, t + averageIH * scaleY)
        val w = images[pos].width.toFloat()
        val h = images[pos].height.toFloat()
        return (w to h).fitCenter(rectF)

    }
}

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