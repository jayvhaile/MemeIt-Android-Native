package com.innov8.memegenerator.customViews

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import com.innov8.memeit.commons.dp

class ColorChooser : HorizontalScrollView {


    var colorList = listOf(
            Color.WHITE,
            Color.BLACK,
            Color.GRAY,
            Color.RED,
            Color.YELLOW,
            Color.GREEN,
            Color.BLUE,
            Color.CYAN,
            Color.MAGENTA
    )

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
        it as ColorView
        choose(it.id)
        onColorChoosed?.invoke(colorList[it.id])
    }
    val linearLayout=LinearLayout(context)

    private fun init() {
        linearLayout.orientation = LinearLayout.HORIZONTAL

        linearLayout.gravity = Gravity.CENTER_HORIZONTAL
        colorList.forEach{create(it)}
        addView(linearLayout)
    }

    private fun create(color: Int, index: Int = linearLayout.childCount): ColorView {
        val colorView = ColorView(context, color, radius = radius,strokeWidth = strokeWidth)
        colorView.setPadding(padding, padding, padding, padding)
        colorView.id = index

        colorView.setOnClickListener(onClick)
        linearLayout.addView(colorView, childParams)
        return colorView
    }

    fun chooseColor(color: Int, addIfNone: Boolean = false) {
        val index = colorList.indexOf(color)
        if (index != -1) choose(index)
        else if (addIfNone)
            create(color).choosed = true

    }

    private fun choose(index: Int) {
        for (i in 0 until linearLayout.childCount) {
            val view = linearLayout.getChildAt(i) as ColorView
            view.choosed = view.id == index
        }
    }


}

