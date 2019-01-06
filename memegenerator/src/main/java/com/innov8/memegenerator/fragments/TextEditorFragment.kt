package com.innov8.memegenerator.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.innov8.memegenerator.customViews.ColorChooser
import com.innov8.memegenerator.customViews.FontChooser
import com.innov8.memegenerator.customViews.TextStyleView
import com.innov8.memegenerator.memeEngine.MemeTextItem
import com.memeit.backend.models.MemeTextStyleProperty
import com.innov8.memegenerator.R
import com.innov8.memegenerator.customViews.TextAlignmentView
import com.innov8.memegenerator.interfaces.TextEditListener
import com.innov8.memegenerator.utils.Listener
import com.innov8.memegenerator.utils.ViewAdapter
import com.innov8.memeit.commons.sp
import com.warkiz.widget.IndicatorSeekBar
import kotlinx.android.synthetic.main.bottom_tab.*
import kotlinx.android.synthetic.main.text_pager.*

class TextEditorFragment : Fragment() {

    var textEditListener: TextEditListener? = null
    var textStyleProperty: MemeTextStyleProperty? = null
        set(value) {
            field = value
            applyTextProperty()
        }

    internal lateinit var colorChooser: ColorChooser
    internal lateinit var fontChooser: FontChooser
    internal lateinit var textStyleView: TextStyleView
    internal lateinit var textAlignmentView: TextAlignmentView
    internal lateinit var seekBarView: IndicatorSeekBar
    internal lateinit var strokeView: View
    private lateinit var strokeEnable: Switch
    private lateinit var strokeSize: IndicatorSeekBar
    private lateinit var strokeColorChooser: ColorChooser
    internal lateinit var bgColorChooser: ColorChooser

    private fun createViews() {
        colorChooser = ColorChooser(context!!).apply {
            onColorChoosed = { textEditListener?.onTextColorChanged(it) }

        }
        fontChooser = FontChooser(context!!).apply {
            onFontChoosed = { textEditListener?.onTextFontChanged(it) }
        }
        textStyleView = TextStyleView(context!!).apply {
            textEditListener = this@TextEditorFragment.textEditListener
        }
        textAlignmentView = TextAlignmentView(context!!).apply {
            textEditListener = this@TextEditorFragment.textEditListener
        }
        seekBarView = IndicatorSeekBar.with(context!!)
                .min(5f)
                .max(100f)
                .tickCount(40)
                .build()


        seekBarView.onSeekChangeListener = Listener(onSeek = {
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

        bgColorChooser = ColorChooser(context!!).apply {
            addColorView(Color.TRANSPARENT, 0)
            onColorChoosed = {
                textEditListener?.onTextBgColorChanged(it)
            }
        }


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.text_pager, container, false)
        strokeView = inflater.inflate(R.layout.text_editor_stroke_tab, container, false)
        createViews()
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text_pager.adapter = Adapter(context!!)
        pager_tab.setupWithViewPager(text_pager)
        applyTextProperty()
        pager_add_text.setOnClickListener {
            textEditListener?.onAddText(MemeTextItem(context!!, 400, 150).apply {
                applyTextStyleProperty(generateTextStyleProperty())
            })
        }
    }

    private fun applyTextProperty() {
        textStyleProperty ?: return
        context ?: return
        val tp = textStyleProperty!!

        val tempListener = textEditListener
        textEditListener = null
        colorChooser.chooseColor(tp.textColor)
        fontChooser.choose(tp.font)
        textStyleView.setProperty(tp)
        textAlignmentView.setProperty(tp)
        seekBarView.setProgress(tp.textSize)
        strokeEnable.isChecked = tp.stroked
        strokeSize.setProgress(tp.strokeWidth)
        strokeColorChooser.chooseColor(tp.strokeColor)
        bgColorChooser.chooseColor(tp.bgColor)
        textEditListener = tempListener
    }

    private fun generateProperty(): MemeTextStyleProperty {
        return MemeTextStyleProperty(
                seekBarView.progressFloat,
                colorChooser.getChoosedColor(),
                fontChooser.choosedFont,
                textStyleView.bold,
                textStyleView.italic,
                textStyleView.allCap,
                strokeEnable.isChecked,
                strokeColorChooser.getChoosedColor(),
                strokeSize.progressFloat,
                bgColorChooser.getChoosedColor(),
                textAlignmentView.getAlignment()
        )
    }


    inner class Adapter(context: Context) : ViewAdapter(context) {
        private var titles = listOf("Color", "Size", "Font", "Style", "Stroke", "Alignment", "Background")
        private var views = listOf(colorChooser,
                seekBarView,
                fontChooser,
                textStyleView,
                strokeView,
                textAlignmentView,
                bgColorChooser)


        override fun getItem(position: Int): View = views[position]


        override fun getCount(): Int = titles.size

        override fun getPageTitle(position: Int): CharSequence? = titles[position]
    }


}