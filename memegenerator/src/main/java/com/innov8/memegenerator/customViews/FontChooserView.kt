package com.innov8.memegenerator.customViews

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import com.innov8.memegenerator.models.Font

import com.jaredrummler.materialspinner.MaterialSpinner

class FontChooserView : MaterialSpinner {


    lateinit var fonts:List<Font>
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        //todo customize the item views to show the font
        fonts=Font.loadAllFonts(context)
        setItems(*fonts.map { it.name }.toTypedArray())
    }
    fun getSelectedFont():Typeface =fonts[selectedIndex].loadTypeFace(context)


}
