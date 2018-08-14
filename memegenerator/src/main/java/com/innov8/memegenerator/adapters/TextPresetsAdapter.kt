package com.innov8.memegenerator.adapters

import android.content.Context
import android.view.View
import android.widget.TextView
import com.innov8.memegenerator.R
import com.innov8.memegenerator.memeEngine.MemeTextView
import com.innov8.memegenerator.models.TextPreset

class TextPresetsAdapter(context:Context) : ListAdapter<TextPreset>(context,R.layout.list_item_text_preset) {
    var onItemClick:((TextPreset)->Unit)?=null
    override fun createViewHolder(view: View): MyViewHolder<TextPreset> {
        return TextpresetViewHolder(view)
    }

    inner class TextpresetViewHolder(view: View): MyViewHolder<TextPreset>(view) {
        private val presetV:MemeTextView
        private val nameV:TextView
        init {
            presetV=view.findViewById(R.id.text_preset_view)
            nameV=view.findViewById(R.id.text_preset_name)
            presetV.onClickListener={
                onItemClick?.invoke(getItemAt(item_position))
            }
        }
        override fun bind(t: TextPreset, position: Int) {
            presetV.applyTextProperty(t.textProperty,text = "Sample")
            nameV.text = t.name
        }
    }
}