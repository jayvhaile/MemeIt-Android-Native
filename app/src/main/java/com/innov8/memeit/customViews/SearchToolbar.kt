package com.innov8.memeit.customViews

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.util.AttributeSet
import android.view.Gravity
import android.view.MenuItem
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import com.innov8.memeit.R
import com.innov8.memeit.commons.addOnTextChanged
import com.innov8.memeit.commons.dp

class SearchToolbar : LinearLayout, MenuItem.OnActionExpandListener {
    private var expanded = false
    var onSearch: ((String) -> Unit)? = null
    var onSearchDone: ((String) -> Unit)? = null

    constructor(context: Context) : super(context) {
        init()

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        layoutParams = ViewGroup.LayoutParams(500.dp(context), LayoutParams.MATCH_PARENT)
        gravity = Gravity.CENTER_VERTICAL

        addView(editText, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        addView(clearButton, LayoutParams(56.dp(context), LayoutParams.MATCH_PARENT))
    }

    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        editText.requestFocusFromTouch()
        expanded = true
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        editText.setText("")
        expanded = false
        return true
    }

    private val editText by lazy {

        (EditText(context)).apply {
            background = ColorDrawable(Color.TRANSPARENT)
            hint = "Search Template"
            setLines(1)
            val dp16 = 16.dp(context)
            setPadding(dp16, 0, dp16, 0)
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            inputType = InputType.TYPE_CLASS_TEXT
            addOnTextChanged {
                if (expanded) onSearch?.invoke(it)
            }
            setOnEditorActionListener { view, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (expanded) onSearchDone?.invoke(view.text.toString())
                    (context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
                        hideSoftInputFromWindow(view.windowToken, 0)
                    }
                    true
                } else
                    false
            }
        }
    }
    private val clearButton by lazy {
        ImageButton(context).apply {
            setBackgroundColor(Color.TRANSPARENT)
            setImageResource(R.drawable.ic_clear_black_24dp)
            setOnClickListener {
                editText.setText("")
            }
        }
    }

}