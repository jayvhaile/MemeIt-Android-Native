package com.innov8.memegenerator.CustomViews

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import com.innov8.memeit.commons.models.MyTypeFace
import com.innov8.memegenerator.utils.dp

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
    var onFontChoosed: ((MyTypeFace) -> Unit)? = null
    private val typefaces = MyTypeFace.typefaceFiles

    var choosedTypeface = Typeface.DEFAULT
        private set

    private fun init() {
        linearLayout.orientation = LinearLayout.HORIZONTAL
        val childParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        val paddingH = 16.dp(context)
        val paddingV = 4.dp(context)

        val onClick: (View) -> Unit = {
            it as TextView
            if (choosedTypeface != it.typeface) {
                val mtf = typefaces.find { v->v.getTypeFace()==it.typeface}!!
                choose(mtf)
                choosedTypeface = it.typeface
                onFontChoosed?.invoke(mtf)
            }
        }

        typefaces.forEach {
            val t = TextView(context)
            t.text = it.name
            t.textSize = 18f
            t.setTextColor(unchoosedColor)
            t.typeface = it.getTypeFace(context)
            t.setPadding(paddingH, paddingV, paddingH, paddingV)
            t.setOnClickListener(onClick)
            linearLayout.addView(t, childParams)
        }
        addView(linearLayout)
    }

    fun choose(typeface: MyTypeFace, addIfNone: Boolean = false) {
        val index = typefaces.indexOf(typeface)
        if (index != -1) choose(index)
    }

    private fun choose(index: Int) {
        for (i in 0 until linearLayout.childCount) {
            val tv = linearLayout.getChildAt(i) as TextView
            tv.setTextColor(if (i == index) choosedColor else unchoosedColor)

        }
    }
}