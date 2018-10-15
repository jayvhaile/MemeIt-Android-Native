package com.innov8.memeit.CustomViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.innov8.memegenerator.utils.dp
import com.innov8.memeit.R

class CountView : View {
    var radius = 0
        set(value) {
            field = value
            requestLayout()
        }
    var borderWidth = 0f
        set(value) {
            field = value
            paintBorder.strokeWidth = field
            invalidate()
        }

    var borderColor = Color.GRAY
        set(value) {
            field = value
            paintBorder.color = field
        }
    var backColor = Color.GRAY
        set(value) {
            field = value
            paint.color = field
            invalidate()
        }

    var textColor = Color.WHITE
        set(value) {
            field = value
            textPaint.color = field
            invalidate()
        }
    var choosed = false
        set(value) {
            field = value
            calc()
            invalidate()
        }
    var count = 0
        set(value) {
            field = value
            invalidate()
        }

    private val rect = RectF()

    private val paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        isDither = true
        textAlign = Paint.Align.CENTER
        color = backColor
    }
    private val paintBorder = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        isDither = true
        textAlign = Paint.Align.CENTER
        style = Paint.Style.STROKE
        color = borderColor
        strokeWidth = borderWidth
    }
    private val textPaint = TextPaint().apply {
        this.isAntiAlias = true
        this.color = textColor
        this.textSize = rect.height() * 0.55f
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CountView, 0, 0)
        try {
            lock = true
            radius = a.getDimension(R.styleable.CountView_radius, 8f.dp(context)).toInt()
            borderWidth = a.getDimension(R.styleable.CountView_borderWidth, 2f.dp(context))
            backColor = a.getColor(R.styleable.CountView_backColor, Color.BLACK)
            borderColor = a.getColor(R.styleable.CountView_borderColor, Color.GRAY)
            textColor = a.getColor(R.styleable.CountView_textColor, Color.WHITE)
            choosed = a.getBoolean(R.styleable.CountView_choosed, false)
            count = a.getInteger(R.styleable.CountView_count, 0)
            lock = false
        } finally {
            a.recycle()
        }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (choosed)
            canvas.drawOval(rect, paint)
        canvas.drawOval(rect, paintBorder)
        if (!choosed) return
        val x = rect.centerX() - (textPaint.measureText(text) / 2f)
        val y = rect.centerY() - (textPaint.ascent() + textPaint.descent()) * 0.5f
        canvas.drawText(text, x, y, textPaint)
    }

    private val text
        get() = count.toString()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var w = MeasureSpec.getSize(widthMeasureSpec)
        var h = MeasureSpec.getSize(heightMeasureSpec)
        val wm = MeasureSpec.getMode(widthMeasureSpec)
        val hm = MeasureSpec.getMode(heightMeasureSpec)

        val size = radius * 2 + borderWidth
        if (wm != MeasureSpec.EXACTLY) w = size.toInt() + paddingLeft + paddingRight
        if (hm != MeasureSpec.EXACTLY) h = size.toInt() + paddingTop + paddingBottom

        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calc()
    }

    private fun calc() {
        val widthF = width.toFloat()-paddingLeft-paddingRight
        val heightF = height.toFloat()-paddingTop-paddingBottom
        val size = Math.min(widthF, heightF)
        val s = size - borderWidth
        val borderHalf = borderWidth / 2
        val xF = (widthF / 2) - (size / 2) + borderHalf + paddingLeft
        val yF = (heightF / 2) - (size / 2) + borderHalf + paddingTop
        rect.set(xF, yF, xF + s , yF + s)
        textPaint.textSize = rect.height() * 0.55f
    }

    private var lock = false
        set(value) {
            field = value
            if (!field) {
                requestLayout()
                invalidate()
            }
        }

    override fun invalidate() {
        if (!lock) super.invalidate()
    }

    override fun requestLayout() {
        if (!lock) super.requestLayout()
    }
}