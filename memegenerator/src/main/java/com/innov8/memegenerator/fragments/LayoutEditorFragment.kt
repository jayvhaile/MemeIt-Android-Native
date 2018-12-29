package com.innov8.memegenerator.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memegenerator.adapters.LayoutPresetsAdapter
import com.innov8.memegenerator.customViews.ColorChooser
import com.innov8.memegenerator.customViews.MarginControlView
import com.innov8.memegenerator.customViews.SpacingControlView
import com.innov8.memegenerator.interfaces.LayoutEditInterface
import com.innov8.memegenerator.memeEngine.MemeLayout
import com.innov8.memegenerator.R
import com.innov8.memegenerator.utils.ViewAdapter
import kotlinx.android.synthetic.main.bottom_tab.*
import kotlinx.android.synthetic.main.layout_pager.*

class LayoutEditorFragment : Fragment() {
    private val views = mutableListOf<View>()

    var layoutEditListener: LayoutEditInterface? = null

    var memeLayout: MemeLayout? = null
        set(value) {
            field = value
            apply()
        }


    fun apply() {
        context ?: return
        memeLayout ?: return

        adapter.setAll(memeLayout!!.loadPresets().toList())
    }

    val adapter by lazy {
        LayoutPresetsAdapter(context!!).apply {
            onItemClick = { layoutEditListener?.onLayoutSet(it) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.add(RecyclerView(context!!).apply {
            layoutManager = LinearLayoutManager(context!!, RecyclerView.HORIZONTAL, false)
            adapter = this@LayoutEditorFragment.adapter
            apply()
        })

        val mc = MarginControlView(context!!)
        mc.layoutEditInterface = layoutEditListener
        views.add(mc)

        val sc = SpacingControlView(context!!)
        sc.layoutEditInterface = layoutEditListener
        views.add(sc)

        val cc = ColorChooser(context!!)
        cc.onColorChoosed = {
            layoutEditListener?.onBackgroundColorChanged(it)
        }
        views.add(cc)

        text_pager.adapter = Adapter(context!!)
        pager_tab.setupWithViewPager(text_pager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        views.clear()
    }


    inner class Adapter(context: Context) : ViewAdapter(context) {
        private var titles = listOf("Presets", "Margin", "Spacing", "Background")

        override fun getItem(position: Int): View = views[position]

        override fun getCount(): Int = views.size

        override fun getPageTitle(position: Int): CharSequence? = titles[position]

    }
}