package com.innov8.memegenerator.memeEngine


import android.content.Context
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.innov8.memegenerator.models.MemeTemplate
import com.innov8.memegenerator.models.MyTypeFace
import com.innov8.memegenerator.models.TextProperty
import com.innov8.memegenerator.models.TextStyleProperty
import com.innov8.memegenerator.utils.fromDPToPX
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

    /*var image: Bitmap? = null
        set(value) {
            field = value
            invalidate()
        }*/
    private var memeLayout: MemeLayout? = null
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

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
            for (i in 0 until ml.getCount()) {
                canvas.drawBitmap(ml.images[i],
                        null,
                        ml.getDrawingRectAt(i),
                        null)
            }
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
            val image = context.loadBitmap(context.getDrawableIdByName(memeTemplate.imageURL), .3f)
            val memeLayout = SingleImage(width, height, image)
            memeLayout.topMargin = 100.fromDPToPX(context)

            memeLayout.backgroudColor = Color.WHITE

            setLayout(memeLayout)

            memeTemplate.textProperties.forEach {
                val memeTextView = MemeTextView(context)
                addMemeItemView(memeTextView)
                memeTextView.applyTextProperty(it, width.toFloat(), height.toFloat())

            }
        }, 300)
    }

    fun loadBitmab(bitmap: Bitmap) {
        clearMemeItems()
        val memeLayout = SingleImage(width, height, bitmap)
        memeLayout.topMargin = 100.fromDPToPX(context)
        memeLayout.backgroudColor = Color.YELLOW
        setLayout(memeLayout)
    }

    fun captureMeme(): Bitmap {
        selectedView?.setItemSelected(false)
        isDrawingCacheEnabled = true
        val bitmap = getDrawingCache(true).copy(Bitmap.Config.ARGB_8888, false)
        log("view", width, height)
        log("bitmap", bitmap.width, bitmap.height)

        val rect=memeLayout!!.drawingRect.toRect()
        val rb = Bitmap.createBitmap(bitmap, rect.left, rect.top,
                rect.right - rect.left, rect.bottom - rect.top)


        destroyDrawingCache()
        return rb
    }

    fun RectF.toRect():Rect{
        return Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
    }
    fun Rect.toRectF():RectF{
        return RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
    }
    fun getRatio(): Float {
        val rect=memeLayout!!.drawingRect.toRect()
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

