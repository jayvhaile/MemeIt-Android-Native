package com.innov8.memegenerator.customViews

import android.content.Context
import android.icu.text.Normalizer.NO
import android.text.Layout
import android.text.Layout.Alignment.*
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import com.memeit.backend.models.MemeTextStyleProperty
import com.innov8.memegenerator.interfaces.TextEditListener
import com.innov8.memegenerator.R
import com.innov8.memeit.commons.dp


class TextAlignmentView : LinearLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        orientation = HORIZONTAL
        val size = 40.dp(context)
        val param = LayoutParams(size, size)
        gravity = Gravity.CENTER_HORIZONTAL
        addView(ToggleImageButton(context).apply {
            setImageResource(R.drawable.ic_format_align_left_white_24dp)
            onCheckChanged = { _, fromUser ->
                if (fromUser) setAlignment(ALIGN_NORMAL, true)
            }
        }, param)
        addView(ToggleImageButton(context).apply {
            setImageResource(R.drawable.ic_format_align_center_white_24dp)
            onCheckChanged = { _, fromUser ->
                if (fromUser) setAlignment(ALIGN_CENTER, true)
            }
        }, param)
        addView(ToggleImageButton(context).apply {
            setImageResource(R.drawable.ic_format_align_right_white_24dp)
            onCheckChanged = { _, fromUser ->
                if (fromUser) setAlignment(ALIGN_OPPOSITE, true)
            }
        }, param)
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size)
    }

    var textEditListener: TextEditListener? = null


    fun setProperty(textStyleProperty: MemeTextStyleProperty) {
        setAlignment(textStyleProperty.alignment)
    }

    fun getAlignment(): Layout.Alignment {
        return children.map { it as ToggleImageButton }.find { it.isChecked }?.let {
            when (children.indexOf(it)) {
                0 -> ALIGN_NORMAL
                1 -> ALIGN_CENTER
                2 -> ALIGN_OPPOSITE
                else -> null
            }
        } ?: ALIGN_CENTER
    }

    private fun setAlignment(alignment: Layout.Alignment, notifyListener: Boolean = false) {
        clearSelection()
        when (alignment) {
            ALIGN_NORMAL -> (getChildAt(0) as ToggleImageButton).setChecked(true)
            ALIGN_OPPOSITE -> (getChildAt(2) as ToggleImageButton).setChecked(true)
            ALIGN_CENTER -> (getChildAt(1) as ToggleImageButton).setChecked(true)
            ALIGN_LEFT -> (getChildAt(0) as ToggleImageButton).setChecked(true)
            ALIGN_RIGHT -> (getChildAt(2) as ToggleImageButton).setChecked(true)
        }
        if (notifyListener) textEditListener?.onTextAlignmentChanged(alignment)
    }

    private fun clearSelection() {
        children.map { it as ToggleImageButton }.forEach { it.setChecked(false) }
    }

}