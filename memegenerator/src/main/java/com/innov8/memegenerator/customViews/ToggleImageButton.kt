package com.innov8.memegenerator.customViews

import android.content.Context
import android.graphics.Color
import androidx.appcompat.widget.AppCompatImageButton
import android.util.AttributeSet
import android.widget.SeekBar
import com.innov8.memegenerator.R

class ToggleImageButton : AppCompatImageButton {
    var isChecked: Boolean = false
        private set
    private var checkedbackground: Int = 0
    private var uncheckedbackground: Int = 0

    var onCheckChanged:(checked:Boolean,fromUser:Boolean)->Unit={checked, fromUser ->  }
    constructor(context: Context) : super(context) {
        checkedbackground = Color.LTGRAY
        uncheckedbackground = Color.TRANSPARENT
        setChecked(false,false)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet) {
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ToggleImageButton,
                0, 0)
        try {
            isChecked = a.getBoolean(R.styleable.ToggleImageButton_checked, false)
            checkedbackground = a.getColor(R.styleable.ToggleImageButton_checked_background, Color.LTGRAY)
            uncheckedbackground = a.getColor(R.styleable.ToggleImageButton_checked_background, Color.TRANSPARENT)
        } finally {
            a.recycle()
        }
        setChecked(isChecked, false)
        setOnClickListener { e ->
            toggleCheck(true)
        }
    }
    fun setChecked(checked: Boolean, fromUser: Boolean) {
        this.isChecked = checked
        onCheckChanged(checked,fromUser)
        if (checked) {
            setBackgroundColor(checkedbackground)
        } else {
            setBackgroundColor(uncheckedbackground)
            val textSizeV: SeekBar

        }
    }
    fun toggleCheck(fromUser: Boolean) {
        setChecked(!isChecked, fromUser)
    }
}
