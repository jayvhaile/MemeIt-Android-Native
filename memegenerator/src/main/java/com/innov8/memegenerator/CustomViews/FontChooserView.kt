package com.innov8.memegenerator.CustomViews

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import com.innov8.memeit.commons.dp
import com.innov8.memeit.commons.models.TypefaceManager

class FontChooser : HorizontalScrollView {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    val linearLayout = LinearLayout(context)


    private val choosedColor = Color.RED
    private val unchoosedColor = Color.WHITE
    var onFontChoosed: ((String) -> Unit)? = null
    private val typefaces = TypefaceManager.fonts

    var choosedFont = "Default"
        private set

    private fun init() {
        linearLayout.orientation = LinearLayout.HORIZONTAL
        val childParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        val paddingH = 16.dp(context)
        val paddingV = 4.dp(context)

        val onClick: (View) -> Unit = {
            it as TextView
            val font = it.text.toString()
            if (choosedFont != font) {
                choose(font)
                choosedFont = font
                onFontChoosed?.invoke(font)
            }
        }

        typefaces.forEach {
            val t = TextView(context)
            t.text = it
            t.textSize = 18f
            t.setTextColor(unchoosedColor)
            t.typeface = TypefaceManager.byName(it)
            t.setPadding(paddingH, paddingV, paddingH, paddingV)
            t.setOnClickListener(onClick)
            linearLayout.addView(t, childParams)
        }
        addView(linearLayout)
    }

    fun choose(font: String) {
        val index = typefaces.indexOf(font)
        if (index != -1) choose(index)
    }

    private fun choose(index: Int) {
        for (i in 0 until linearLayout.childCount) {
            val tv = linearLayout.getChildAt(i) as TextView
            tv.setTextColor(if (i == index) choosedColor else unchoosedColor)
        }
    }
}