package com.innov8.memeit.commons.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Patterns
import android.widget.TextView
import androidx.core.text.set
import androidx.core.text.toSpannable
import com.innov8.memeit.commons.LinkTouchMovementMethod
import com.innov8.memeit.commons.R
import com.innov8.memeit.commons.TouchableSpan
import com.innov8.memeit.commons.models.TypefaceManager
import java.util.regex.Pattern

/**
 * Created by Biruk on 5/11/2018.
 */

class MemeItTextView : TextView {
    companion object {
        private val defColor = Color.parseColor("#1384fd")
        private const val defSelectedColor = Color.LTGRAY
        private const val defFont = "Avenir"
        private const val MIN_PHONE_NUMBER_LENGTH = 8

        private val PHONE_PATTERN = Patterns.PHONE.pattern()
        private val EMAIL_PATTERN = Patterns.EMAIL_ADDRESS.pattern()
        private const val HASHTAG_PATTERN = "(?:^|\\s|$)#[\\p{L}0-9_]*"
        private const val MENTION_PATTERN = "(?:^|\\s|$|[.])@[\\p{L}0-9_]*"
        private const val URL_PATTERN = "(^|[\\s.:;?\\-\\]<\\(])" +
                "((https?://|www\\.|pic\\.)[-\\w;/?:@&=+$\\|\\_.!~*\\|'()\\[\\]%#,☺]+[\\w/#](\\(\\))?)" +
                "(?=$|[\\s',\\|\\(\\).:;?\\-\\[\\]>\\)])"

    }

    enum class LinkMode(val id: String, val regex: String) {
        PHONE("phone", PHONE_PATTERN),
        EMAIL("email", EMAIL_PATTERN),
        HASHTAG("hashtag", HASHTAG_PATTERN),
        MENTION("mention", MENTION_PATTERN),
        URL("url", URL_PATTERN);

        fun getMatcher(text: CharSequence) = Pattern.compile(regex).matcher(text)

    }

    private var linkColor = defColor
    private var selectedColor = defSelectedColor
    var onLinkClicked: ((LinkMode, String) -> Unit)? = null

    private var enabledLinks: MutableList<LinkMode>

    init {
        enabledLinks = mutableListOf()
    }

    constructor(context: Context) : super(context) {
        setFont(defFont)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }


    private fun init(context: Context, attrs: AttributeSet?) {
        if (isInEditMode) {
            TypefaceManager.init(context)
        }
        val a = context.obtainStyledAttributes(attrs, R.styleable.MemeItTextView, 0, 0)
        try {
            linkColor = a.getColor(R.styleable.MemeItTextView_linkColor, defColor)
            selectedColor = a.getColor(R.styleable.MemeItTextView_selectedColor, defSelectedColor)
            setFont(a.getString(R.styleable.MemeItTextView_mfont) ?: defFont)
            val lm = (a.getString(R.styleable.MemeItTextView_linkModes) ?: "none").toLowerCase()
            enabledLinks = when (lm) {
                "all" -> LinkMode.values().toMutableList()
                "none" -> mutableListOf()
                else -> {
                    val names = LinkMode.values()
                    lm.split(",").mapNotNull { names.findLast { mode -> mode.id == it } }
                            .toMutableList()
                }
            }
        } finally {
            a.recycle()
        }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }


    fun setLinkColor(color: Int) {
        linkColor = color
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
        if (enabledLinks.isNullOrEmpty())
            super.setText(text, type)
        else
            text?.run {
                val span = toSpannable()
                enabledLinks.forEach { linkMode ->
                    val matcher = linkMode.getMatcher(this)
                    while (matcher.find()) {
                        if (linkMode == LinkMode.PHONE && matcher.group().length <= MIN_PHONE_NUMBER_LENGTH)
                            continue
                        val start = matcher.start()
                        val end = matcher.end()
                        val match = matcher.group()
                        span[start..end] = TouchableSpan(linkColor, selectedColor) {
                            onLinkClicked?.invoke(linkMode, match)
                        }
                    }
                }
                super.setText(span, type)
                movementMethod = LinkTouchMovementMethod() //todo check if this can be optimized
            } ?: super.setText(null, type)

    }
}
