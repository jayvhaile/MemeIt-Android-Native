package com.innov8.memeit.CustomViews

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import com.innov8.memegenerator.utils.fromDPToPX

class TextDrawable(val context: Context, text: String = "", bColor: Int = Color.RED, tColor: Int = Color.WHITE, padding: Int = 0) : Drawable() {
    companion object {
        val SHAPE_CIRCLE = 0
        val SHAPE_RECT = 1
    }
    constructor(context: Context) : this(context,"")
    var text: String = text
        set(value) {
            field = value
            recalcTextSize()
            invalidateSelf()

        }
    var padding:Int= padding
        set(value) {
            field = value
            recalcTextSize()
            invalidateSelf()
        }
    var color: Int = bColor
        set(value) {
            field = value
            bPaint.color = value
            invalidateSelf()
        }
    var textColor: Int = tColor
        set(value) {
            field = value
            tPaint.color = value
            invalidateSelf()
        }
    var typeface: Typeface = Typeface.DEFAULT
        set(value) {
            field = value
            tPaint.typeface = typeface
            invalidateSelf()
        }
    var shape: Int = SHAPE_CIRCLE
        set(value) {
            field = value
            invalidateSelf()
        }

    private val bPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val tPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        bPaint.color = color
        tPaint.color = textColor
        tPaint.typeface = typeface

    }
    fun recalcTextSize() {
        if(bounds.width()==0)return
        val t = (bounds.width()-(padding*2)-24.fromDPToPX(context)) / tPaint.measureText(text) * tPaint.textSize
        tPaint.textSize = t
    }
    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        recalcTextSize()
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        val cx = bounds.width() / 2f
        val cy = bounds.height() / 2f
        when (shape) {
            SHAPE_CIRCLE -> {
                val rad = Math.min(cx, cy)-padding
                canvas.drawCircle(cx, cy, rad, bPaint)
            }
            SHAPE_RECT -> {
                canvas.drawRect(bounds.left.toFloat(),
                        bounds.top.toFloat(),
                        bounds.right.toFloat(),
                        bounds.bottom.toFloat(),
                        bPaint)
            }
        }
        val tx = cx - (tPaint.measureText(text) / 2f)
        val ty = cy - (tPaint.fontMetrics.descent + tPaint.fontMetrics.ascent) / 2f

        canvas.drawText(text, tx, ty, tPaint)
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }


}