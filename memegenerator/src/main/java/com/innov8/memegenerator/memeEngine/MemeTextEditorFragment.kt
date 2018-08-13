package com.innov8.memegenerator.memeEngine


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.SeekBar
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.innov8.memegenerator.R
import com.innov8.memegenerator.customViews.ColorView
import com.innov8.memegenerator.customViews.FontChooserView
import com.innov8.memegenerator.customViews.ToggleImageButton
import com.innov8.memegenerator.utils.onProgressChanged

class MemeTextEditorFragment : Fragment(), ColorChooserDialog.ColorCallback {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.tab_customize_text, container, false)
        init(view)
        initEvent()
        return view
    }

    //todo make the color chooser and font chooser self contained view
    lateinit var textSizeV: SeekBar
    lateinit var textColorV: ColorView
    lateinit var textfontV: FontChooserView
    lateinit var textStyleBoldV: ToggleImageButton
    lateinit var textStyleItalicV: ToggleImageButton
    lateinit var textStyleAllCapV: ToggleImageButton
    lateinit var textStrokeV: CheckBox
    lateinit var textStrokeSizeV: SeekBar
    lateinit var textStrokeColorV: ColorView

    lateinit var textEditInterface: TextEditInterface
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
        textColorV.onColorChanged = { textEditInterface.onTextColorChanged(it) }
        textfontV.setOnItemSelectedListener({ _, _, _, _ ->
            textEditInterface.onTextFontChanged(textfontV.getSelectedFont())
        })
        textSizeV.onProgressChanged { progress, fromuser ->
            if (fromuser)
                textEditInterface.onTextSizeChanged(progress.toFloat())
        }
        textStrokeV.setOnCheckedChangeListener({ compoundButton, b ->
            textEditInterface.onTextSetStroked(b)
        })
        textStrokeColorV.onColorChanged={textEditInterface.onTextStrokrColorChanged(it)}
        textStrokeSizeV.onProgressChanged { progress, fromUser  ->
            if (fromUser)
                textEditInterface.onTextStrokeChanged(progress.toFloat())
        }
        textStyleAllCapV.onCheckChanged={checked, fromUser ->
            if (fromUser)
                textEditInterface.onTextSetAllCap(checked)
        }
    }

    override fun onColorSelection(dialog: ColorChooserDialog, selectedColor: Int) {
        if (dialog.tag() == "textColor") {
            textColorV.color = selectedColor
        } else {
            textStrokeColorV.color = selectedColor
        }
    }

    override fun onColorChooserDismissed(dialog: ColorChooserDialog) {

    }


}
