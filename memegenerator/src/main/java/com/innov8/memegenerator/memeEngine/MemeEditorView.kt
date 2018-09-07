package com.innov8.memegenerator.memeEngine


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.innov8.memegenerator.models.MemeTemplate
import com.innov8.memegenerator.models.MyTypeFace
import com.innov8.memegenerator.models.TextProperty
import com.innov8.memegenerator.models.TextStyleProperty
import com.innov8.memegenerator.utils.getDrawableIdByName
import com.innov8.memegenerator.utils.loadBitmap
import com.innov8.memegenerator.utils.log

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

    var itemSelectedInterface: ItemSelectedInterface? = null
    private val aspectRatio: Float = 0.toFloat()
    private var selectedView: MemeItemView? = null


    lateinit var textEditListener: TextEditListener
    lateinit var selectionListner: (memeItemView: MemeItemView) -> Unit
    private fun init() {
        textEditListener = object : TextEditListener {
            override fun onApplyAll(textStyleProperty: TextStyleProperty, applySize: Boolean) {
                (selectedView as MemeTextView?)?.applyTextStyleProperty(textStyleProperty, applySize)
            }

            override fun onTextColorChanged(color: Int) {
                (selectedView as MemeTextView?)?.setTextColor(color)
            }

            override fun onTextFontChanged(typeface: MyTypeFace) {
                (selectedView as MemeTextView?)?.setTypeface(typeface)
            }

            override fun onTextSetBold(bold: Boolean) {

            }

            override fun onTextSetItalic(italic: Boolean) {

            }

            override fun onTextSetAllCap(allCap: Boolean) {
                (selectedView as MemeTextView?)?.setAllCaps(allCap)
            }

            override fun onTextSetStroked(stroked: Boolean) {
                (selectedView as MemeTextView?)?.setStroke(stroked)
            }

            override fun onTextStrokeChanged(strokeSize: Float) {
                (selectedView as MemeTextView?)?.setStrokeWidth(strokeSize)
            }

            override fun onTextStrokrColorChanged(strokeColor: Int) {
                (selectedView as MemeTextView?)?.setStrokeColor(strokeColor)
            }

            override fun onTextSizeChanged(size: Float) {
                (selectedView as MemeTextView?)?.setTextSize(size)
            }

        }
        selectionListner = {
            selectedView?.setItemSelected(false)
            selectedView = it
            val item = selectedView
            when (item) {
                is MemeTextView -> itemSelectedInterface?.onTextItemSelected(item.generateTextStyleProperty())
            }
        }
    }

    fun addMemeItemView(child: MemeItemView) {
        super.addView(child)
        child.onSelection = selectionListner
    }

    fun removeMemeItemView(child: MemeItemView) {
        super.removeView(child)
    }

    var image: Bitmap? = null
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        background.draw(canvas)
        if (image != null) {
            val img: Bitmap = image!!
            val w: Float
            val h: Float
            val x: Float
            val y: Float

            val iw = img.width.toFloat()
            val ih = img.height.toFloat()
            val cw = width.toFloat()
            val ch = height.toFloat()

            val ir = iw / ih
            val cr = cw / ch

            if (ir < cr) {
                val hr = ch / ih
                w = iw * hr
                h = ch
                x = (cw / 2.0f) - (w / 2.0f)
                y = 0f
            } else {
                val wr = cw / iw
                w = cw
                h = ih * wr
                x = 0f
                y = (ch / 2f) - (h / 2f)
            }

            rect = Rect(x.toInt(), y.toInt(), x.toInt() + w.toInt(), y.toInt() + h.toInt())
            canvas.drawBitmap(img, null, rect, null)


        }
        super.onDraw(canvas)
    }

    var rect = Rect(0, 0, 0, 0)
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
            child.layout(curLeft, curTop, curRight  , curBottom)
        }
    }

    override fun onEditTypeChanged(editType: EditType) {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun removeSelectedItem(clazz: (Class<out MemeItemView>) = MemeItemView::class.java) {
        if (selectedView != null && selectedView!!.javaClass == clazz) {
            removeMemeItemView(selectedView!!)
        }
    }

    fun clearMemeItems() {
        removeAllViews()
    }


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


    fun loadMemeTemplate(memeTemplate: MemeTemplate) {
        Handler().postDelayed({
            clearMemeItems()
            image = context.loadBitmap(context.getDrawableIdByName(memeTemplate.imageURL), .3f)
            memeTemplate.textProperties.forEach {
                val memeTextView = MemeTextView(context)
                addMemeItemView(memeTextView)
                memeTextView.applyTextProperty(it, width.toFloat(), height.toFloat())

            }
        }, 300)
    }

    fun loadBitmab(bitmap: Bitmap) {
        clearMemeItems()
        image = bitmap
    }

    fun captureMeme(): Bitmap {
        selectedView?.setItemSelected(false)
        isDrawingCacheEnabled = true
        val bitmap = getDrawingCache(true).copy(Bitmap.Config.ARGB_8888, false)
        log("view", width, height)
        log("bitmap", bitmap.width, bitmap.height)

        val rb = Bitmap.createBitmap(bitmap, rect.left, rect.top,
                rect.right - rect.left, rect.bottom - rect.top)
        log("bitmap", rb.width, rb.height)

        destroyDrawingCache()
        return rb
    }

    fun getRatio(): Float {
        val w = rect.right - rect.left
        val h = rect.bottom - rect.top

        return w.toFloat() / h
    }

    fun getTexts(): List<String> {
        val x = mutableListOf<String>()
        for (i in 0 until childCount) {
            val v = getChildAt(i)
            if (v is MemeTextView) {
                x.add(v.text)
            }
        }
        return x
    }
}

fun Float.minmax(min: Float, max: Float): Float {
    return if (this < min) min else if (this > max) max else this
}

