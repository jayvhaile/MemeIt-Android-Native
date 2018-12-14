package com.innov8.memegenerator.MemeEngine

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
import com.innov8.memeit.commons.dp
import com.innov8.memeit.commons.models.MyTypeFace
import com.innov8.memeit.commons.models.TextProperty
import com.innov8.memeit.commons.models.TextStyleProperty
import com.innov8.memeit.commons.sp
import com.innov8.memeit.commons.toSP

class MemeTextView : MemeItemView {
    constructor(context: Context, requiredWidth: Int=100, requiredHeight: Int=100) : super(context, requiredWidth, requiredHeight) {
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
    private var myTypeface: MyTypeFace = MyTypeFace.DEFAULT
    fun setTypeface(value: MyTypeFace) {
        myTypeface=value
        dl.paint.typeface = value.getTypeFace(context)
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

    private var color: Int = Color.BLACK
    fun setTextColor(value: Int) {
        color = value
        dl.paint.color = value
        invalidate()
    }


    private fun init() {
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        with(textPaint) {
            typeface = Typeface.DEFAULT
            color = Color.BLACK
            textSize = 20f.sp(context)
            style = Paint.Style.FILL
        }
        minimumWidth = 10.dp(context)
        minimumHeight = 10.dp(context)
        dl = DynamicLayout(text, textPaint, requiredWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
        if (isInMemeEditor)
            onClickListener = {
                MaterialDialog.Builder(context)
                        .title("Insert Text")
                        .inputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                        .input("Write the text here", text
                        ) { _, input ->
                            text = input.toString()
                        }
                        .show()
            }
        onResize = { _, _ ->
            resetDL()
        }
    }

    fun resetDL() {
        val tx = if (allCaps) text.toUpperCase() else text
        dl = DynamicLayout(tx, dl.paint, requiredWidth, Layout.Alignment.ALIGN_CENTER, 0.8f, 0f, false)
    }

    private fun resizeToWrapText(reset: Boolean = false) {
        if (reset) resetDL()
        if (isInMemeEditor && requiredHeight < dl.height)
            requiredHeight = dl.height
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
        var a = requiredHeight - dl.height
        a = if (a > 0) a / 2 else 0
        canvas?.translate(itemX + paddingLeft, itemY + paddingTop + a)
        if (stroke) {
            dl.paint.style = Paint.Style.STROKE
            dl.paint.color = strokeColor
            dl.draw(canvas)

            dl.paint.style = Paint.Style.FILL
        }
        dl.paint.color = color
        dl.draw(canvas)
        canvas?.restore()
        super.onDraw(canvas)

    }

    fun generateTextProperty(totalW: Float, totalH: Float): TextProperty {
        return TextProperty(x / totalW,
                y / totalH,
                requiredWidth / totalW,
                requiredHeight / totalH,
                generateTextStyleProperty())
    }

    fun generateTextStyleProperty(): TextStyleProperty {
        return TextStyleProperty(dl.paint.textSize.toSP(context), color, myTypeface,
                bold, italic, allCaps,
                stroke, strokeColor, dl.paint.strokeWidth
        )
    }

    fun applyTextProperty(tp: TextProperty, totalW: Float, totalH: Float, xoff: Float = 0f, yOff: Float = 0f) {
        x = totalW * tp.xP + xoff
        y = totalH * tp.yP + yOff
        requiredWidth = (totalW * tp.widthP).toInt()
        requiredHeight = (totalH * tp.heightP).toInt()
        applyTextStyleProperty(tp.textStyleProperty, text = "text")
    }

    fun applyTextStyleProperty(tp: TextStyleProperty, applySize: Boolean = true, text: String = "") {
        color = tp.textColor
        if (applySize) dl.paint.textSize = tp.textSize.sp(context)

        myTypeface = tp.myTypeFace
        dl.paint.typeface = tp.myTypeFace.getTypeFace(context)
        bold = tp.bold
        italic = tp.italic
        allCaps = tp.allCap
        stroke = tp.stroked
        strokeColor = tp.strokeColor
        dl.paint.strokeWidth = tp.strokeWidth
        this.text = if (text.isEmpty()) this.text else text
    }

    override fun copy(): MemeTextView {
        val tp = generateTextProperty(maxWidth.toFloat(), maxHeight.toFloat())
        val nt = MemeTextView(context, width, height)
        nt.applyTextProperty(tp, maxWidth.toFloat(), maxHeight.toFloat())
        nt.x += 10.dp(context)
        nt.y += 10.dp(context)
        nt.rotation=this.rotation
        nt.text = text
        return nt
    }

}
