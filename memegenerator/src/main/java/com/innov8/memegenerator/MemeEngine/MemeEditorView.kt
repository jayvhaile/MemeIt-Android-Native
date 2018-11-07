package com.innov8.memegenerator.MemeEngine


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.innov8.memegenerator.CustomViews.CheckerBoardDrawable
import com.innov8.memegenerator.utils.capture
import com.innov8.memegenerator.utils.toRect
import com.innov8.memeit.commons.dp
import com.innov8.memeit.commons.getDrawableIdByName
import com.innov8.memeit.commons.loadBitmap
import com.innov8.memeit.commons.models.MemeTemplate
import com.innov8.memeit.commons.models.TextProperty

/**
 * Created by Haile on 5/19/2018.
 */

class MemeEditorView : ViewGroup {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                when (v) {
                    is MemeTextView -> itemSelectedInterface?.onTextItemSelected(v.generateTextStyleProperty())
                }
            }
        }
        background = CheckerBoardDrawable(12f.dp(context), Color.LTGRAY, Color.GRAY)
        setOnClickListener { requestFocus() }
    }

    var itemSelectedInterface: ItemSelectedInterface? = null
    var focusedItem: MemeItemView? = null
        get() = focusedChild as? MemeItemView

    internal var memeLayout: MemeLayout? = null
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val paintHandler = PaintHandler(this)
    fun addMemeItemView(child: MemeItemView) {
        super.addView(child)
        child.onFocusChangeListener = onFocusChangeListener
        child.requestFocus()
        child.onRemoveListener = { removeMemeItemView(it) }
        child.onCopyListener = { it.copy()?.let { it1 -> addMemeItemView(it1) } }

    }

    fun removeMemeItemView(child: MemeItemView) {
        super.removeView(child)
    }


    fun setLayout(memeLayout: MemeLayout) {
        this.memeLayout = memeLayout
        this.memeLayout?.invalidate = {
            paint.color = this.memeLayout?.backgroudColor ?: Color.BLACK
            invalidate()
        }
        this.memeLayout?.invalidate?.invoke()
    }

    override fun onDraw(canvas: Canvas) {
        background.draw(canvas)
        if (memeLayout != null) {
            val ml = memeLayout!!
            canvas.drawRect(ml.drawingRect, paint)
            for (i in 0 until ml.count) {
                canvas.drawBitmap(ml.images[i],
                        null,
                        ml.getDrawingRectAt(i),
                        null)
            }
            paintHandler.draw(canvas)
            super.onDraw(canvas)
        }
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
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(View.MeasureSpec.makeMeasureSpec(childWidth, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(childHeight, View.MeasureSpec.AT_MOST))
            curLeft = child.left
            curTop = child.top
            curRight = curLeft + child.measuredWidth
            curBottom = curTop + child.measuredHeight
            child.layout(curLeft, curTop, curRight, curBottom)
        }
    }

    val enablePaint = true
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (enablePaint) paintHandler.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    fun removeSelectedItem(clazz: (Class<out MemeItemView>) = MemeItemView::class.java) {
        if (focusedItem != null && focusedItem!!.javaClass == clazz) {
            removeMemeItemView(focusedItem!!)
        }
    }

    fun clearMemeItems() = removeAllViews()
    fun generateAllTextProperty(): List<TextProperty> {
        val list = mutableListOf<TextProperty>()
        for (index in 0 until childCount) {
            val v = super.getChildAt(index)
            if (v is MemeTextView) {
                list.add(v.generateTextProperty(width.toFloat(), height.toFloat()))
            }
        }
        return list
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        memeLayout?.updateSize(w, h)
    }

    fun loadMemeTemplate(memeTemplate: MemeTemplate) {
        clearMemeItems()
        val image = context.loadBitmap(context.getDrawableIdByName(memeTemplate.imageURL),1f)
        val memeLayout = SingleImageLayout(width, height, image)
        setLayout(memeLayout)
        Handler().postDelayed({
            val rect = memeLayout.drawingRect

            memeTemplate.textProperties.forEach {
                val memeTextView = MemeTextView(context)
                memeTextView.applyTextProperty(it, rect.width(), rect.height(), rect.left, rect.top)
                addMemeItemView(memeTextView)
            }
        }, 300)

    }


    fun loadBitmab(bitmap: Bitmap) {
        clearMemeItems()
        val memeLayout = SingleImageLayout(width, height, bitmap)
        setLayout(memeLayout)
    }

    fun loadImageByLayout(memeLayout: MemeLayout) {
        clearMemeItems()
        setLayout(memeLayout)
    }

    fun captureMeme(): Bitmap {
        val tempFocus = focusedItem
        focusedItem?.clearFocus()
        val rect = memeLayout?.drawingRect?.toRect()
        val b = capture(rect)
        tempFocus?.requestFocus()
        return b
    }

    fun captureItems(): Bitmap {
        val tempFocus = focusedItem
        val tempBack = background
        val tempLayout = memeLayout

        val rect = memeLayout?.drawingRect?.toRect()
        focusedItem?.clearFocus()
        setBackgroundColor(Color.TRANSPARENT)
        memeLayout = null

        invalidate()
        val b = capture(rect)

        memeLayout = tempLayout
        background = tempBack
        tempFocus?.requestFocus()

        invalidate()

        return b


    }


    fun getTexts(): List<String> {
        val texts = mutableListOf<String>()
        for (i in 0 until childCount) {
            val v = getChildAt(i)
            if (v is MemeTextView) {
                texts.add(v.text)
            }
        }
        return texts
    }
}


