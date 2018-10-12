package com.innov8.memegenerator.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memegenerator.Adapters.PaintShapeInfo
import com.innov8.memegenerator.Adapters.PaintShapesListAdapter
import com.innov8.memegenerator.CustomViews.ColorChooser
import com.innov8.memegenerator.MemeEngine.PaintEditInterface
import com.innov8.memegenerator.R
import com.innov8.memegenerator.utils.ViewAdapter
import com.innov8.memegenerator.utils.listener
import com.warkiz.widget.IndicatorSeekBar
import kotlinx.android.synthetic.main.bottom_tab.*
import kotlinx.android.synthetic.main.paint_pager.*

class PaintOptionsFragment : Fragment() {


    private val views = mutableListOf<View>()

    var paintEditInterface: PaintEditInterface? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.paint_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val paintShapesAdapter = PaintShapesListAdapter(context!!).apply {
            onSelectionChangedListener = {
                paintEditInterface?.onShapeChanged(it)
            }
        }
        paintShapesAdapter.addAll(PaintShapeInfo.getList())

        views.add(RecyclerView(context!!).apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = paintShapesAdapter
        })

        views.add(ColorChooser(context!!).apply {
            onColorChoosed = { paintEditInterface?.onBrushColorChanged(it) }
        })
        views.add(IndicatorSeekBar.with(context!!)
                .min(5f)
                .max(100f)
                .tickCount(1)
                .build().apply {
                    listener(onSeek = {
                        paintEditInterface?.onBrushSizeChanged(it.progressFloat)
                    })
                })

        paint_pager.adapter = Adapter(context!!)
        pager_tab.setupWithViewPager(paint_pager)
    }

    inner class Adapter(context: Context) : ViewAdapter(context) {
        private var titles = listOf("Shapes", "Brush Color", "Brush Size")
        override fun getItem(position: Int): View = views[position]
        override fun getCount(): Int = titles.size
        override fun getPageTitle(position: Int): CharSequence? = titles[position]
    }

}