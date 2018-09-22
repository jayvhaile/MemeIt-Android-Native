package com.innov8.memegenerator.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.innov8.memegenerator.R
import com.innov8.memegenerator.customViews.MyToolbarmenu
import com.innov8.memegenerator.memeEngine.LayoutEditInterface
import com.innov8.memegenerator.utils.fromDPToPX
import com.innov8.memegenerator.utils.onProgressChanged
import com.innov8.memegenerator.utils.onTabSelected
import com.innov8.memegenerator.utils.replace
import kotlinx.android.synthetic.main.tab.*
import kotlinx.android.synthetic.main.tab_customize_layout.*

class MemeLayoutEditorFragment : MemeEditorFragment() {
    override val menus: List<MyToolbarmenu>
        get() = listOf()

    lateinit var layoutPresetsFragment: LayoutPresetsFragment
    lateinit var layoutCustomizeFragment: LayoutCustomizeFragment
    lateinit var layoutEditInterface: LayoutEditInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutPresetsFragment = LayoutPresetsFragment()
        layoutCustomizeFragment = LayoutCustomizeFragment()
        setListener(layoutEditInterface)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meme_editor_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout.onTabSelected {
            when (it.position) {
                0 -> {
                    childFragmentManager.replace(R.id.holder, layoutPresetsFragment)
                }
                1 -> {
                    childFragmentManager.replace(R.id.holder, layoutCustomizeFragment)

                }
            }
        }
        childFragmentManager.replace(R.id.holder, layoutPresetsFragment)
    }
    fun setListener(layoutEditInterface: LayoutEditInterface){
        layoutCustomizeFragment.layoutEditInterface=layoutEditInterface
    }
}

class LayoutPresetsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.custom_view_testing_layout, container, false)

    }
}

class LayoutCustomizeFragment : Fragment() , ColorChooserDialog.ColorCallback{

    var layoutEditInterface: LayoutEditInterface? = null

    lateinit var colorChooserDialog:ColorChooserDialog
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tab_customize_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        margin_left.onProgressChanged { progress, u ->
            if (u)
                layoutEditInterface?.onLeftMargin(progress.fromDPToPX(context!!))
        }
        margin_right.onProgressChanged { progress, u ->
            if (u)
                layoutEditInterface?.onRightMargin(progress.fromDPToPX(context!!))
        }
        margin_bottom.onProgressChanged { progress, u ->
            if (u)
                layoutEditInterface?.onBottomMargin(progress.fromDPToPX(context!!))
        }
        margin_top.onProgressChanged { progress, u ->
            if (u)
                layoutEditInterface?.onTopMargin(progress.fromDPToPX(context!!))
        }
        margin_all.onProgressChanged { progress, u ->
            if (u)
                layoutEditInterface?.onAllMarginSet(progress.fromDPToPX(context!!))
        }
        margin_all.isEnabled = all_margin_check.isChecked

        all_margin_check.setOnCheckedChangeListener { _, isChecked ->

            margin_all.isEnabled = isChecked
            margin_left.isEnabled = !isChecked
            margin_right.isEnabled = !isChecked
            margin_bottom.isEnabled = !isChecked
            margin_top.isEnabled = !isChecked

            if (isChecked) {
                layoutEditInterface?.onAllMarginSet(margin_all.progress.fromDPToPX(context!!))
            } else {
                layoutEditInterface?.onAllMarginSet(margin_left.progress.fromDPToPX(context!!),
                        margin_top.progress.fromDPToPX(context!!),
                        margin_right.progress.fromDPToPX(context!!),
                        margin_bottom.progress.fromDPToPX(context!!))
            }
        }
        colorChooserDialog = ColorChooserDialog.Builder(context!!, R.string.color_chooser_dialog_title)
                .dynamicButtonColor(false)
                .build()
        margin_color.setOnClickListener { colorChooserDialog.show(childFragmentManager, "backgroundColor") }

        margin_color.onColorChanged={
            layoutEditInterface?.onBackgroundColorChanged(it)
        }
    }
    override fun onColorSelection(dialog: ColorChooserDialog, selectedColor: Int) {
            margin_color.color = (selectedColor)
    }

    override fun onColorChooserDismissed(dialog: ColorChooserDialog) {

    }

}


