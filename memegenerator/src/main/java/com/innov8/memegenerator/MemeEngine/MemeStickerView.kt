package com.innov8.memegenerator.MemeEngine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import com.innov8.memeit.commons.dp

class MemeStickerView : MemeItemView {

    var srcRect = Rect(0, 0, 100, 100)
    var destRect = Rect(0, 0, 100, 100)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var bitmap: Bitmap? = null
        set(value) {
            field = value
            if (value != null)
                srcRect = Rect(0, 0, value.width, value.height)
        }

    constructor(context: Context, bitmap: Bitmap, width: Int = 100, height: Int = 100) : super(context, width, height) {
        this.bitmap = bitmap
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas?) {
        bitmap ?: return
        canvas?.drawBitmap(bitmap!!, srcRect, destRect, paint)
        super.onDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val xx = itemX.toInt()
        val yy = itemY.toInt()
        destRect = Rect(xx + paddingLeft, yy + paddingTop, xx+paddingLeft+requiredWidth, yy+paddingTop+requiredHeight)
    }

    override fun copy(): MemeStickerView {
        val nt = MemeStickerView(context, bitmap!!, requiredWidth, requiredHeight)
        nt.x = x + 10.dp(context)
        nt.y = y + 10.dp(context)
        return nt
    }
}