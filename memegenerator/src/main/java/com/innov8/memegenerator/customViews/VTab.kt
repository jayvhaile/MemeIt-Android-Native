package com.innov8.memegenerator.customViews

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.innov8.memegenerator.R

class VTab : LinearLayout {

    var items: List<VTabItems> = listOf()
        set(value) {
            field = value
            init()
        }
    private var selectedTint: Int = 0
    private var selectedBackgroundTint: Int = 0
    private var itemColor: Int = 0
    private var itemBackgroundColor: Int = 0
    var itemPadding: Int = 0
    private lateinit var itemLayoutParam: LayoutParams

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
        itemLayoutParam = LayoutParams(0,LayoutParams.MATCH_PARENT, 1f)
        orientation = HORIZONTAL
    }

    fun init() {
        removeAllViews()
        items.forEachIndexed { index, item ->
            val imgV = ImageView(context)
            imgV.setImageDrawable(item.drawable)
            imgV.setOnClickListener {
                select(index)
                item.onClick(index)
            }
            imgV.setPadding(0,itemPadding, 0, itemPadding)
            addView(imgV, itemLayoutParam)
        }
        select(0)

    }

    fun select(x: Int) {
        for (i in 0 until childCount) {
            val iv = getChildAt(i) as ImageView
            if (i == x) {
                iv.setColorFilter(selectedTint, PorterDuff.Mode.SRC_IN)
                iv.setBackgroundColor(selectedBackgroundTint)
                continue
            }
            iv.setColorFilter(itemColor, PorterDuff.Mode.SRC_IN)
            iv.setBackgroundColor(itemBackgroundColor)
        }
        invalidate()
    }

    fun item(id: Int, onClick: (index: Int) -> Unit = {}): VTabItems {
        return VTabItems(VectorDrawableCompat.create(resources, id, null)!!, onClick)
    }

    class VTabItems(val drawable: Drawable, val onClick: (index: Int) -> Unit = {})


}
