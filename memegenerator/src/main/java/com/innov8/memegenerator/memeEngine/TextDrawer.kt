package com.innov8.memegenerator.memeEngine

import android.graphics.Canvas
import android.text.TextPaint

class TextDrawer{
    lateinit var textPaint: TextPaint
    var text: String=""
        set(value) {
            field=value
            sText=field.split("\n")
        }
    var verticalSpacing:Float=0f
    var textHeight:Float=0f
    private  var sText = listOf<String>()

    private fun calcHeightForTextSize(): Float {
        val metric = textPaint.fontMetrics

        return Math.ceil((metric.descent - metric.ascent).toDouble()).toFloat()
    }
    private fun fitIntoHeight(height:Float){

    }

    fun draw(canvas: Canvas){
        sText.forEachIndexed{index ,text->{

        }}
        sText.forEach {
            val ty = textHeight - textPaint.fontMetrics.descent
            canvas?.drawText(text, 0f, ty, textPaint)
        }

    }

}
