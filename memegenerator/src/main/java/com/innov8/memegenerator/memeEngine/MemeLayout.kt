package com.innov8.memegenerator.memeEngine

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RectF

abstract class MemeLayout(val maxWidth:Int,val maxHeight:Int,val images:List<Bitmap>) {

    var invalidate:(()->Unit)?=null
    lateinit var drawingRect:RectF

    init {
        calc()
    }
    var leftMargin:Int=0
        set(value) {
            field = value
            calc()
        }
    var topMargin:Int=0
        set(value) {
            field = value
            calc()
        }
    var rightMargin:Int=0
        set(value) {
            field = value
            calc()
        }
    var bottomMargin:Int=0
        set(value) {
            field = value
            calc()
        }
    var backgroudColor:Int= Color.BLACK
        set(value) {
            field = value
            invalidate?.invoke()
        }



    private fun calc(){
        val w: Float
        val h: Float
        val x: Float
        val y: Float


        val iw = innerWidth().toFloat()
        val ih = innerHeight().toFloat()
        val cw = maxWidth.toFloat()-horizontalMargin()
        val ch = maxHeight.toFloat()-verticalMargin()

        val ir = iw / ih
        val cr = cw / ch

        if (ir < cr) {
            val hr = ch / ih
            w = iw * hr+horizontalMargin()
            h = ch+verticalMargin()
            x = (cw / 2.0f) - (w / 2.0f)
            y = 0f
        } else {
            val wr = cw / iw
            w = cw+horizontalMargin()
            h = ih * wr+verticalMargin()
            x = 0f
            y = (ch / 2f) - (h / 2f)
        }
        drawingRect=RectF(x,y,x+w,y+h)
        invalidate?.invoke()
    }

    fun horizontalMargin():Int=leftMargin+rightMargin
    fun verticalMargin():Int=topMargin+bottomMargin
    abstract fun getCount():Int

    protected abstract fun innerWidth():Int
    protected abstract fun innerHeight():Int

    abstract fun getDrawingRectAt(pos:Int):RectF

}

class SingleImage(maxWidth:Int, maxHeight:Int, bitmap:Bitmap): MemeLayout(maxWidth,maxHeight, listOf(bitmap)) {

    override fun innerWidth(): Int=images[0].width


    override fun innerHeight(): Int=images[0].height


    override fun getDrawingRectAt(pos: Int):RectF {
        if(pos>=getCount())throw IllegalArgumentException("pos must be 0")

        val l=drawingRect.left+leftMargin
        val t=drawingRect.top+topMargin
        val r=drawingRect.right-rightMargin
        val b=drawingRect.bottom-bottomMargin
        return RectF(l,t,r,b)
    }

    override fun getCount(): Int=1

}