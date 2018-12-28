package com.innov8.memeit.commons.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.text.set
import androidx.core.text.toSpannable
import com.innov8.memeit.commons.LinkTouchMovementMethod
import com.innov8.memeit.commons.R
import com.innov8.memeit.commons.TouchableSpan
import com.innov8.memeit.commons.models.TypefaceManager

class MemeItTextView : TextView {
    companion object {
        private val defColor = Color.parseColor("#1384fd")
        private val defSelectedColor = Color.LTGRAY
        private const val defFont = "Avenir"
    }

    private var tagColor = defColor
    private var mentionColor = defColor
    private var selectedColor = defSelectedColor
    var onTagClicked: ((String) -> Unit)? = null
    var onUsernameClicked: ((String) -> Unit)? = null


    constructor(context: Context) : super(context) {
        setFont(defFont)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MemeItTextView, 0, 0)
        try {
            tagColor = a.getColor(R.styleable.MemeItTextView_tagColor, defColor)
            mentionColor = a.getColor(R.styleable.MemeItTextView_mentionColor, defColor)
            selectedColor = a.getColor(R.styleable.MemeItTextView_selectedColor, defSelectedColor)
            setFont(a.getString(R.styleable.MemeItTextView_mfont) ?: defFont)

        } finally {
            a.recycle()
        }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }


    fun setTagColor(color: Int) {
        tagColor = color
        text = text
    }

    fun setMentionColor(color: Int) {
        mentionColor = color
        text = text
    }

    fun setSelectedColor(color: Int) {
        selectedColor = color
        text = text
    }

    fun setFont(font: String) {
        typeface = TypefaceManager.byName(font)
    }


    override fun setText(text: CharSequence?, type: BufferType?) {
        text?.run {
            val span = toSpannable()
            //todo split by regex
            split(" ").forEach { s ->
                if (s.startsWith("#") && s.length > 1) {
                    val i = indexOf(s)
                    span[i..i + s.length] = TouchableSpan(tagColor, selectedColor) {
                        onTagClicked?.invoke(s)
                    }
                } else if (s.startsWith("@") && s.length > 1) {
                    val i = indexOf(s)
                    span[i..i + s.length] = TouchableSpan(mentionColor, selectedColor) {
                        onUsernameClicked?.invoke(s)
                    }
                }
            }
            super.setText(span, type)
        } ?: super.setText(null, type)
        movementMethod = LinkTouchMovementMethod() //todo check if this can be optimized

    }
}