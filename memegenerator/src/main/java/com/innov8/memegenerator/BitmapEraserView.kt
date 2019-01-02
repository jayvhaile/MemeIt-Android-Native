package com.innov8.memegenerator

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toRect
import com.innov8.memegenerator.interfaces.PaintEditInterface
import com.innov8.memegenerator.memeEngine.PaintHandler
import com.innov8.memegenerator.utils.capture
import com.innov8.memegenerator.utils.fitCenter

class BitmapEraserView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    var bitmap: Bitmap? = null
        set(value) {
            field = value?.apply {
                setHasAlpha(true)
            }
            updateRect()
            invalidate()
        }
    val paintHandler by lazy {
        PaintHandler(context).apply {
            paint.apply {
            }
            onInvalidate = {
                invalidate()
            }
        }

    }

    private val destRect by lazy {
        RectF(0f, 0f, 0f, 0f)
    }

    private fun updateRect() {
        destRect.set(((bitmap?.width ?: 1) to (bitmap?.height ?: 1)).fitCenter(width, height))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateRect()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.let {
            canvas.drawBitmap(it, null, destRect, null)
            paintHandler.draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return paintHandler.onTouchEvent(event)
    }

    fun getEditedBitmap(): Bitmap {
        return capture(destRect.toRect())
    }

}