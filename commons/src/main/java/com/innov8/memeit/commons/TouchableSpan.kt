package com.innov8.memeit.commons

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

class TouchableSpan(private val normalTextColor: Int,
                    private val pressedTextColor: Int,
                    private val onClickCallback: (View) -> Unit) : ClickableSpan() {
    override fun onClick(widget: View) {
        onClickCallback(widget)
    }

    private var isPressed: Boolean = false

    fun setPressed(isSelected: Boolean) {
        isPressed = isSelected
    }

    override fun updateDrawState(textPaint: TextPaint) {
        super.updateDrawState(textPaint)
        val textColor = if (isPressed) pressedTextColor else normalTextColor
        textPaint.color = textColor
        textPaint.bgColor = Color.TRANSPARENT
        textPaint.isUnderlineText = false
    }
}