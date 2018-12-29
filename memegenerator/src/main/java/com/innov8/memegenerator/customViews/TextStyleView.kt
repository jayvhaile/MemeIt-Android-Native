package com.innov8.memegenerator.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import com.memeit.backend.models.MemeTextStyleProperty
import com.innov8.memegenerator.interfaces.TextEditListener
import com.innov8.memegenerator.R
import com.innov8.memeit.commons.dp


class TextStyleView : LinearLayout {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    init {
        orientation = HORIZONTAL
        val size=40.dp(context)
        val param=LayoutParams(size,size)
        gravity=Gravity.CENTER_HORIZONTAL
        addView(ToggleImageButton(context).apply {
            setImageResource(R.drawable.ic_format_bold)
            onCheckChanged= { checked, fromUser ->
                if (fromUser) textEditListener?.onTextSetBold(checked)
            }
        },param)
        addView(ToggleImageButton(context).apply {
            setImageResource(R.drawable.ic_format_italic)
            onCheckChanged= { checked, fromUser ->
                if (fromUser) textEditListener?.onTextSetItalic(checked)
            }
        },param)
        addView(ToggleImageButton(context).apply {
            setImageResource(R.drawable.ic_text_fields_black_24dp)
            onCheckChanged= { checked, fromUser ->
                if (fromUser) textEditListener?.onTextSetAllCap(checked)
            }
        },param)
        layoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,size)
    }

    private fun init() {

    }

    var textEditListener: TextEditListener? = null



    fun setProperty(textStyleProperty: MemeTextStyleProperty) {
        (getChildAt(0) as ToggleImageButton).setChecked(textStyleProperty.bold)
        (getChildAt(1) as ToggleImageButton).setChecked(textStyleProperty.italic)
        (getChildAt(2) as ToggleImageButton).setChecked(textStyleProperty.allCap)
    }

}