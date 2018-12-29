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
import androidx.core.graphics.withTranslation
import com.afollestad.materialdialogs.MaterialDialog
import com.memeit.backend.models.MemeItemProperty
import com.memeit.backend.models.MemeTextItemProperty
import com.memeit.backend.models.MemeTextStyleProperty
import com.innov8.memeit.commons.dp
import com.innov8.memeit.commons.models.TypefaceManager
import com.innov8.memeit.commons.sp
import com.innov8.memeit.commons.toSP

class MemeTextView : MemeItemView {
    constructor(context: Context, requiredWidth: Int = 100, requiredHeight: Int = 100) : super(context, requiredWidth, requiredHeight) {
        initDefaultDynamicLayout()
    }

    constructor(context: Context, tp: MemeTextItemProperty) : super(context, tp) {
        tp.tsp?.let {
            dynamicLayout = DynamicLayout(text, TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = TypefaceManager.byName(it.font)
                color = it.textColor
                textSize = it.textSize.sp(context)
                style = Paint.Style.FILL
                strokeWidth = it.strokeWidth.sp(context)
            }, itemWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
            color = it.textColor
            font = it.font
            bold = it.bold
            italic = it.italic
            allCaps = it.allCap
            stroke = it.stroked
            strokeColor = it.strokeColor

        } ?: initDefaultDynamicLayout()
        text = tp.text
    }


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initDefaultDynamicLayout()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initDefaultDynamicLayout()
    }


    init {
        minimumWidth = 10.dp(context)
        minimumHeight = 10.dp(context)
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
    }

    private fun initDefaultDynamicLayout() {
        dynamicLayout = DynamicLayout(text, TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.DEFAULT
            color = Color.BLACK
            textSize = 20f.sp(context)
            style = Paint.Style.FILL
        }, itemWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
    }

    lateinit var dynamicLayout: DynamicLayout

    var text: String = ""
        set(value) {
            field = value
            resizeToWrapText(true)
        }
    private var font = "Default"
    private var bold: Boolean = false
    private var italic: Boolean = false
    private var allCaps: Boolean = false
    private var stroke: Boolean = true
    private var strokeColor: Int = Color.WHITE
    private var color: Int = Color.BLACK

    fun setTypeface(font: String) {
        this.font = font
        dynamicLayout.paint.typeface = TypefaceManager.byName(font)
        resizeToWrapText()
    }

    fun setTextSize(value: Float) {
        dynamicLayout.paint.textSize = value
        resizeToWrapText(true)
    }

    fun setBold(value: Boolean) {
        bold = value
        invalidate()
    }

    fun setItalic(value: Boolean) {
        italic = value
        invalidate()
    }

    fun setAllCaps(value: Boolean) {
        allCaps = value
        recreateDynamicLayout()
        invalidate()
    }

    fun setStroke(value: Boolean) {
        stroke = value
        invalidate()
    }

    fun setStrokeWidth(value: Float) {
        dynamicLayout.paint.strokeWidth = value
        invalidate()
    }

    fun setStrokeColor(value: Int) {
        strokeColor = value
        invalidate()
    }

    fun setTextColor(value: Int) {
        color = value
        dynamicLayout.paint.color = value
        invalidate()
    }


    private fun recreateDynamicLayout() {
        val tx = if (allCaps) text.toUpperCase() else text
        dynamicLayout = DynamicLayout(tx, dynamicLayout.paint, itemWidth, Layout.Alignment.ALIGN_CENTER, 0.8f, 0f, false)
    }

    private fun resizeToWrapText(reset: Boolean = false) {
        if (reset) recreateDynamicLayout()
        if (isInMemeEditor && itemHeight < dynamicLayout.height)
            itemHeight = dynamicLayout.height
        invalidate()
        requestLayout()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        recreateDynamicLayout()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var a = itemHeight - dynamicLayout.height
        a = if (a > 0) a / 2 else 0
        canvas.withTranslation(itemX + paddingLeft, itemY + paddingTop + a) {
            if (stroke) {
                dynamicLayout.paint.style = Paint.Style.STROKE
                dynamicLayout.paint.color = strokeColor
                dynamicLayout.draw(this)

                dynamicLayout.paint.style = Paint.Style.FILL
            }
            dynamicLayout.paint.color = color
            dynamicLayout.draw(this)
        }
    }


    fun applyTextProperty(tp: MemeTextItemProperty) {
        applyProperty(tp)
        tp.tsp?.let {
            applyTextStyleProperty(it, text = "text")
        }
    }


    fun applyTextStyleProperty(tp: MemeTextStyleProperty, applySize: Boolean = true, text: String = "") {
        color = tp.textColor
        if (applySize) dynamicLayout.paint.textSize = tp.textSize.sp(context)
        font = tp.font
        dynamicLayout.paint.typeface = TypefaceManager.byName(tp.font)
        bold = tp.bold
        italic = tp.italic
        allCaps = tp.allCap
        stroke = tp.stroked
        strokeColor = tp.strokeColor
        dynamicLayout.paint.strokeWidth = tp.strokeWidth
        this.text = if (text.isEmpty()) this.text else text
    }

    override fun copy(): MemeTextView {
        val nt = MemeTextView(context, generateProperty() as MemeTextItemProperty)
        nt.x += 10.dp(context)
        nt.y += 10.dp(context)
        nt.text = text
        return nt
    }

    override fun generateProperty(): MemeItemProperty {
        return MemeTextItemProperty(
                x / maxWidth,
                y / maxHeight,
                itemWidth.toFloat() / maxWidth,
                itemHeight.toFloat() / maxHeight,
                rotation,
                text,
                generateTextStyleProperty()
        )
    }

    fun generateTextStyleProperty() = MemeTextStyleProperty(
            dynamicLayout.paint.textSize.toSP(context),
            color,
            font,
            bold, italic, allCaps,
            stroke, strokeColor, dynamicLayout.paint.strokeWidth.toSP(context)
    )


}
