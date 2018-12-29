package com.innov8.memegenerator.memeEngine

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.memeit.backend.models.MemeItemProperty
import com.innov8.memegenerator.R
import com.innov8.memegenerator.utils.contains
import com.innov8.memegenerator.utils.enlarge
import com.innov8.memeit.commons.dp
import com.innov8.memeit.commons.loadBitmap


abstract class MemeItemView : View {
    protected val isInMemeEditor: Boolean

    var controlsSize = 20f.dp(context)
    val leftResizeRectF by lazy { RectF() }
    val topResizeRectF by lazy { RectF() }
    val rightResizeRectF by lazy { RectF() }
    val bottomResizeRectF by lazy { RectF() }
    val deleteRect by lazy { RectF() }
    val rotateRect by lazy { RectF() }
    val copyRect by lazy { RectF() }
    val resizeRect by lazy { RectF() }
    val innerRect by lazy { RectF() }


    private val deleteB = context.loadBitmap(R.drawable.icon_delete, controlsSize.toInt())
    private val rotateB = context.loadBitmap(R.drawable.icon_rotate, controlsSize.toInt())
    private val copyB = context.loadBitmap(R.drawable.icon_copy, controlsSize.toInt())
    private val resizeB = context.loadBitmap(R.drawable.icon_resize, controlsSize.toInt())


    private val gestureDetector: GestureDetector by lazy { GestureDetector(context, GestureListener()) }
    private val paint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1f.dp(context)
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.BUTT
            color = Color.WHITE
        }
    }

    var itemWidth: Int = 0
    var itemHeight: Int = 0


    var onClickListener: ((MemeItemView) -> Unit)? = null
    var onCopyListener: ((MemeItemView) -> Unit)? = null
    var onRemoveListener: ((MemeItemView) -> Unit)? = null
    protected var onResize: ((width: Int, height: Int) -> Unit)? = null
    private var resizeOffset = 0
    private var topOffset = 0
    private val resizeRectSize = 8.dp(context)

    var maxWidth = 0
    var maxHeight = 0

    /*open fun onMaxWidthChanged(old: Int, new: Int) {
        if (old == 0 || new == 0) {
            requestLayout()
            return
        }
        itemWidth = (itemWidth * (new.toFloat() / old)).toInt()
        requestLayout()
    }

    open fun onMaxHeightChanged(old: Int, new: Int) {
        if (old == 0 || new == 0) {
            requestLayout()
            return
        }
        itemHeight = (itemHeight * (new.toFloat() / old)).toInt()
        requestLayout()
    }*/

    val itemX: Float
        get() = controlsSize / 2f

    val itemY: Float
        get() = controlsSize / 2f


    constructor(context: Context, memeItemWidth: Int, memeItemHeight: Int) : super(context) {
        isInMemeEditor = true
        this.itemWidth = memeItemWidth
        this.itemHeight = memeItemHeight
        init()
    }

    constructor(context: Context, memeItemProperty: MemeItemProperty) : super(context) {
        isInMemeEditor = true
        applyProperty(memeItemProperty)
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


    private fun init() {
        isFocusable = isInMemeEditor
        isFocusableInTouchMode = isFocusable
        minimumWidth = 50f.dp(context).toInt()
        minimumHeight = 50f.dp(context).toInt()
        resizeOffset = 28f.dp(context).toInt()
        topOffset = (24f).dp(context).toInt()
    }

    private var tempProperty: MemeItemProperty? = null
    fun applyProperty(memeItemProperty: MemeItemProperty) {
        if (maxWidth == 0 || maxHeight == 0)
            tempProperty = memeItemProperty
        else {
            this.x = memeItemProperty.x * maxWidth
            this.y = memeItemProperty.y * maxHeight
            this.rotation = memeItemProperty.r
            this.itemWidth = (memeItemProperty.w * maxWidth).toInt()
            this.itemHeight = (memeItemProperty.h * maxHeight).toInt()
            tempProperty = null
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (isEnabled) gestureDetector.onTouchEvent(event) else false
    }

    override fun onDraw(canvas: Canvas) {
        if (isInMemeEditor && isFocused) {
            paint.style = Paint.Style.STROKE

            canvas.drawRect(innerRect, paint)
            paint.style = Paint.Style.FILL
            canvas.apply {
                drawRect(leftResizeRectF, paint)
                drawRect(topResizeRectF, paint)
                drawRect(rightResizeRectF, paint)
                drawRect(bottomResizeRectF, paint)
                drawBitmap(deleteB, null, deleteRect, null)
                drawBitmap(rotateB, null, rotateRect, null)
                drawBitmap(copyB, null, copyRect, null)
                drawBitmap(resizeB, null, resizeRect, null)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var w = MeasureSpec.getSize(widthMeasureSpec)
        var h = MeasureSpec.getSize(heightMeasureSpec)
        val wSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val hSpecMode = MeasureSpec.getMode(heightMeasureSpec)

        maxWidth = w
        maxHeight = h

        tempProperty?.let {
            applyProperty(it)
        }

        val hPadding = paddingLeft + paddingRight
        val vPadding = paddingTop + paddingBottom
        if (wSpecMode == MeasureSpec.EXACTLY) {
            itemWidth = (w - controlsSize - hPadding).toInt()
        } else {
            w = (itemWidth + controlsSize + paddingLeft + paddingRight).toInt()
        }
        if (hSpecMode == MeasureSpec.EXACTLY) {
            itemHeight = (h - controlsSize - vPadding).toInt()
        } else {
            h = (itemHeight + controlsSize + vPadding).toInt()
        }
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        update()
    }


    open fun copy(): MemeItemView? = null


    private fun update() {
        val rsh = (resizeRectSize) / 2f
        deleteRect.set(0f, 0f, controlsSize, controlsSize)
        rotateRect.set(width - controlsSize, 0f, width.toFloat(), controlsSize)
        copyRect.set(0f, height - controlsSize, controlsSize, height.toFloat())
        resizeRect.set(width - controlsSize, height - controlsSize, width.toFloat(), height.toFloat())
        innerRect.set(itemX, itemY, width - itemX, height - itemY)
        leftResizeRectF.set(itemX - rsh, height / 2 - rsh, itemX + rsh, height / 2 + rsh)
        topResizeRectF.set(width / 2f - rsh, itemY - rsh, width / 2 + rsh, itemY + rsh)
        rightResizeRectF.set(width - itemX - rsh, height / 2 - rsh, width - itemX + rsh, height / 2 + rsh)
        bottomResizeRectF.set(width / 2f - rsh, height - itemY - rsh, width / 2 + rsh, height - itemY + rsh)
        invalidate()
    }


    abstract fun generateProperty(): MemeItemProperty

    internal inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private var dx = 0f
        private var dy = 0f
        private var r1 = 0.0
        private var type = 0
        private var selectedBefore: Boolean = false
        private val resizeEnlarge = 5f.dp(context)
        override fun onDown(event: MotionEvent): Boolean {
            selectedBefore = isFocused
            requestFocus()
            return when (event) {
                in rotateRect -> {
                    dx = pivotX + x
                    dy = pivotY + y + 56.dp(context)//todo change 56 to the top margin of the memeEditorView
                    r1 = Math.toDegrees(Math.atan2((event.rawY - dy).toDouble(), (event.rawX - dx).toDouble())) + 180
                    type = TYPE_ROTATE
                    true
                }
                in resizeRect -> {
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
                    val i = rectList.indexOf(rectList.find { event in it })

                    when {
                        i != -1 -> {
                            type = i + 1
                            dx = event.rawX
                            dy = event.rawY
                            true
                        }
                        event in innerRect -> {
                            dx = x - event.rawX
                            dy = y - event.rawY
                            type = TYPE_DRAG
                            true
                        }
                        else -> event in deleteRect || event in copyRect
                    }
                }
            }

        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return if (isInMemeEditor) {
                if (e in deleteRect) {
                    onRemoveListener?.invoke(this@MemeItemView)

                    true
                } else if (e in copyRect) {
                    onCopyListener?.invoke(this@MemeItemView)
                    true
                } else if (e in innerRect && selectedBefore) {
                    onClickListener?.invoke(this@MemeItemView)
                    true
                } else
                    false
            } else {
                onClickListener?.invoke(this@MemeItemView)
                true
            }
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            return super.onDoubleTap(e)
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


            val r = -1 * rotation * Math.PI / 180


            val x1 = (xx * Math.cos(r) - yy * Math.sin(r)).toInt()
            val y1 = (xx * Math.sin(r) + yy * Math.cos(r)).toInt()
            if (type == TYPE_RESIZE) {


                itemWidth += x1
                itemHeight += y1
                requestLayout()

            } else {
                when (type) {
                    TYPE_LEFT_RESIZE -> {
                        itemWidth -= x1
                        x += x1
                    }
                    TYPE_TOP_RESIZE -> {
                        itemHeight -= y1
                        y += y1
                    }
                    TYPE_RIGHT_RESIZE -> {
                        itemWidth += x1
                        requestLayout()
                    }
                    TYPE_BOTTOM_RESIZE -> {
                        itemHeight += y1
                        requestLayout()
                    }

                }
            }
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
            x = nx
            y = ny
        }
    }

    companion object {
        const val TYPE_DRAG = 0
        const val TYPE_LEFT_RESIZE = 1
        const val TYPE_TOP_RESIZE = 2
        const val TYPE_RIGHT_RESIZE = 3
        const val TYPE_BOTTOM_RESIZE = 4
        const val TYPE_ROTATE = 5
        const val TYPE_RESIZE = 6
    }

}
