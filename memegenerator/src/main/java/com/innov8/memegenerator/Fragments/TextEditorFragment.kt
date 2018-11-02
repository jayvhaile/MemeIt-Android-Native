package com.innov8.memegenerator.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.innov8.memegenerator.CustomViews.ColorChooser
import com.innov8.memegenerator.CustomViews.FontChooser
import com.innov8.memegenerator.CustomViews.TextStyleView
import com.innov8.memegenerator.MemeEngine.MemeTextView
import com.innov8.memegenerator.MemeEngine.TextEditListener
import com.innov8.memegenerator.R
import com.innov8.memegenerator.utils.Listener
import com.innov8.memegenerator.utils.ViewAdapter
import com.innov8.memeit.commons.models.TextStyleProperty
import com.innov8.memeit.commons.sp
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
    lateinit var strokeView: View
    lateinit var strokeEnable: Switch
    lateinit var strokeSize: IndicatorSeekBar
    lateinit var strokeColorChooser: ColorChooser

    fun createViews(inflater: LayoutInflater) {
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


        seekbarView.onSeekChangeListener = Listener(onSeek = {
            textEditListener?.onTextSizeChanged(it.progressFloat.sp(context!!))
        })
        strokeEnable = strokeView.findViewById(R.id.enable_stroke)
        strokeEnable.setOnCheckedChangeListener { _, isChecked ->
            textEditListener?.onTextSetStroked(isChecked)
        }
        strokeSize = strokeView.findViewById(R.id.stroke_size)
        strokeSize.onSeekChangeListener = Listener(onSeek = {
            textEditListener?.onTextStrokeChanged(it.progressFloat.sp(context!!))
        })
        strokeColorChooser = strokeView.findViewById(R.id.stroke_color)
        strokeColorChooser.onColorChoosed = {
            textEditListener?.onTextStrokrColorChanged(it)
        }


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.text_pager, container, false)
        strokeView = inflater.inflate(R.layout.text_editor_stroke_tab, container, false)
        createViews(inflater)
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

        val tempListener = textEditListener
        textEditListener = null
        colorChooser.chooseColor(tp.textColor)
        fontChooser.choose(tp.myTypeFace)
        textStyleView.setProperty(tp)
        seekbarView.setProgress(tp.textSize)
        strokeEnable.isChecked = tp.stroked
        strokeSize.setProgress(tp.strokeWidth)
        strokeColorChooser.chooseColor(tp.strokeColor)

        textEditListener = tempListener
    }


    inner class Adapter(context: Context) : ViewAdapter(context) {
        private var titles = listOf("Color", "Size", "Font", "Style", "Stroke", "Background")
        private var views = listOf(colorChooser, seekbarView, fontChooser, textStyleView, strokeView, ColorChooser(context))


        override fun getItem(position: Int): View = views[position]


        override fun getCount(): Int = titles.size

        override fun getPageTitle(position: Int): CharSequence? = titles[position]
    }


}