package com.innov8.memegenerator.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.SeekBar
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.google.android.material.tabs.TabLayout
import com.innov8.memegenerator.R
import com.innov8.memegenerator.TextPresetFragment
import com.innov8.memegenerator.customViews.ColorView
import com.innov8.memegenerator.customViews.FontChooserView
import com.innov8.memegenerator.customViews.MyToolbarmenu
import com.innov8.memegenerator.customViews.ToggleImageButton
import com.innov8.memegenerator.memeEngine.ItemSelectedInterface
import com.innov8.memegenerator.memeEngine.MemeEditorView
import com.innov8.memegenerator.memeEngine.MemeTextView
import com.innov8.memegenerator.memeEngine.TextEditListener
import com.innov8.memegenerator.models.TextStyleProperty
import com.innov8.memegenerator.utils.onProgressChanged
import com.innov8.memegenerator.utils.onTabSelected
import com.innov8.memegenerator.utils.replace

class MemeTextEditorFragment : MemeEditorFragment(), ItemSelectedInterface {

    override val menus: List<MyToolbarmenu>
        get() = listOf(
                MyToolbarmenu(R.drawable.ic_text_copy) {
                    val x=memeEditorView?.getSelectedview<MemeTextView>()
                    if(x!=null){
                        val n:MemeTextView=x.copy()
                        memeEditorView?.addMemeItemView(n)
                    }
                },
                MyToolbarmenu(R.drawable.ic_add) {
                    val t = MemeTextView(context!!, 400, 100)
                    memeEditorView?.addMemeItemView(t)
                    t.text = "text"

                },
                MyToolbarmenu(R.drawable.ic_text_menu_delete) {
                    memeEditorView?.removeSelectedItem(MemeTextView::class.java)
                }
        )
    private lateinit var textPresetFragment: TextPresetFragment
    private lateinit var textCustomizeFragment: TextCustomizeFragment
    var memeEditorView: MemeEditorView? = null//todo this should not be here
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
        if (context == null) return
        if (tabLayout.selectedTabPosition == 1)
            textCustomizeFragment.applyTextProperty(textStyleProperty)
    }
}



class TextCustomizeFragment : androidx.fragment.app.Fragment(), ColorChooserDialog.ColorCallback {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.tab_customize_text, container, false)
        init(view)
        initEvent()
        return view
    }

    private lateinit var textSizeV: SeekBar
    private lateinit var textColorV: ColorView
    private lateinit var textFontV: FontChooserView
    private lateinit var textStyleBoldV: ToggleImageButton
    private lateinit var textStyleItalicV: ToggleImageButton
    private lateinit var textStyleAllCapV: ToggleImageButton
    private lateinit var textStrokeV: CheckBox
    private lateinit var textStrokeSizeV: SeekBar
    private lateinit var textStrokeColorV: ColorView

    var textEditListener: TextEditListener? = null
    private lateinit var colorChooserDialog: ColorChooserDialog
    private fun init(view: View) {
        textSizeV = view.findViewById(R.id.opt_text_size)
        textColorV = view.findViewById(R.id.opt_text_color)
        textFontV = view.findViewById(R.id.opt_text_font)
        textStyleBoldV = view.findViewById(R.id.opt_text_bold)
        textStyleItalicV = view.findViewById(R.id.opt_text_italic)
        textStyleAllCapV = view.findViewById(R.id.opt_text_allcap)
        textStrokeV = view.findViewById(R.id.opt_text_stroke)
        textStrokeColorV = view.findViewById(R.id.opt_text_stroke_color)
        textStrokeSizeV = view.findViewById(R.id.opt_text_stroke_size)

        colorChooserDialog = ColorChooserDialog.Builder(context!!, R.string.color_chooser_dialog_title)
                .dynamicButtonColor(false)
                .build()
        textColorV.setOnClickListener { colorChooserDialog.show(childFragmentManager, "textColor") }
        textStrokeColorV.setOnClickListener { colorChooserDialog.show(childFragmentManager, "textStrokeColor") }
    }


    private fun initEvent() {
        textColorV.onColorChanged = { color ->
            textEditListener?.onTextColorChanged(color)
        }
        textFontV.setOnItemSelectedListener { _, _, _, _ ->
            textEditListener?.onTextFontChanged(textFontV.getSelectedFont())
        }
        textSizeV.onProgressChanged { progress, _ ->
            textEditListener?.onTextSizeChanged(progress.toFloat())
        }
        textStrokeV.setOnCheckedChangeListener { _, b ->
            textEditListener?.onTextSetStroked(b)
        }
        textStrokeColorV.onColorChanged = { color ->
            textEditListener?.onTextStrokrColorChanged(color)
        }
        textStrokeSizeV.onProgressChanged { progress, _ ->
            textEditListener?.onTextStrokeChanged(progress.toFloat())
        }
        textStyleAllCapV.onCheckChanged = { checked, _ ->
            textEditListener?.onTextSetAllCap(checked)
        }
    }

    fun applyTextProperty(tp: TextStyleProperty) {
        val temp = textEditListener
        textEditListener = null
        textColorV.color = tp.textColor
        // textFontV.selectedIndex=
        textStyleBoldV.setChecked(true, false)
        textStyleItalicV.setChecked(true, false)
        textStyleAllCapV.setChecked(true, false)
        textSizeV.progress = tp.textSize.toInt()
        textStrokeV.isChecked = tp.stroked
        textStrokeColorV.color = tp.strokeColor
        textStrokeSizeV.progress = tp.strokeWidth.toInt()
        textEditListener = temp
    }

    override fun onColorSelection(dialog: ColorChooserDialog, selectedColor: Int) {
        if (dialog.tag() == "textColor") {
            textColorV.color = (selectedColor)
        } else {
            textStrokeColorV.color = (selectedColor)
        }
    }

    override fun onColorChooserDismissed(dialog: ColorChooserDialog) {

    }
}
