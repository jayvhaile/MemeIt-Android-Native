package com.innov8.memegenerator.memeEngine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.DynamicLayout
import android.text.InputType
import android.text.Layout
import android.text.TextPaint
import android.util.AttributeSet
import com.afollestad.materialdialogs.MaterialDialog

class MemeTextView : MemeItemView {
    constructor(context: Context, memeItemWidth: Int, memeItemHeight: Int) : super(context, memeItemWidth, memeItemHeight) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    lateinit var dl: DynamicLayout

    var text: String = ""
        set(value) {
            field = value
            resizeToWrapText(true)
            invalidate()
        }
    var typeface: Typeface = Typeface.DEFAULT
        set(value) {
            field = value
            dl.paint.typeface = value
            resizeToWrapText()
        }
    var textSize: Float = 1f
        set(value) {
            field = value
            dl.paint.textSize=value
            resizeToWrapText(true)
        }
    var bold: Boolean = false
        set(value) {
            field = value
            invalidate()
        }
    var italic: Boolean = false
        set(value) {
            field = value
            invalidate()
        }
    var allCaps: Boolean = false
        set(value) {
            field = value
            resetDL()
            invalidate()
        }
    var stroke: Boolean = true
        set(value) {
            field = value
            invalidate()
        }
    var strokeWidth: Float = 10f
        set(value) {
            field = value
            dl.paint.strokeWidth=value
            invalidate()
        }
    var strokeColor: Int = Color.BLACK
        set(value) {
            field = value
            invalidate()
        }
    var color: Int = Color.WHITE
        set(value) {
            field = value
            dl.paint.color = value
            invalidate()
        }


    private fun init() {
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        with(textPaint) {
            typeface = Typeface.DEFAULT
            color = Color.WHITE
            textSize = 120f
            style = Paint.Style.FILL

        }
        dl = DynamicLayout(text, textPaint, memeItemWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)

        onClickListener = {
            MaterialDialog.Builder(context)
                    .title("Insert Text")
                    .inputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                    .input("Write the text here", text,
                            { _, input ->
                                text = input.toString()
                            })
                    .show()
        }
        onResize = { w, h ->
            resetDL()
        }

    }

    fun resetDL() {
        val tx=if (allCaps)text.toUpperCase() else text
        dl = DynamicLayout(tx, dl.paint, memeItemWidth, Layout.Alignment.ALIGN_CENTER, 0.8f, 0f, false)
    }
    private fun resizeToWrapText(reset:Boolean=false) {
        if (reset)resetDL()
        if (memeItemHeight < dl.height)
            memeItemHeight = dl.height
        invalidate()
        requestLayout()
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        var ty=memeItemHeight/2f-dl.height/2f
        canvas?.translate(0f,ty)
        if(stroke){
            dl.paint.style=Paint.Style.STROKE
            dl.paint.color=strokeColor
            dl.draw(canvas)
            dl.paint.style=Paint.Style.FILL
            dl.paint.color=color
        }
        dl.draw(canvas)



        canvas?.restore()

    }

}
