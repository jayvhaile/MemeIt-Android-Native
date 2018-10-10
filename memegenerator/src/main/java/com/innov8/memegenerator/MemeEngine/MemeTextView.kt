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
import com.innov8.memegenerator.Models.MyTypeFace
import com.innov8.memegenerator.Models.TextProperty
import com.innov8.memegenerator.Models.TextStyleProperty
import com.innov8.memegenerator.utils.dp
import com.innov8.memegenerator.utils.log
import com.innov8.memegenerator.utils.sp
import com.innov8.memegenerator.utils.toSP

class MemeTextView : MemeItemView {
    constructor(context: Context, memeItemWidth: Int=0, memeItemHeight: Int=0) : super(context, memeItemWidth, memeItemHeight) {
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
    private var myTypeface:MyTypeFace= MyTypeFace.DEFAULT
    fun setTypeface(value: MyTypeFace) {
        myTypeface=value
        dl.paint.typeface = value.getTypeFace(context)
        resizeToWrapText(true)
    }

    fun setTextSize(value: Float) {
        dl.paint.textSize = value.sp(context)
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
        resizeToWrapText(true)
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
            textSize = 20f.sp(context)
            style = Paint.Style.FILL

        }
        minimumWidth=10.dp(context)
        minimumHeight=10.dp(context)
        dl = DynamicLayout(text, textPaint,mW(textPaint), Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
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
        memeItemWidth
    }
    val off=10.dp(context)

    private fun resetDL() {
        val tx = if (allCaps) text.toUpperCase() else text
        dl = DynamicLayout(tx, dl.paint,mW(), Layout.Alignment.ALIGN_CENTER, 0.8f, 0f, false)

        sx=memeItemWidth/dl.width.toFloat()
        sy=memeItemHeight/dl.height.toFloat()
    }
    private fun mW(textPaint: TextPaint=dl.paint):Int{
              val w=text.split("\n").map {
            textPaint.measureText(if(allCaps)it.toUpperCase()else it).toInt()
        }.max()?:minimumWidth
        return  if(w>maxWidth) maxWidth else w
    }
    private fun mH():Int{
        if(stroke){
            dl.paint.style=Paint.Style.STROKE
        }
        val h=dl.height
        dl.paint.style=Paint.Style.FILL
        return h
    }

    private fun resizeToWrapText(reset: Boolean = false) {
        if (reset) resetDL()
        if (isInMemeEditor){
            memeItemWidth= (dl.width*sx).toInt()
            memeItemHeight = (mH()*sy).toInt()

        }
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!isInMemeEditor) resetDL()
    }
    var sx=1f
    var sy=1f

    override fun onDraw(canvas: Canvas?) {
        canvas?.save()
        val ty = itemY+(memeItemHeight / 2f) - dl.height / 2f
        canvas?.translate(itemX, ty/sy)

        canvas?.scale(sx,sy)
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
    fun generateTextProperty(totalW:Float,totalH:Float):TextProperty{
        return TextProperty(x/totalW,
                y/totalH,
                memeItemWidth/totalW,
                memeItemHeight/totalH,
                generateTextStyleProperty())
    }
    fun generateTextStyleProperty(): TextStyleProperty {
        return TextStyleProperty(dl.paint.textSize.toSP(context), color, myTypeface,
                bold, italic, allCaps,
                stroke, strokeColor, dl.paint.strokeWidth
        )
    }
    fun applyTextProperty(tp:TextProperty,totalW:Float,totalH:Float,xoff:Float=0f,yOff:Float=0f){
        x=totalW*tp.xP+xoff
        y=totalH*tp.yP+yOff
        memeItemWidth= (totalW*tp.widthP).toInt()
        memeItemHeight= (totalH*tp.heightP).toInt()
        applyTextStyleProperty(tp.textStyleProperty,text = "text")
    }
    fun applyTextStyleProperty(tp: TextStyleProperty, applySize:Boolean=true, text: String = "") {
        color = tp.textColor
        if(applySize) dl.paint.textSize = tp.textSize.sp(context)

        myTypeface=tp.myTypeFace
        log("apply",tp.myTypeFace)
        dl.paint.typeface = tp.myTypeFace.getTypeFace(context)
        bold = tp.bold
        italic = tp.italic
        allCaps = tp.allCap
        stroke = tp.stroked
        strokeColor = tp.strokeColor
        dl.paint.strokeWidth = tp.strokeWidth
        this.text = if(text.isEmpty()) this.text else text
    }
    override fun copy():MemeTextView{
        val tp=generateTextProperty(maxWidth.toFloat(), maxHeight.toFloat())
        val nt=MemeTextView(context,width,height)
        nt.applyTextProperty(tp,maxWidth.toFloat(),maxHeight.toFloat())
        nt.x+=10.dp(context)
        nt.y+=10.dp(context)
        nt.text=text
        return nt
    }

}
