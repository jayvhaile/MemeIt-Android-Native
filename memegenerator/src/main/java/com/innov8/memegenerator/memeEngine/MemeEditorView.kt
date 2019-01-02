package com.innov8.memegenerator.memeEngine


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toRect
import com.innov8.memegenerator.customViews.CheckerBoardDrawable
import com.memeit.backend.models.MemeStickerItemProperty
import com.memeit.backend.models.LoadedImageMemeTemplateProperty
import com.memeit.backend.models.LoadedMemeTemplateProperty
import com.memeit.backend.models.MemeTextItemProperty
import com.innov8.memegenerator.interfaces.EditorStateChangedListener
import com.innov8.memegenerator.interfaces.ItemSelectedInterface
import com.innov8.memegenerator.utils.CloseableFragment
import com.innov8.memegenerator.utils.capture
import com.innov8.memeit.commons.dp

/**
 * Created by Haile on 5/19/2018.
 */

class MemeEditorView : ViewGroup, EditorStateChangedListener {
    override fun onEditorOpened(tag: String, cf: CloseableFragment) {
        enablePaint = tag == "paint"

        when (tag) {
            "text" -> {
                if (focusedItem !is MemeTextView) {
                    var selected = false
                    for (i in 0 until childCount) {
                        val it = getChildAt(i)
                        if (it is MemeTextView) {
                            it.requestFocus()
                            selected = true
                            break
                        }
                    }
                    if (!selected) requestFocus()
                }
            }
            "sticker" -> {
                if (focusedItem !is MemeStickerView) {
                    var selected = false
                    for (i in 0 until childCount) {
                        val it = getChildAt(i)
                        if (it is MemeStickerView) {
                            it.requestFocus()
                            selected = true
                            break
                        }
                    }
                    if (!selected) requestFocus()
                }
            }
        }
    }

    override fun onEditorClosed() {
        enablePaint = false
    }

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
        set(value) {
            field = value
            field?.apply {
                updateSize(width, height)
            }
            invalidate()
        }
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val paintHandler = PaintHandler(context).apply {
        onInvalidate = { invalidate() }
    }

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


    fun setLayout(memeLayout: MemeLayout?) {
        this.memeLayout = memeLayout
        this.memeLayout?.updateSize(width, height)
        this.memeLayout?.invalidate = {
            paint.color = this.memeLayout?.backgroudColor ?: Color.WHITE
            invalidate()
        }
        this.memeLayout?.invalidate?.invoke() ?: invalidate()

    }

    override fun onDraw(canvas: Canvas) {
        background.draw(canvas)
        memeLayout?.let {
            canvas.drawRect(it.drawingRect, paint)
            for (i in 0 until it.count) {
                canvas.drawBitmap(it.images[i],
                        null,
                        it.getDrawingRectAt(i),
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

    var enablePaint = false
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        memeLayout?.updateSize(w, h)
    }

    /*fun loadMemeTemplate(memeTemplate: MemeTemplate) {
        clearMemeItems()
        val image = context.loadBitmap(context.getDrawableIdByName(memeTemplate.imageURL), 1f)
        val memeLayout = SingleImageLayout(image).apply { updateSize(width, height) }
        setLayout(memeLayout)
        Handler().postDelayed({
            val rect = memeLayout.drawingRect

            memeTemplate.textProperties.forEach {
                val memeTextView = MemeTextView(context)
                memeTextView.applyTextProperty(it, rect.width(), rect.height(), rect.left, rect.top)
                addMemeItemView(memeTextView)
            }
        }, 300)

    }*/


    fun loadBitmab(bitmap: Bitmap) {
        clearMemeItems()
        setLayout(SingleImageLayout(bitmap))
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

    fun generateProperty(): LoadedMemeTemplateProperty {
        return LoadedImageMemeTemplateProperty(
                memeLayout!!.generateProperty(),
                List(childCount) { getChildAt(it) }
                        .map { it as MemeItemView }
                        .map { it.generateProperty() },
                memeLayout!!.images,
                captureMeme()
        )
    }

    fun applyProperty(memeTemplateProperty: LoadedMemeTemplateProperty) {
        clearMemeItems()
        setLayout(MemeLayout.fromProperty(memeTemplateProperty.images, memeTemplateProperty.layoutProperty))

        memeTemplateProperty.memeItemsProperty.forEach {
            addMemeItemView(when (it) {
                is MemeTextItemProperty -> MemeTextView(context, it)
                is MemeStickerItemProperty -> MemeStickerView(context, it)
            })
        }
    }
}


