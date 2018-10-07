package com.innov8.memeit.CustomClasses

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.animation.LinearInterpolator
import com.innov8.memegenerator.utils.loadBitmap

class ScrollingImageDrawable(val bitmap: Bitmap) : Drawable() {

    constructor(context: Context, id: Int) : this(context.loadBitmap(id, .8f))
    constructor(context: Context, id: Int,width:Int,height:Int) :
            this(context.loadBitmap(id, width,height))


    private val rec1: RectF= RectF()
    private val rec2: RectF= RectF()
    var progress: Float = 0f
        set(value) {
            field = value
            updateRecs()
        }
    val anim = ValueAnimator.ofFloat(0f, 1f)

    init {
        anim.repeatCount = ValueAnimator.INFINITE
        anim.interpolator = LinearInterpolator()
        anim.duration = 20000L
        anim.addUpdateListener {
            progress = it.animatedValue as Float
        }
        anim.start()
    }

    private fun updateRecs(){
        val w = bounds.width()
        val h = bounds.height().toFloat()
        rec1.set(progress * w, 0f, progress * w + w, h)
        rec2.set((progress - 1) * w, 0f, (progress - 1) * w + w,h)
        invalidateSelf()
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        updateRecs()
    }
    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, null, rec1, null)
        canvas.drawBitmap(bitmap, null, rec2, null)
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE


    override fun setColorFilter(colorFilter: ColorFilter?) {
    }
}