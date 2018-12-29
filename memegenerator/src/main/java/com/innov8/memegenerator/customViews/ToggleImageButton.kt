package com.innov8.memegenerator.customViews

import android.content.Context
import android.graphics.Color
import androidx.appcompat.widget.AppCompatImageButton
import android.util.AttributeSet
import com.innov8.memegenerator.R

class ToggleImageButton : AppCompatImageButton {
    var isChecked: Boolean = false
        private set
    private var checkedColor: Int = Color.RED
    private var uncheckedColor: Int = Color.WHITE

    var onCheckChanged:((checked:Boolean,fromUser:Boolean)->Unit)?=null
    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        if(attrs!=null){
            val a = context.theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.ToggleImageButton,
                    0, 0)
            try {
                isChecked = a.getBoolean(R.styleable.ToggleImageButton_checked, false)
                checkedColor = a.getColor(R.styleable.ToggleImageButton_checked_background, Color.RED)
                uncheckedColor = a.getColor(R.styleable.ToggleImageButton_unchecked_background, Color.WHITE)
            } finally {
                a.recycle()
            }
        }

        setChecked(isChecked, false)
        setOnClickListener {
            toggleCheck(true)
        }
        setBackgroundColor(Color.TRANSPARENT)
    }
    fun setChecked(checked: Boolean, fromUser: Boolean=false) {
        this.isChecked = checked
        if (checked) {
            setColorFilter(checkedColor)
        } else {
            setColorFilter(uncheckedColor)
        }
        onCheckChanged?.invoke(checked,fromUser)
    }
    fun toggleCheck(fromUser: Boolean=false) {
        setChecked(!isChecked, fromUser)
    }
}
