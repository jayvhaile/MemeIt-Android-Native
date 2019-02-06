package com.innov8.memegenerator.adapters

import android.content.Context
import android.view.View
import com.innov8.memegenerator.R
import com.innov8.memegenerator.memeEngine.MemeTextItem
import com.innov8.memeit.commons.MyViewHolder
import com.memeit.backend.models.MemeTextStyleProperty

class TextPresetsAdapter(context: Context) : ListAdapter<Pair<MemeTextStyleProperty, MemeTextStyleProperty>>(context, R.layout.list_item_text_preset) {
    var onItemClick: ((MemeTextStyleProperty) -> Unit)? = null
    override fun createViewHolder(view: View): MyViewHolder<Pair<MemeTextStyleProperty, MemeTextStyleProperty>> {
        return TextpresetViewHolder(view)
    }

    inner class TextpresetViewHolder(view: View) : MyViewHolder<Pair<MemeTextStyleProperty, MemeTextStyleProperty>>(view) {
        private val presetV: MemeTextItem = view.findViewById(R.id.text_preset_view)

        init {
            presetV.onClickListener = {
                onItemClick?.invoke(getItemAt(adapterPosition).first)
            }
        }

        override fun bind(t: Pair<MemeTextStyleProperty, MemeTextStyleProperty>) {
            presetV.applyTextStyleProperty(t.second, text = "Sample")
        }
    }
}