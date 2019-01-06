package com.innov8.memegenerator.memeEngine

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.memeit.backend.models.Sticker
import com.memeit.backend.models.MemeItemProperty
import com.memeit.backend.models.MemeStickerItemProperty
import com.innov8.memeit.commons.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main

class MemeStickerView : MemeItemView {
    override fun generateProperty(): MemeItemProperty {
        return MemeStickerItemProperty(
                sticker,
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

    private lateinit var sticker: Sticker

    constructor(context: Context, bitmap: Bitmap, sticker: Sticker, width: Int = 200, height: Int = 200) : super(context, width, height) {
        this.bitmap = bitmap
        this.sticker = sticker
    }

    constructor(context: Context, sticker: Sticker, width: Int = 200, height: Int = 200) : super(context, width, height) {
        this.sticker = sticker
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            bitmap = withContext(Dispatchers.Default) { sticker.load(context.applicationContext) }
        }
    }

    constructor(context: Context, memeStickerItemProperty: MemeStickerItemProperty, mw: Int = 0, mh: Int = 0) : super(context, memeStickerItemProperty, mw, mh) {
        this.sticker = memeStickerItemProperty.sticker
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            bitmap = withContext(Dispatchers.Default) { sticker.load(context.applicationContext) }
        }
    }

    constructor(context: Context, bitmap: Bitmap, memeStickerItemProperty: MemeStickerItemProperty, mw: Int = 0, mh: Int = 0) : super(context, memeStickerItemProperty, mw, mh) {
        this.sticker = memeStickerItemProperty.sticker
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
        return MemeStickerView(context, bitmap!!, generateProperty() as MemeStickerItemProperty, maxWidth, maxHeight).apply {
            x += 10.dp(context)
            y += 10.dp(context)
        }
    }
}