package com.innov8.memegenerator.meme_engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


open class MemeItemView : View {

    lateinit var upaint: Paint
    //lateinit var gestureDetector: GestureDetector
    var maxWidth: Int = 0
    var maxHeight: Int = 0
    var memeItemWidth: Int = 0
    var memeItemHeight: Int = 0

    constructor(context: Context, memeItemWidth: Int, memeItemHeight: Int) : super(context) {
        this.memeItemWidth = memeItemWidth
        this.memeItemHeight = memeItemHeight
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    private fun init() {
        upaint = Paint(Paint.ANTI_ALIAS_FLAG)
        upaint.style = Paint.Style.STROKE
        upaint.strokeWidth = 5f
        upaint.strokeJoin = Paint.Join.ROUND
        upaint.strokeCap = Paint.Cap.BUTT
        upaint.color = Color.WHITE
        minimumWidth = 120
        minimumHeight = 120
    }

    override fun onDraw(canvas: Canvas?) {
        if (resizeMode) {
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

    private var dx = 0f
    private var dy = 0f
    private var lastAction = 0
    private var type = 0

    private var resizeOffset = 100
    var resizeMode = true
        set(value) {
            field = value
            invalidate()
        }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> onDown(event)
            MotionEvent.ACTION_MOVE -> {
                if (type == 0) {
                    onDrag(event)
                } else if (type == 1) {
                    onResize(event)
                }
                lastAction = MotionEvent.ACTION_MOVE
            }
            MotionEvent.ACTION_UP -> {
                if (lastAction == MotionEvent.ACTION_DOWN) {
                    return false
                }
            }
        }
        return true
    }

    private fun onDown(event: MotionEvent) {
        if (resizeMode && event.rawX.isBetween(x + width - resizeOffset, x + width) &&
                event.rawY.isBetween(y + 60 + height - resizeOffset, y + 60 + height)) {//todo the 60 is added to account for the topbar fix it
            type = 1
            dx = event.rawX
            dy = event.rawY
            log("resize")
        } else {
            dx = x - event.rawX
            dy = y - event.rawY
            type = 0
            log("drag")
        }
        lastAction = MotionEvent.ACTION_DOWN
    }

    private fun onResize(event: MotionEvent) {
        var nw = memeItemWidth + (event.rawX - dx).toInt()
        var nh = memeItemHeight + (event.rawY - dy).toInt()
        memeItemWidth = if (x + nw > maxWidth) (maxWidth - x).toInt() else if (nw < minimumWidth) minimumWidth else nw
        memeItemHeight = if (y + nh > maxHeight) (maxHeight - y).toInt() else if (nh < minimumHeight) minimumHeight else nh
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


    fun Number.isBetween(a: Number, b: Number): Boolean {
        var d = this.toDouble();
        return d >= a.toDouble() && d <= b.toDouble()
    }

}
