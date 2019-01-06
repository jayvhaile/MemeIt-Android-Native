package com.innov8.memeit.commons.views

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import com.innov8.memeit.commons.models.TypefaceManager

/**
 * Created by Biruk on 6/30/2018.
 */

class FontEditText : androidx.appcompat.widget.AppCompatEditText {

    constructor(context: Context) : super(context) {
        handleActionBtnClick()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        handleActionBtnClick()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        handleActionBtnClick()
    }

    init {
        if (isInEditMode)
            TypefaceManager.init(context)
        this.typeface = TypefaceManager.byName("regular")
    }

    private fun handleActionBtnClick() {
        setOnEditorActionListener { v, actionId, event ->
            (v.context
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    v.windowToken, 0)
            clearFocus()
            false
        }
    }


    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            clearFocus()
        }
        return super.onKeyPreIme(keyCode, event)
    }
}
