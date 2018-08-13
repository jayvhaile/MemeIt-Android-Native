package com.innov8.memegenerator.memeEngine


import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

/**
 * Created by Haile on 5/19/2018.
 */

class MemeEditorView : ViewGroup, MemeEditorInterface {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private val aspectRatio: Float = 0.toFloat()
    private var selectedView: MemeItemView? = null

    lateinit var textEditInterface: TextEditInterface
    lateinit var selectionListner:(memeItemView:MemeItemView)->Unit
    private fun init() {
        textEditInterface = object : TextEditInterface {
            override fun onTextColorChanged(color: Int) {
                (selectedView as MemeTextView).color = color
            }

            override fun onTextFontChanged(typeface: Typeface) {
                (selectedView as MemeTextView).typeface = typeface
            }

            override fun onTextSetBold(bold: Boolean) {

            }

            override fun onTextSetItalic(italic: Boolean) {

            }

            override fun onTextSetAllCap(allCap: Boolean) {
                (selectedView as MemeTextView).allCaps = allCap
            }

            override fun onTextSetStroked(stroked: Boolean) {
                (selectedView as MemeTextView).stroke = stroked
            }

            override fun onTextStrokeChanged(strokeSize: Float) {
                (selectedView as MemeTextView).strokeWidth = strokeSize
            }

            override fun onTextStrokrColorChanged(strokeColor: Int) {
                (selectedView as MemeTextView).strokeColor = strokeColor
            }

            override fun onTextSizeChanged(size: Float) {
                (selectedView as MemeTextView).textSize = size
            }

        }
        selectionListner={
            selectedView?.setItemSelected(false)
            selectedView=it
        }
    }

    fun addMemeItemView(child: MemeItemView) {
        super.addView(child)
        child.onSelection=selectionListner
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

    override fun onEditTypeChanged(editType: EditType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

