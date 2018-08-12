package com.innov8.memegenerator.meme_engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.InputType
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import com.afollestad.materialdialogs.MaterialDialog
import com.innov8.memegenerator.utils.toast

class MemeTextView : MemeItemView {
    var text: String = ""
        set(value) {
            field = value
            textPaint.textSize = getFitTextSize()
            sl = StaticLayout(text, textPaint, memeItemWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0f, false)
            invalidate()
        }


    var typeface: Typeface = Typeface.DEFAULT
        set(value) {
            field = value
            context.toast("typeface")
            textPaint.typeface = value
            invalidate()
            requestLayout()
        }
    var textSize: Float = 1f
        set(value) {
            field = value
            textPaint.textSize = textSize
            invalidate()
            requestLayout()
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
    var underline: Boolean = false
        set(value) {
            field = value
            invalidate()
        }
    var stroke: Boolean = false
        set(value) {
            field = value
            invalidate()
        }
    var strokeWidth: Float = 1f
        set(value) {
            field = value
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
            textPaint.color = value
            context.toast("chhh")
            invalidate()
        }


    constructor(context: Context, memeItemWidth: Int, memeItemHeight: Int) : super(context, memeItemWidth, memeItemHeight) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    lateinit var textPaint: TextPaint
    lateinit var sl: StaticLayout
    private fun init() {
        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        with(textPaint) {
            typeface = Typeface.DEFAULT
            color = Color.WHITE
            textSize = 30f
            style = Paint.Style.FILL

        }
        onClickListener= {
            MaterialDialog.Builder(context)
                    .title("Insert Text")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input("Write the text here", null,
                            { dialog, input ->
                                text=input.toString()
                            })
                    .show()
        }
    }

    private fun getFitTextSize(): Float =
            (memeItemWidth - 10) / textPaint.measureText(text) * textPaint.textSize

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        textPaint.textSize = getFitTextSize()

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        sl = StaticLayout(text, textPaint, memeItemWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0f, false)
        sl.draw(canvas)
    }

}
