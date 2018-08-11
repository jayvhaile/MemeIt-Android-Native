package com.innov8.memegenerator.meme_engine


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup

/**
 * Created by Haile on 5/19/2018.
 */

class MemeEditorView : ViewGroup, MemeEditorInterface {
    override fun onEditTypeChanged(editType: EditType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val aspectRatio: Float = 0.toFloat()

    private val memeItemViews: MutableList<MemeItemView> = mutableListOf()

    constructor(context: Context) : super(context) {
        init()

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    lateinit var textPaint: TextPaint
    private fun init() {
        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        with(textPaint) {
            typeface = Typeface.SANS_SERIF
            color = Color.WHITE
            textSize = 60f
            style = Paint.Style.FILL

        }
        setBackgroundColor(Color.BLACK)
    }

    fun addMemeItemView(child: MemeItemView) {
        super.addView(child)
        log("added")
        invalidate()
    }

    fun removeMemeItemView(child: MemeItemView) {
        super.removeView(child)
    }

    override fun onDraw(canvas: Canvas?) {
        background.draw(canvas)
        canvas?.drawText("hey",0f,0f,textPaint);
        super.onDraw(canvas)
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        val eleft: Int = this.paddingLeft
        val etop: Int = this.paddingTop
        val eright: Int = this.measuredWidth - this.paddingRight
        val ebottom: Int = this.measuredHeight - this.paddingBottom

        val childWidth = eright - eleft
        val childHeight = ebottom - etop

        var curRight: Int
        var curBottom: Int
        var curLeft: Int
        var curTop: Int
        var maxHeight: Int
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(View.MeasureSpec.makeMeasureSpec(childWidth, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(childHeight, View.MeasureSpec.AT_MOST))
            curLeft = if (child.left < eleft) eleft else child.left
            curTop = if (child.left < etop) etop else child.top
            curRight = if (curLeft + child.measuredWidth > eright) eright else curLeft + child.measuredWidth
            curBottom = if (curTop + child.measuredHeight > ebottom) ebottom else curTop + child.measuredHeight
            child.layout(curLeft, curTop, curRight, curBottom)
        }
    }



}

fun log(vararg messages: Any) {
    Log.d("#MemeIt", messages.joinToString (" , "))
}