package com.innov8.memegenerator.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.innov8.memegenerator.utils.fromDPToPX

class ColorView2 : View {


    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var radius: Float = 0f

    var choosed = false
        set(value) {
            field = value
            invalidate()
        }

    init {
        strokePaint.style = Paint.Style.STROKE

    }

    constructor(context: Context, color: Int,
                choosedColor: Int = Color.WHITE,
                radius: Float = 10f.fromDPToPX(context),
                strokeWidth: Float = 2f.fromDPToPX(context)) : super(context) {
        strokePaint.color = color
        strokePaint.strokeWidth = strokeWidth
        innerPaint.color = choosedColor
        this.radius = radius
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        strokePaint.color = Color.BLACK
        strokePaint.strokeWidth = 2f.fromDPToPX(context)
        innerPaint.color = Color.WHITE

        radius = 10f.fromDPToPX(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        strokePaint.color = Color.BLACK
        strokePaint.strokeWidth = 2f.fromDPToPX(context)
        innerPaint.color = Color.WHITE
        radius = 10f.fromDPToPX(context)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val rh = radius.toInt() + paddingLeft + paddingRight + strokePaint.strokeWidth
        val rv = radius.toInt() + paddingTop + paddingBottom + strokePaint.strokeWidth
        setMeasuredDimension(resolveSizeAndState(rh.toInt(), widthMeasureSpec, 1),
                resolveSizeAndState(rv.toInt(), heightMeasureSpec, 1))
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        val pw = width - paddingLeft - paddingRight
        val ph = height - paddingTop - paddingBottom

        val cx = paddingLeft + pw / 2f
        val cy = paddingTop + ph / 2f

        val r = (Math.min(pw, ph) / 2f) - strokePaint.strokeWidth
        canvas?.drawCircle(cx, cy, r, strokePaint)
        if (choosed)
            canvas?.drawCircle(cx, cy, r, innerPaint)
    }
}