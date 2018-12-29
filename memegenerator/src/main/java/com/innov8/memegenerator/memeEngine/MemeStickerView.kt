package com.innov8.memegenerator.memeEngine

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.memeit.backend.models.MemeItemProperty
import com.memeit.backend.models.MemeStickerItemProperty
import com.innov8.memeit.commons.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main

class MemeStickerView : MemeItemView {
    override fun generateProperty(): MemeItemProperty {
        return MemeStickerItemProperty(
                stickerId,
                x / maxWidth,
                y / maxHeight,
                itemWidth.toFloat() / maxWidth,
                itemHeight.toFloat() / maxHeight,
                rotation
        )
    }

    private val srcRect by lazy { Rect(0, 0, 100, 100) }
    private val destRect by lazy { Rect(0, 0, 100, 100) }
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var bitmap: Bitmap? = null
        set(value) {
            field = value
            if (value != null)
                srcRect.set(0, 0, value.width, value.height)
            invalidate()
        }

    private lateinit var stickerId: String

    constructor(context: Context, bitmap: Bitmap, id: String, width: Int = 100, height: Int = 100) : super(context, width, height) {
        this.bitmap = bitmap
        this.stickerId = id
    }

    constructor(context: Context, id: String, width: Int = 100, height: Int = 100) : super(context, width, height) {
        this.stickerId = id
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            bitmap = withContext(Dispatchers.Default) {
                BitmapFactory.decodeStream(context.assets.open(stickerId.substring(9)))
            }
        }
    }

    constructor(context: Context, memeStickerItemProperty: MemeStickerItemProperty) : super(context, memeStickerItemProperty) {
        this.stickerId = memeStickerItemProperty.stickerId
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            bitmap = withContext(Dispatchers.Default) {
                BitmapFactory.decodeStream(context.assets.open(stickerId.substring(9)))
            }
        }
    }

    constructor(context: Context, bitmap: Bitmap, memeStickerItemProperty: MemeStickerItemProperty) : super(context, memeStickerItemProperty) {
        this.stickerId = memeStickerItemProperty.stickerId
        this.bitmap = bitmap
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas) {
        bitmap?.let {
            canvas.drawBitmap(it, srcRect, destRect, paint)
            super.onDraw(canvas)
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val xx = itemX.toInt()
        val yy = itemY.toInt()
        destRect.set(xx + paddingLeft, yy + paddingTop, xx + paddingLeft + itemWidth, yy + paddingTop + itemHeight)
    }

    override fun copy(): MemeStickerView {
        val nt = MemeStickerView(context, bitmap!!, generateProperty() as MemeStickerItemProperty)
        nt.x = x + 10.dp(context)
        nt.y = y + 10.dp(context)
        return nt
    }
}