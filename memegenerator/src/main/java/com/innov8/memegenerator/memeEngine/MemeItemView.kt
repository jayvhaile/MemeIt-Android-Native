package com.innov8.memegenerator.memeEngine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.innov8.memegenerator.utils.fromDP


open class MemeItemView : View {
    protected val isInMemeEditor: Boolean

    constructor(context: Context, memeItemWidth: Int, memeItemHeight: Int) : super(context) {
        isInMemeEditor = true
        this.memeItemWidth = memeItemWidth
        this.memeItemHeight = memeItemHeight
        init()

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        isInMemeEditor = false
        init()

    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        isInMemeEditor = false
        init()

    }

    lateinit var mDetector: GestureDetector
    lateinit var upaint: Paint

    var maxWidth: Int = 2000
        set(value) {
            field = value
            memeItemWidth = memeItemWidth
        }

    var maxHeight: Int = 2000
        set(value) {
            field = value
            memeItemHeight = memeItemHeight
        }
    var memeItemWidth: Int = 0
        get() =
            if (isInMemeEditor) field
            else maxWidth
        set(value) {
            field = if (x + value > maxWidth) (maxWidth - x).toInt() else if (value < minimumWidth) minimumWidth else value
        }
    var memeItemHeight: Int = 0
        get() =
            if (isInMemeEditor) field
            else maxHeight
        set(value) {
            field = if (y + value > maxHeight) (maxHeight - y).toInt() else if (value < minimumHeight) minimumHeight else value
        }
    var onClickListener: (() -> Unit)? = null
    protected var onResize: ((width: Int, height: Int) -> Unit)? = null
    var onSelection: ((memeItemView: MemeItemView) -> Unit)? = null
    private var resizeOffset=0
    private var topOffeset=0
    private fun init() {
        upaint = Paint(Paint.ANTI_ALIAS_FLAG)
        upaint.style = Paint.Style.STROKE
        upaint.strokeWidth = 5f
        upaint.strokeJoin = Paint.Join.ROUND
        upaint.strokeCap = Paint.Cap.BUTT
        upaint.color = Color.argb(60, 255, 255, 255)
        minimumWidth = 50f.fromDP(context).toInt()
        minimumHeight = 30f.fromDP(context).toInt()
        mDetector = GestureDetector(context, MyListener())
        resizeOffset = 28f.fromDP(context).toInt()
        topOffeset = 24f.fromDP(context).toInt()
    }

    override fun onDraw(canvas: Canvas?) {
        if (itemSelected) {
            val rect = Rect(0, 0, width, height)
            val rect2 = Rect(width - resizeOffset, height - resizeOffset, width, height)
            canvas?.drawRect(rect, upaint)
            canvas?.drawRect(rect2, upaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        maxWidth = MeasureSpec.getSize(widthMeasureSpec)
        maxHeight = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(resolveSizeAndState(memeItemWidth, widthMeasureSpec, 1),
                resolveSizeAndState(memeItemHeight, heightMeasureSpec, 1))
    }


    var itemSelected = false
        private set(value) {
            field = value
        }


    fun setItemSelected(selected: Boolean, fromUser: Boolean = false) {
        if (selected == itemSelected) return
        itemSelected = selected
        if (fromUser and selected) onSelection?.invoke(this)
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mDetector.onTouchEvent(event)
    }

    fun Number.isBetween(a: Number, b: Number): Boolean {
        var d = this.toDouble()
        return d >= a.toDouble() && d <= b.toDouble()

    }

    internal inner class MyListener : GestureDetector.SimpleOnGestureListener() {
        private var dx = 0f
        private var dy = 0f
        private var type = 0
        private var selectedBefore: Boolean = false
        override fun onDown(event: MotionEvent): Boolean {
            selectedBefore = itemSelected
            setItemSelected(true, true)
            if (event.rawX.isBetween(x + width - resizeOffset, x + width) &&
                    event.rawY.isBetween(y + topOffeset + height - resizeOffset, y + topOffeset + height)) {//todo the 60 is added to account for the topbar fix it
                type = 1
                dx = event.rawX
                dy = event.rawY
            } else {
                dx = x - event.rawX
                dy = y - event.rawY
                type = 0
            }
            return true
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            if (!isInMemeEditor or selectedBefore)
                onClickListener?.invoke()
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (type == 0) {
                onDrag(e2!!)
            } else if (type == 1) {
                onResize(e2!!)
            }
            return true
        }

        private fun onResize(event: MotionEvent) {
            var nw = memeItemWidth + (event.rawX - dx).toInt()
            var nh = memeItemHeight + (event.rawY - dy).toInt()
            memeItemWidth = nw
            memeItemHeight = nh
            onResize?.invoke(memeItemWidth, memeItemHeight)
            dx = event.rawX
            dy = event.rawY
            requestLayout()

        }

        private fun onDrag(event: MotionEvent) {
            var nx = event.rawX + dx
            var ny = event.rawY + dy
            x = if (nx < 0) 0f else if (nx + width > maxWidth) (maxWidth - width).toFloat() else nx
            y = if (ny < 0) 0f else if (ny + height > maxHeight) (maxHeight - height).toFloat() else ny
        }

    }
}
