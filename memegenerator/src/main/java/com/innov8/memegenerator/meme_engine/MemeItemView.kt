package com.innov8.memegenerator.meme_engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View


open class MemeItemView : View {

    lateinit var upaint: Paint
    //lateinit var gestureDetector: GestureDetector
    var maxWidth: Int = 1080
    var maxHeight: Int = 800
    var memeItemWidth: Int = 0
        set(value) {
            field=if (x + value > maxWidth) (maxWidth - x).toInt() else if (value < minimumWidth) minimumWidth else value
        }
    var memeItemHeight: Int = 0
        set(value) {
            field=if (y + value > maxHeight) (maxHeight - y).toInt() else if (value < minimumHeight) minimumHeight else value
        }
    var onClickListener:(()->Unit)?=null
    protected var onResize:((width:Int,height:Int)->Unit)?=null
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

    var mDetector = GestureDetector(context,  mListener());
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



    private var resizeOffset = 100
    var resizeMode = true
        set(value) {
            field = value
            invalidate()
        }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mDetector.onTouchEvent(event)
    }





    fun Number.isBetween(a: Number, b: Number): Boolean {
        var d = this.toDouble();
        return d >= a.toDouble() && d <= b.toDouble()
    }

    internal inner class mListener : GestureDetector.SimpleOnGestureListener() {
        private var dx = 0f
        private var dy = 0f
        private var type = 0
        override fun onDown(event: MotionEvent): Boolean {
            if (resizeMode && event.rawX.isBetween(x + width - resizeOffset, x + width) &&
                    event.rawY.isBetween(y + 60 + height - resizeOffset, y + 60 + height)) {//todo the 60 is added to account for the topbar fix it
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
            onResize?.invoke(memeItemWidth,memeItemHeight)
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
