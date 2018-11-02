package com.innov8.memegenerator.Adapters

import android.content.Context
import android.view.View
import com.innov8.memegenerator.MemeEngine.MemeTextView
import com.innov8.memegenerator.Models.TextPreset
import com.innov8.memegenerator.R

class TextPresetsAdapter(context:Context) : ListAdapter<TextPreset>(context,R.layout.list_item_text_preset) {
    var onItemClick:((TextPreset)->Unit)?=null
    override fun createViewHolder(view: View): MyViewHolder<TextPreset> {
        return TextpresetViewHolder(view)
    }

    inner class TextpresetViewHolder(view: View): MyViewHolder<TextPreset>(view) {
        private val presetV: MemeTextView = view.findViewById(R.id.text_preset_view)
        init {
            presetV.onClickListener={
                onItemClick?.invoke(getItemAt(item_position))
            }
        }
        override fun bind(t: TextPreset) {
            presetV.applyTextStyleProperty(t.textStyleProperty,text = "Sample")

        }
    }
}