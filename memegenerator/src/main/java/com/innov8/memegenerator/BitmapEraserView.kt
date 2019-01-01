package com.innov8.memegenerator

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.innov8.memegenerator.customViews.CheckerBoardDrawable
import com.innov8.memegenerator.memeEngine.PaintHandler
import com.innov8.memegenerator.utils.fitCenter
import com.innov8.memeit.commons.dp

class BitmapEraserView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var bitmap: Bitmap? = null
        set(value) {
            field = value
            updateRect()
            invalidate()
        }
    private val paintHandler by lazy {
        PaintHandler(context).apply {
            paint.apply {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                color = Color.TRANSPARENT
                alpha = 0
                isAntiAlias = true
            }
            paintProperty = paintProperty.copy(color = Color.TRANSPARENT, brushSize = 64f)
            onInvalidate = { invalidate() }
        }
    }

    init {
        background = CheckerBoardDrawable(12f.dp(context), Color.LTGRAY, Color.GRAY)
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
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
            canvas.drawColor(Color.TRANSPARENT)
            canvas.drawBitmap(it, null, destRect, null)
            paintHandler.draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return paintHandler.onTouchEvent(event)
    }


}