package com.innov8.memegenerator.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.innov8.memegenerator.R

class ColorView : View {
    lateinit var paint: Paint
    var onColorChanged: ((color: Int) -> Unit)? =null
    var color: Int
        get() = paint.color
        set(color) {
            paint.color = color
            invalidate()
            onColorChanged?.invoke(color)
        }
    var strokeWidth=5f

    constructor(context: Context) : super(context) {
        init()
        color = Color.BLACK
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.strokeWidth=strokeWidth
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        init()
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ColorView,
                0, 0)

        try {
            var color = a.getColor(R.styleable.ColorView_preview_color, Color.RED)
            color = color
        } finally {
            a.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        val radius = Math.min(measuredWidth, measuredHeight) / 2.0f-strokeWidth
        val c=Math.min(measuredWidth,measuredHeight)/2.0f
        paint.style=Paint.Style.STROKE
        canvas.drawCircle(c, c, radius, paint)
        paint.style=Paint.Style.FILL
        canvas.drawCircle(c, c, radius-(strokeWidth*2), paint)
    }

}
