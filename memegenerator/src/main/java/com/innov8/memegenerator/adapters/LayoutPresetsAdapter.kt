package com.innov8.memegenerator.adapters

import android.content.Context
import android.view.View
import android.widget.TextView
import com.innov8.memegenerator.customViews.LayoutPresetView
import com.innov8.memegenerator.memeEngine.MemeLayout
import com.innov8.memegenerator.R

class LayoutPresetsAdapter(context: Context) : ListAdapter<Pair<String, MemeLayout>>(context, R.layout.list_item_layout_presets) {
    var onItemClick: ((MemeLayout) -> Unit)? = null


    override fun createViewHolder(view: View): MyViewHolder<Pair<String, MemeLayout>> {
        return LayoutPresetViewHolder(view)
    }

    inner class LayoutPresetViewHolder(view: View) : MyViewHolder<Pair<String, MemeLayout>>(view) {
        private val presetV: LayoutPresetView = view.findViewById(R.id.layout_preset_view)
        private val presetDesc: TextView = view.findViewById(R.id.layout_preset_desc)

        init {
            presetV.setOnClickListener {
                onItemClick?.invoke(getItemAt(item_position).second)
            }
        }

        override fun bind(t: Pair<String, MemeLayout>) {
            presetDesc.text = t.first
            presetV.update(t.second)
        }
    }
}