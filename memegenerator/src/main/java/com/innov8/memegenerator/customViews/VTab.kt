package com.innov8.memegenerator.customViews

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.innov8.memegenerator.R

class VTab : LinearLayout {

    var items: List<VTabItems> = listOf()
        set(value) {
            field = value
            init()
        }
    var selectedTint: Int = 0
    var selectedBackgroundTint: Int = 0
    var itemColor: Int = 0
    var itemBackgroundColor: Int = 0
    var itemPadding: Int = 0
    lateinit var itemlayoutParam: LayoutParams

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setup(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup(context, attrs)

    }
    private fun setup(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.VTab, 0, 0)
        try {
            itemColor = a.getColor(R.styleable.VTab_item_color, Color.GRAY)
            itemBackgroundColor = a.getColor(R.styleable.VTab_item_background, Color.TRANSPARENT)
            selectedTint = a.getColor(R.styleable.VTab_selected_tint, itemColor)
            selectedBackgroundTint = a.getColor(R.styleable.VTab_selected_background_tint, itemBackgroundColor)
            itemPadding = a.getDimension(R.styleable.VTab_item_padding, 0f).toInt()
        } finally {
            a.recycle()
        }
        itemlayoutParam = LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f)
        orientation = VERTICAL
    }
    fun log(s:String){
        Log.d("fuck",s)
    }
    fun init() {
        removeAllViews()
        log("init "+items.size)
        items.forEachIndexed({index, item ->
            var imgV = ImageView(context)
            imgV.setImageDrawable(item.drawable)
            imgV.setOnClickListener({ view ->
                select(index)
                Toast.makeText(context,"clicked "+index,Toast.LENGTH_SHORT)
                item.onClick(index)
            })
            imgV.setPadding(itemPadding, 0, itemPadding, 0)
            addView(imgV,itemlayoutParam)
        } )
        select(0)

    }
    fun select(x: Int) {
        log("select"+0)
        for (i in 0 until childCount) {
            log("select loop"+i)
            val iv = getChildAt(i) as ImageView
            if (i == x) {
                log("match"+x)
                iv.setColorFilter(selectedTint, PorterDuff.Mode.SRC_IN)
                iv.setBackgroundColor(selectedBackgroundTint)
                continue
            }
            iv.setColorFilter(itemColor, PorterDuff.Mode.SRC_IN)
            iv.setBackgroundColor(itemBackgroundColor)
        }
        invalidate()
    }
    fun item(id:Int,onClick: (index:Int) -> Unit = {}):VTabItems{
        return VTabItems(context.resources.getDrawable(id),onClick)
    }
    class VTabItems(val drawable: Drawable, val onClick: (index:Int) -> Unit = {})


}
