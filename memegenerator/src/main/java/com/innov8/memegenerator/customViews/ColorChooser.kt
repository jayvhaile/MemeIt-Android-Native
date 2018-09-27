package com.innov8.memegenerator.customViews

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.innov8.memegenerator.utils.dp

class ColorChooser : LinearLayout {


    var colorList = listOf(Color.RED,
            Color.BLUE,
            Color.YELLOW,
            Color.GREEN,
            Color.CYAN,
            Color.MAGENTA,
            Color.GRAY,
            Color.WHITE,
            Color.BLACK)

    var onColorChoosed: ((color: Int) -> Unit)? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()

    }


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private val childParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    private val padding = 8.dp(context)
    private val radius = 28f.dp(context)
    private val strokeWidth = 4f.dp(context)
    private val onClick: (View) -> Unit = {
        it as ColorView2
        choose(it.id)
        onColorChoosed?.invoke(colorList[it.id])
    }

    private fun init() {
        orientation = HORIZONTAL

        gravity = Gravity.CENTER_HORIZONTAL
        colorList.forEachIndexed { index, color ->
            create(color, index)
        }
    }

    private fun create(color: Int, index: Int = childCount): ColorView2 {
        val colorView = ColorView2(context, color, radius = radius,strokeWidth = strokeWidth)
        colorView.setPadding(padding, padding, padding, padding)
        colorView.id = index

        colorView.setOnClickListener(onClick)
        addView(colorView, childParams)
        return colorView
    }

    fun chooseColor(color: Int, addIfNone: Boolean = false) {
        val index = colorList.indexOf(color)
        if (index != -1) choose(index)
        else if (addIfNone)
            create(color).choosed = true

    }

    private fun choose(index: Int) {
        for (i in 0 until childCount) {
            val view = getChildAt(i) as ColorView2
            view.choosed = view.id == index
        }
    }


}

