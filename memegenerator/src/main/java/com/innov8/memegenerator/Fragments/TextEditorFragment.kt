package com.innov8.memegenerator.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.innov8.memegenerator.R
import com.innov8.memegenerator.utils.ViewAdapter
import com.innov8.memegenerator.CustomViews.ColorChooser
import com.innov8.memegenerator.CustomViews.FontChooser
import com.innov8.memegenerator.CustomViews.TextStyleView
import com.innov8.memegenerator.MemeEngine.MemeTextView
import com.innov8.memegenerator.MemeEngine.TextEditListener
import com.innov8.memeit.commons.models.TextStyleProperty
import com.warkiz.widget.IndicatorSeekBar
import kotlinx.android.synthetic.main.bottom_tab.*
import kotlinx.android.synthetic.main.text_pager.*

class TextEditorFragment : Fragment() {


    var textEditListener: TextEditListener? = null
    var textStyleProperty: TextStyleProperty? = null
        set(value) {
            field = value
            applyTextProperty()
        }

    lateinit var colorChooser: ColorChooser
    lateinit var fontChooser: FontChooser
    lateinit var textStyleView: TextStyleView
    lateinit var seekbarView: IndicatorSeekBar

    fun createViews() {
        colorChooser = ColorChooser(context!!).apply {
            onColorChoosed = { textEditListener?.onTextColorChanged(it) }
        }
        fontChooser = FontChooser(context!!).apply {
            onFontChoosed = { textEditListener?.onTextFontChanged(it) }
        }
        textStyleView = TextStyleView(context!!).apply {
            textEditListener = this@TextEditorFragment.textEditListener
        }
        seekbarView = IndicatorSeekBar.with(context!!)
                .min(5f)
                .max(100f)
                .tickCount(20)
                .build()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.text_pager, container, false)
        createViews()
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text_pager.adapter = Adapter(context!!)
        pager_tab.setupWithViewPager(text_pager)
        applyTextProperty()
        pager_add_text.setOnClickListener {
            val t = MemeTextView(context!!, 400, 100)
            t.text = "text"
            textEditListener?.onAddText(t)
        }
    }

    private fun applyTextProperty() {
        textStyleProperty ?: return
        context ?: return
        val tp = textStyleProperty!!
        colorChooser.chooseColor(tp.textColor)
        fontChooser.choose(tp.myTypeFace)
        textStyleView.setProperty(tp)
    }


    inner class Adapter(context: Context) : ViewAdapter(context) {
        private var titles = listOf("Color", "Size", "Font", "Style", "Stroke", "Background")
        private var views = listOf(colorChooser, seekbarView, fontChooser, textStyleView, ColorChooser(context), ColorChooser(context))


        override fun getItem(position: Int): View = views[position]


        override fun getCount(): Int = titles.size

        override fun getPageTitle(position: Int): CharSequence? = titles[position]
    }


}