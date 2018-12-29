package com.innov8.memegenerator.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.innov8.memeit.commons.dp

class BrushSizeChooserView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val brushSizes = floatArrayOf(4f, 8f, 12f, 16f, 20f, 24f, 28f).map { it.dp(context) }

    private val brushWidth: Int = (brushSizes.max()!! + 4.dp(context)).toInt()

    var onBrushSizeSelected: ((Float) -> Unit)? = null

    init {
        val lp = LayoutParams(0, brushWidth, 1f)
        val margin = 2.dp(context)
        lp.setMargins(margin, margin, margin, margin)
        brushSizes.map {
            BrushSizeView(context, it).apply {
                setOnClickListener { _ ->
                    chooseBrushSize(brushSize)
                    onBrushSizeSelected?.invoke(brushSize)
                }
            }
        }.forEach {
            addView(it, lp)
        }
        choose(0)
        gravity = Gravity.CENTER
    }

    fun setColorForAll(color: Int) {
        for (i in 0 until childCount) {
            val view = getChildAt(i) as BrushSizeView
            view.brushColor = color
        }
    }

    fun chooseBrushSize(brushSize: Float) {
        val index = brushSizes.indexOf(brushSize)
        if (index != -1) choose(index)
    }

    private fun choose(index: Int) {
        for (i in 0 until childCount) {
            val view = getChildAt(i) as BrushSizeView
            view.choosed = i == index
        }
    }

    class BrushSizeView : View {
        var brushSize = 1f
            set(value) {
                field = value
                requestLayout()
            }
        var brushColor = Color.WHITE
            set(value) {
                field = value
                paint.color = field
                strokePaint.color = field
                invalidate()
            }

        var strokeSize = 2f.dp(context)
            set(value) {
                field = value
                strokePaint.strokeWidth = field
                invalidate()
            }
        var choosed = false
            set(value) {
                field = value
                invalidate()
            }

        private val paint by lazy {
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = brushColor
            }
        }
        private val strokePaint by lazy {
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = brushColor
                strokeWidth = strokeSize
                style = Paint.Style.STROKE
            }
        }

        constructor(context: Context, brushSize: Float = 1f, brushColor: Int = Color.WHITE) : super(context) {
            this.brushSize = brushSize
            this.brushColor = brushColor

        }

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
        constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


        override fun onDraw(canvas: Canvas) {
            val pw = width - paddingLeft - paddingRight
            val ph = height - paddingTop - paddingBottom
            val brSize = floatArrayOf(pw.toFloat(), ph.toFloat(), brushSize).min()!!
            val cx = paddingLeft + pw / 2f
            val cy = paddingTop + ph / 2f
            canvas.drawCircle(cx, cy, brSize / 2, paint)
            val r = ((Math.min(pw, ph)) / 2f) - strokePaint.strokeWidth
            if (choosed) {
                canvas.drawCircle(cx, cy, r, strokePaint)
            }
        }

        private infix fun Float.min(i: Int): Int = Math.min(this.toInt(), i)


        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            var w = MeasureSpec.getSize(widthMeasureSpec)
            var h = MeasureSpec.getSize(heightMeasureSpec)
            val wm = MeasureSpec.getMode(widthMeasureSpec)
            val hm = MeasureSpec.getMode(heightMeasureSpec)
            if (wm != MeasureSpec.EXACTLY)
                w = (brushSize + paddingLeft + paddingRight + strokeSize * 2) min 8.dp(context)
            if (hm != MeasureSpec.EXACTLY)
                h = (brushSize + paddingTop + paddingBottom + strokeSize * 2) min 8.dp(context)
            setMeasuredDimension(w, h)
        }
    }
}
