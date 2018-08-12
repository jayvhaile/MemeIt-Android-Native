package com.innov8.memegenerator.custom_views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet

import com.jaredrummler.materialspinner.MaterialSpinner

class FontChooserView : MaterialSpinner {
    private val fonts = arrayOf("Avenir", "Lyric", "Pacifico", "Ubuntu")
    private val fontsfilename = arrayOf("avenir", "lyric_font", "Pacifico", "ubuntu")

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
        //todo use fonts that are used for memes
        setItems(*fonts)
    }
    fun getSelectedFont():Typeface{
        return Typeface.createFromAsset(context.assets,"fonts/${fontsfilename[selectedIndex]}.ttf")
    }
}
