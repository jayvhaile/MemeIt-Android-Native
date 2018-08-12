package com.innov8.memegenerator.meme_engine


import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
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
    private var selectedView:MemeItemView?=null
    private val memeItemViews: MutableList<MemeItemView> = mutableListOf()

    lateinit var textEditInterface:TextEditInterface
    constructor(context: Context) : super(context) {
        init()

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    private fun init() {
        textEditInterface=object :TextEditInterface{
            override fun onTextColorChanged(color: Int) {
                (selectedView as MemeTextView).color=color
            }

            override fun onTextFontChanged(typeface: Typeface) {
                (selectedView as MemeTextView).typeface=typeface
            }

            override fun onTextSetBold(bold: Boolean) {

            }

            override fun onTextSetItalic(italic: Boolean) {

            }

            override fun onTextSetAllCap(allCap: Boolean) {

            }

            override fun onTextSetStroked(stroked: Boolean) {

            }

            override fun onTextStrokeChanged(strokeSize: Float) {

            }

            override fun onTextStrokrColorChanged(strokeColor: Int) {

            }

            override fun onTextSizeChanged(size: Float) {

            }

        }
    }

    fun addMemeItemView(child: MemeItemView) {
        super.addView(child)
        selectedView=child
        invalidate()
    }

    fun removeMemeItemView(child: MemeItemView) {
        super.removeView(child)
    }

    override fun onDraw(canvas: Canvas?) {
        background.draw(canvas)
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