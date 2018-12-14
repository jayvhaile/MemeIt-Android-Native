package com.innov8.memegenerator.CustomViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.innov8.memegenerator.MemeEngine.MemeLayout
import kotlinx.android.synthetic.main.meme_editor.view.*

class LayoutPresetView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var memeLayout: MemeLayout? = null
        set(value) {
            field = value
            field?.updateSize(width, height)
        }
    private val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
        }
    }

    fun update(ml: MemeLayout? = memeLayout) {
        memeLayout = ml
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (memeLayout != null) {
            val ml = memeLayout!!
            canvas.drawRect(ml.drawingRect, paint)
            for (i in 0 until ml.count) {
                canvas.drawBitmap(ml.images[i],
                        null,
                        ml.getDrawingRectAt(i),
                        null)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        memeLayout?.updateSize(w, h)
    }

}