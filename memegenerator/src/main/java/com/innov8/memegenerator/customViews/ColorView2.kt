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
    private var enlarge=0f
    var choosed = false
        set(value) {
            if (value != field) {
                field = value
                invalidate()
            }
        }

    init {
        strokePaint.style = Paint.Style.STROKE

    }

    constructor(context: Context, color: Int,
                choosedColor: Int = Color.WHITE,
                radius: Float = 10f.fromDPToPX(context),
                strokeWidth: Float = 2f.fromDPToPX(context)) : super(context) {
        init(color, choosedColor, radius, strokeWidth)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init(color: Int = Color.BLACK,
                     choosedColor: Int = Color.WHITE,
                     radius: Float = 10f.fromDPToPX(context),
                     strokeWidth: Float = 2f.fromDPToPX(context)) {
        strokePaint.color = choosedColor
        strokePaint.strokeWidth = strokeWidth
        innerPaint.color = color
        this.radius = radius
        enlarge=min(paddingLeft,paddingRight,paddingTop,paddingBottom)/2f
    }
    private fun min(vararg i:Int):Int=i.min()?:0


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


        val r = ((Math.min(pw, ph)+ if(choosed)enlarge else 0f) / 2f) - strokePaint.strokeWidth
        if (choosed) {

            canvas?.drawCircle(cx, cy, r, strokePaint)
        }
        canvas?.drawCircle(cx, cy, r, innerPaint)
    }
}