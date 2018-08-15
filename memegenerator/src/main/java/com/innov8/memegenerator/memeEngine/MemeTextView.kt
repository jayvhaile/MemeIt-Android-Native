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
import com.innov8.memegenerator.models.TextProperty

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
        }

    fun setTypeface(value: Typeface) {
        dl.paint.typeface = value
        resizeToWrapText()
    }

    fun setTextSize(value: Float) {
        dl.paint.textSize = value
        resizeToWrapText(true)
    }

    private var bold: Boolean = false

    fun setBold(value: Boolean) {
        bold = value
        invalidate()
    }

    private var italic: Boolean = false
    fun setItalic(value: Boolean) {
        italic = value
        invalidate()
    }

    private var allCaps: Boolean = false
    fun setAllCaps(value: Boolean) {
        allCaps = value
        resetDL()
        invalidate()
    }

    private var stroke: Boolean = true
    fun setStroke(value: Boolean) {
        stroke = value
        invalidate()
    }

    fun setStrokeWidth(value: Float) {
        dl.paint.strokeWidth = value
        invalidate()
    }

    private var strokeColor: Int = Color.BLACK
    fun setStrokeColor(value: Int) {
        strokeColor = value
        invalidate()
    }

    private var color: Int = Color.WHITE
    fun setTextColor(value: Int) {
        color = value
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
        if (isInMemeEditor)
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
        val tx = if (allCaps) text.toUpperCase() else text
        dl = DynamicLayout(tx, dl.paint, memeItemWidth, Layout.Alignment.ALIGN_CENTER, 0.8f, 0f, false)
    }

    private fun resizeToWrapText(reset: Boolean = false) {
        if (reset) resetDL()
        if (isInMemeEditor && memeItemHeight < dl.height)
            memeItemHeight = dl.height
        invalidate()
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!isInMemeEditor) resetDL()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        var ty = memeItemHeight / 2f - dl.height / 2f
        canvas?.translate(0f, ty)
        if (stroke) {
            dl.paint.style = Paint.Style.STROKE
            dl.paint.color = strokeColor
            dl.draw(canvas)
            dl.paint.style = Paint.Style.FILL
        }
        dl.paint.color = color
        dl.draw(canvas)
        canvas?.restore()
    }

    fun generateProperty(): TextProperty {
        return TextProperty(dl.paint.textSize, color, dl.paint.typeface,
                bold, italic, allCaps,
                stroke, strokeColor, dl.paint.strokeWidth
        )
    }

    fun applyTextProperty(tp: TextProperty,applySize:Boolean=true, text: String = "") {
        color = tp.textColor
        if(applySize) dl.paint.textSize = tp.textSize
        dl.paint.typeface = tp.typeface
        bold = tp.bold
        italic = tp.italic
        allCaps = tp.allCap
        stroke = tp.stroked
        strokeColor = tp.strokeColor
        dl.paint.strokeWidth = tp.strokeWidth
        this.text = if(text.isEmpty()) this.text else text
    }

}
