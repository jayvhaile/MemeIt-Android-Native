package com.innov8.memegenerator.fragments


import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.innov8.memegenerator.R
import com.innov8.memegenerator.adapters.TextPresetsAdapter
import com.innov8.memegenerator.customViews.ColorView
import com.innov8.memegenerator.customViews.FontChooserView
import com.innov8.memegenerator.customViews.ToggleImageButton
import com.innov8.memegenerator.memeEngine.ItemSelectedInterface
import com.innov8.memegenerator.memeEngine.TextEditListener
import com.innov8.memegenerator.models.MyTypeFace
import com.innov8.memegenerator.models.TextPreset
import com.innov8.memegenerator.models.TextStyleProperty
import com.innov8.memegenerator.utils.*

class MemeTextEditorFragment : Fragment(),ItemSelectedInterface {
    lateinit var textPresetFragment: TextPresetFragment
    lateinit var textCustomizeFragment: TextCustomizeFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textPresetFragment = TextPresetFragment()
        textCustomizeFragment = TextCustomizeFragment()
        textPresetFragment.textEditListener = textEditListener
        textCustomizeFragment.textEditListener = textEditListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_meme_editor_text, container, false)
        textPresetFragment = TextPresetFragment()
        textCustomizeFragment = TextCustomizeFragment()
        textPresetFragment.textEditListener = textEditListener
        textCustomizeFragment.textEditListener = textEditListener
        initViews(view)
        return view
    }


    lateinit var tabLayout: TabLayout
    private fun initViews(view: View) {
        tabLayout = view.findViewById(R.id.tabLayout)
        tabLayout.onTabSelected {
            when (it.position) {
                0 -> {
                    childFragmentManager.replace(R.id.holder, textPresetFragment)
                }
                1 -> {
                    childFragmentManager.replace(R.id.holder, textCustomizeFragment)
                }
            }
        }
        childFragmentManager.replace(R.id.holder, textPresetFragment)
    }


    override fun onDestroyView() {

        textPresetFragment.onDestroy()
        textCustomizeFragment.onDestroy()
        super.onDestroyView()
    }


    lateinit var textEditListener: TextEditListener
    override fun onTextItemSelected(textStyleProperty: TextStyleProperty) {
        textCustomizeFragment.applyTextProperty(textStyleProperty)
    }
}

class TextPresetFragment : Fragment() {
    lateinit var textEditListener: TextEditListener
    var asyncLoaders: AsyncLoader<List<TextPreset>>? = null
    lateinit var textPresetsAdapter: TextPresetsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textPresetsAdapter = TextPresetsAdapter(context!!)
        textPresetsAdapter.onItemClick = {
            textEditListener.onApplyAll(it.textStyleProperty, false)
        }
        asyncLoaders = AsyncLoader<List<TextPreset>>({
            return@AsyncLoader listOf<TextPreset>(
                    TextPreset("Normal", TextStyleProperty(
                            20f, Color.WHITE, MyTypeFace.byName("Arial")!!,
                            false, false, false,
                            true, Color.BLACK, 10f
                    )),
                    TextPreset("Meme", TextStyleProperty(
                            20f, Color.WHITE,  MyTypeFace.byName("Impact")!!,
                            false, false, true,
                            true, Color.BLACK, 10f
                    )),
                    TextPreset("Red", TextStyleProperty(
                            20f, Color.RED,  MyTypeFace.byName("Pacifico")!!
                    )),
                    TextPreset("Dialog", TextStyleProperty(
                            20f, Color.YELLOW,  MyTypeFace.byName("Ubuntu")!!,
                            false, false, false,
                            false, Color.BLACK, 10f
                    ))
            )
        })
    }

    lateinit var presetList: RecyclerView
    lateinit var presetAdd: Button
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_text_presets, container, false)
        initViews(view)
        load()
        return view
    }

    private fun initViews(view: View) {
        presetList = view.findViewById(R.id.frag_text_preset_list)
        presetAdd = view.findViewById(R.id.frag_text_preset_add)

        presetList.initWithGrid(3)
        presetList.adapter = textPresetsAdapter
    }

    fun load() {
        asyncLoaders?.load({ textPresetsAdapter.addAll(it) })
    }
}

class TextCustomizeFragment : Fragment(), ColorChooserDialog.ColorCallback {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.tab_customize_text, container, false)
        init(view)
        initEvent()
        return view
    }

    lateinit var textSizeV: SeekBar
    lateinit var textColorV: ColorView
    lateinit var textfontV: FontChooserView
    lateinit var textStyleBoldV: ToggleImageButton
    lateinit var textStyleItalicV: ToggleImageButton
    lateinit var textStyleAllCapV: ToggleImageButton
    lateinit var textStrokeV: CheckBox
    lateinit var textStrokeSizeV: SeekBar
    lateinit var textStrokeColorV: ColorView

    var textEditListener: TextEditListener?=null
    lateinit var colorChooserDialog: ColorChooserDialog
    private fun init(view: View) {
        textSizeV = view.findViewById(R.id.opt_text_size)
        textColorV = view.findViewById(R.id.opt_text_color)
        textfontV = view.findViewById(R.id.opt_text_font)
        textStyleBoldV = view.findViewById(R.id.opt_text_bold)
        textStyleItalicV = view.findViewById(R.id.opt_text_italic)
        textStyleAllCapV = view.findViewById(R.id.opt_text_allcap)
        textStrokeV = view.findViewById(R.id.opt_text_stroke)
        textStrokeColorV = view.findViewById(R.id.opt_text_stroke_color)
        textStrokeSizeV = view.findViewById(R.id.opt_text_stroke_size)

        colorChooserDialog = ColorChooserDialog.Builder(context!!, R.string.color_chooser_dialog_title)
                .dynamicButtonColor(false)
                .build()
        textColorV.setOnClickListener({ colorChooserDialog.show(childFragmentManager, "textColor") })
        textStrokeColorV.setOnClickListener({ colorChooserDialog.show(childFragmentManager, "textStrokeColor") })
    }


    private fun initEvent() {
        textColorV.onColorChanged = { color ->
            textEditListener?.onTextColorChanged(color)
        }
        textfontV.setOnItemSelectedListener({ _, _, _, _ ->
            textEditListener?.onTextFontChanged(textfontV.getSelectedFont())
        })
        textSizeV.onProgressChanged { progress, fromuser ->
             textEditListener?.onTextSizeChanged(progress.toFloat())
        }
        textStrokeV.setOnCheckedChangeListener({ compoundButton, b ->
            textEditListener?.onTextSetStroked(b)
        })
        textStrokeColorV.onColorChanged = {  color ->
            textEditListener?.onTextStrokrColorChanged(color)
        }
        textStrokeSizeV.onProgressChanged { progress, fromUser ->
            textEditListener?.onTextStrokeChanged(progress.toFloat())
        }
        textStyleAllCapV.onCheckChanged = { checked, fromUser ->
            textEditListener?.onTextSetAllCap(checked)
        }
    }

    fun applyTextProperty(tp: TextStyleProperty) {
        val temp=textEditListener
        textEditListener=null
        textColorV.setColor(tp.textColor)
       // textfontV.selectedIndex=
        textStyleBoldV.setChecked(true,false)
        textStyleItalicV.setChecked(true,false)
        textStyleAllCapV.setChecked(true,false)
        textSizeV.progress=tp.textSize.toInt()
        textStrokeV.isChecked=tp.stroked
        textStrokeColorV.setColor(tp.strokeColor)
        textStrokeSizeV.progress=tp.strokeWidth.toInt()
        textEditListener=temp
    }

    override fun onColorSelection(dialog: ColorChooserDialog, selectedColor: Int) {
        if (dialog.tag() == "textColor") {
            textColorV.setColor(selectedColor)
        } else {
            textStrokeColorV.setColor(selectedColor)
        }
    }

    override fun onColorChooserDismissed(dialog: ColorChooserDialog) {

    }
}
