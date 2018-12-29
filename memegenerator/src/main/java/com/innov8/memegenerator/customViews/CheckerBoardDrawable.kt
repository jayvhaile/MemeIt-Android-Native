package com.innov8.memegenerator.customViews

import android.graphics.*
import android.graphics.drawable.Drawable

class CheckerBoardDrawable(val size:Float,val lightColor:Int,val darkColor:Int): Drawable() {

    val lpaint= Paint(Paint.ANTI_ALIAS_FLAG)
    val dpaint= Paint(Paint.ANTI_ALIAS_FLAG)
    init {
        lpaint.color=lightColor
        dpaint.color=darkColor
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        invalidateSelf()
    }
    override fun draw(canvas: Canvas) {
        val hCount:Int= (bounds.width()/size).toInt()+1
        val vCount:Int= (bounds.height()/size).toInt()+1
        for (i in 0..vCount){
            for (j in 0..hCount){
                val p=if((i+j)%2==0)lpaint else dpaint
                canvas.drawRect(j*size,i*size,j*size+size,i*size+size,p)
            }
        }
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }


}