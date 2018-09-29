package com.innov8.memegenerator.customViews

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import com.innov8.memegenerator.models.MyTypeFace
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


    val choosedColor = Color.RED
    val unchoosedColor = Color.WHITE
    var onFontChoosed: ((Typeface) -> Unit)? = null
    val tp = MyTypeFace.getTypefaceFiles()

    var choosedTypeface=Typeface.DEFAULT
    private fun init() {
        linearLayout.orientation = LinearLayout.HORIZONTAL
        val childParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        val paddingH = 16.dp(context)
        val paddingV = 4.dp(context)

        val onClick: (View) -> Unit = {
            it as TextView
            if(choosedTypeface!=it.typeface){
                choose(it.typeface)
                choosedTypeface=it.typeface
                onFontChoosed?.invoke(it.typeface)
            }
        }

        tp.forEach {
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

    fun choose(typeface: Typeface, addIfNone: Boolean = false) {
        val index=tp.map { it.getTypeFace(context) }.indexOf(typeface)
        if (index!=-1)choose(index)
    }

    private fun choose(index: Int) {
        for (i in 0 until linearLayout.childCount) {
            val tv = linearLayout.getChildAt(i) as TextView
            tv.setTextColor(if (i == index) choosedColor else unchoosedColor)

        }
    }
}