package com.innov8.memegenerator.MemeEngine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.innov8.memegenerator.R
import com.innov8.memegenerator.utils.dp
import com.innov8.memegenerator.utils.enlarge
import com.innov8.memegenerator.utils.inRect
import com.innov8.memegenerator.utils.loadBitmap


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

    fun setmw(value: Int): Int {
        val m = memeItemWidth
        memeItemWidth = value
        return m - memeItemWidth
    }

    fun setmh(value: Int): Int {
        val m = memeItemHeight
        memeItemHeight = value
        return m - memeItemHeight
    }

    var onClickListener: ((MemeItemView) -> Unit)? = null
    var onCopyListener: ((MemeItemView) -> Unit)? = null
    var onRemoveListener: ((MemeItemView) -> Unit)? = null
    protected var onResize: ((width: Int, height: Int) -> Unit)? = null
    private var resizeOffset = 0
    private var topOffeset = 0
    val resizeRectSize = 8.dp(context)
    private fun init() {
        isFocusable=true
        isFocusableInTouchMode=true
        upaint = Paint(Paint.ANTI_ALIAS_FLAG)
        upaint.style = Paint.Style.STROKE
        upaint.strokeWidth = 1f.dp(context)
        upaint.strokeJoin = Paint.Join.ROUND
        upaint.strokeCap = Paint.Cap.BUTT
        upaint.color = Color.WHITE
        minimumWidth = 50f.dp(context).toInt()
        minimumHeight = 50f.dp(context).toInt()
        mDetector = GestureDetector(context, MyListener())
        resizeOffset = 28f.dp(context).toInt()
        topOffeset = (24f).dp(context).toInt()
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mDetector.onTouchEvent(event)
    }

    fun Number.isBetween(a: Number, b: Number): Boolean {
        var d = this.toDouble()
        return d >= a.toDouble() && d <= b.toDouble()

    }

    open fun copy(): MemeItemView? {
        return null
    }


    override fun onDraw(canvas: Canvas?) {
        if (isInMemeEditor && isFocused) {
            upaint.style = Paint.Style.STROKE

            canvas?.drawRect(innerRect, upaint)
            upaint.style = Paint.Style.FILL

            canvas?.drawRect(leftResizeRectF, upaint)
            canvas?.drawRect(topResizeRectF, upaint)
            canvas?.drawRect(rightResizeRectF, upaint)
            canvas?.drawRect(bottomResizeRectF, upaint)

            canvas?.drawBitmap(deleteB, null, deleteRect, null)
            canvas?.drawBitmap(rotateB, null, rotateRect, null)
            canvas?.drawBitmap(copyB, null, copyRect, null)
            canvas?.drawBitmap(resizeB, null, resizeRect, null)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        maxWidth = MeasureSpec.getSize(widthMeasureSpec)
        maxHeight = MeasureSpec.getSize(heightMeasureSpec)
        val rsh = resizeRectSize / 2
        val w: Int = (memeItemWidth + controlsSize).toInt()
        val h: Int = (memeItemHeight + controlsSize).toInt()
        setMeasuredDimension(resolveSizeAndState(w, widthMeasureSpec, 1),
                resolveSizeAndState(h, heightMeasureSpec, 1))
    }

    val itemX: Float
        get() = controlsSize / 2

    val itemY: Float
        get() = controlsSize / 2f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        update()

    }

    var controlsSize = 20f.dp(context)
    var leftResizeRectF = RectF()
    var topResizeRectF = RectF()
    var rightResizeRectF = RectF()
    var bottomResizeRectF = RectF()
    var deleteRect = RectF()
    var rotateRect = RectF()
    var copyRect = RectF()
    var resizeRect = RectF()
    var innerRect = RectF()


    private val deleteB = context.loadBitmap(R.drawable.icon_delete, controlsSize.toInt())
    private val rotateB = context.loadBitmap(R.drawable.icon_rotate, controlsSize.toInt())
    private val copyB = context.loadBitmap(R.drawable.icon_copy, controlsSize.toInt())
    private val resizeB = context.loadBitmap(R.drawable.icon_resize, controlsSize.toInt())

    private fun update() {
        val rsh = (resizeRectSize) / 2f


        deleteRect = RectF(0f, 0f, controlsSize, controlsSize)
        rotateRect = RectF(width - controlsSize, 0f, width.toFloat(), controlsSize)
        copyRect = RectF(0f, height - controlsSize, controlsSize, height.toFloat())
        resizeRect = RectF(width - controlsSize, height - controlsSize, width.toFloat(), height.toFloat())

        innerRect = RectF(itemX, itemY, width - itemX, height - itemY)

        leftResizeRectF = RectF(itemX - rsh, height / 2 - rsh, itemX + rsh, height / 2 + rsh)
        topResizeRectF = RectF(width / 2f - rsh, itemY - rsh, width / 2 + rsh, itemY + rsh)
        rightResizeRectF = RectF(width - itemX - rsh, height / 2 - rsh, width - itemX + rsh, height / 2 + rsh)
        bottomResizeRectF = RectF(width / 2f - rsh, height - itemY - rsh, width / 2 + rsh, height - itemY + rsh)
        invalidate()
    }


    private val absWidth: Float
        get() = width * scaleX

    private val absHeight: Float
        get() = height * scaleY

    private fun sqr(x: Float, y: Float): Float = Math.sqrt(((x * x) + (y * y)).toDouble()).toFloat()

    companion object {
        const val TYPE_DRAG = 0
        const val TYPE_LEFT_RESIZE = 1
        const val TYPE_TOP_RESIZE = 2
        const val TYPE_RIGHT_RESIZE = 3
        const val TYPE_BOTTOM_RESIZE = 4
        const val TYPE_ROTATE = 5
        const val TYPE_RESIZE = 6
    }

    internal inner class MyListener : GestureDetector.SimpleOnGestureListener() {


        private var dx = 0f
        private var dy = 0f
        private var r1 = 0.0
        private var type = 0
        private var selectedBefore: Boolean = false
        private val resizeEnlarge = 5f.dp(context)
        override fun onDown(event: MotionEvent): Boolean {
            selectedBefore =isFocused
            requestFocus()
            return when {
                event.inRect(rotateRect) -> {
                    dx = pivotX + x
                    dy = pivotY + y + 56.dp(context)//todo change 56 to the top margin of the memeEditorView
                    r1 = Math.toDegrees(Math.atan2((event.rawY - dy).toDouble(), (event.rawX - dx).toDouble())) + 180
                    type = TYPE_ROTATE
                    true
                }
                event.inRect(resizeRect) -> {
                    type = TYPE_RESIZE
                    dx = event.rawX
                    dy = event.rawY
                    true
                }
                else -> {
                    val rectList = listOf(leftResizeRectF, topResizeRectF, rightResizeRectF, bottomResizeRectF)
                            .map {
                                it.enlarge(resizeEnlarge)
                            }
                    val i = rectList.indexOf(rectList.find { event.inRect(it) })

                    when {
                        i != -1 -> {
                            type = i + 1
                            dx = event.rawX
                            dy = event.rawY
                            true
                        }
                        event.inRect(innerRect) -> {
                            dx = x - event.rawX
                            dy = y - event.rawY
                            type = TYPE_DRAG
                            true
                        }
                        else -> event.inRect(deleteRect) || event.inRect(copyRect)
                    }
                }
            }

        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return if (isInMemeEditor) {
                if (e.inRect(deleteRect)) {
                    onRemoveListener?.invoke(this@MemeItemView)

                    true
                } else if (e.inRect(copyRect)) {
                    onCopyListener?.invoke(this@MemeItemView)
                    true
                } else if (e.inRect(innerRect) && selectedBefore) {
                    onClickListener?.invoke(this@MemeItemView)
                    true
                } else
                    false
            } else {
                onClickListener?.invoke(this@MemeItemView)
                true
            }
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            return if (isInMemeEditor)
                when (type) {
                    TYPE_DRAG -> {
                        onDrag(e2)
                        true
                    }
                    TYPE_RESIZE, TYPE_LEFT_RESIZE, TYPE_RIGHT_RESIZE, TYPE_TOP_RESIZE, TYPE_BOTTOM_RESIZE -> {
                        onResize(e2)
                        true
                    }
                    TYPE_ROTATE -> {
                        onRotate(e2)
                        true
                    }
                    else -> false
                }
            else false
        }

        private fun onResize(event: MotionEvent) {
            val xx = event.rawX - dx
            val yy = event.rawY - dy

            if (type == TYPE_RESIZE) {
                x+=setmw(memeItemWidth+(xx*2).toInt())/2
                y+=setmh(memeItemHeight+(yy*2).toInt())/2

            } else {
                val r = rotation * Math.PI / 180
                val sx: Int = (xx * Math.cos(r) + yy * Math.sin(r)).toInt()
                val sy: Int = (xx * Math.sin(r) + yy * Math.cos(r)).toInt()
                when (type) {
                    TYPE_LEFT_RESIZE -> {
                        x += setmw(memeItemWidth - sx)
                    }
                    TYPE_TOP_RESIZE -> {
                        y += setmh(memeItemHeight - sy)
                    }
                    TYPE_RIGHT_RESIZE -> {
                        memeItemWidth += sx
                    }
                    TYPE_BOTTOM_RESIZE -> memeItemHeight += sy

                }
            }
            onResize?.invoke(memeItemWidth, memeItemHeight)
            dx = event.rawX
            dy = event.rawY
            requestLayout()
        }

        private fun onRotate(event: MotionEvent) {
            val r = Math.toDegrees(Math.atan2((event.rawY - dy).toDouble(), (event.rawX - dx).toDouble())) + 180
            rotation += (r - r1).toFloat()
            r1 = r
        }

        private fun onDrag(event: MotionEvent) {
            val nx = event.rawX + dx
            val ny = event.rawY + dy
            x = if (nx < 0) 0f else if (nx + width > maxWidth) (maxWidth - width).toFloat() else nx
            y = if (ny < 0) 0f else if (ny + height > maxHeight) (maxHeight - height).toFloat() else ny
        }
    }
}
