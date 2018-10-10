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
import com.innov8.memegenerator.CustomViews.MarginControlView
import com.innov8.memegenerator.MemeEngine.LayoutEditInterface
import kotlinx.android.synthetic.main.bottom_tab.*
import kotlinx.android.synthetic.main.layout_pager.*

class LayoutEditorFragment : Fragment() {
    private val views = mutableListOf<View>()

    var layoutEditListener: LayoutEditInterface? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mc = MarginControlView(context!!)
        mc.layoutEditInterface = layoutEditListener
        views.add(mc)
        val cc = ColorChooser(context!!)
        cc.onColorChoosed = {
            layoutEditListener?.onBackgroundColorChanged(it)
        }
        views.add(cc)

        text_pager.adapter = Adapter(context!!)
        pager_tab.setupWithViewPager(text_pager)
    }


    inner class Adapter(context: Context) : ViewAdapter(context) {
        private var titles = listOf("Margin", "Background")


        init {

        }

        override fun getItem(position: Int): View = views[position]


        override fun getCount(): Int = titles.size

        override fun getPageTitle(position: Int): CharSequence? = titles[position]

    }
}