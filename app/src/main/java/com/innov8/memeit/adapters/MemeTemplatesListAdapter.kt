package com.innov8.memeit.adapters

import android.content.Context
import android.view.View
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.innov8.memegenerator.R
import com.innov8.memegenerator.adapters.ListAdapter
import com.innov8.memegenerator.adapters.MyViewHolder
import com.memeit.backend.models.MemeTemplate
//todo fix this stupid class
class MemeTemplatesListAdapter (context: Context): ListAdapter<MemeTemplate>(context,R.layout.list_item_meme_template) {
    override fun createViewHolder(view: View): MyViewHolder<MemeTemplate> {
        return MemeTemplateViewHolder(view)
    }


    inner class MemeTemplateViewHolder(view: View) : MyViewHolder<MemeTemplate>(view) {
        private val memeTemplateImageV: SimpleDraweeView = view.findViewById(R.id.meme_template_image)
        private val memeTemplateLabelV: TextView = view.findViewById(R.id.meme_template_label)
        init {
            itemView.setOnClickListener({onItemClicked?.invoke(getItemAt(item_position))})
        }
        override fun bind(t: MemeTemplate) {

               /* memeTemplateImageV.setImageRequest(
                        ImageRequestBuilder.newBuilderWithResourceId(context.getDrawableIdByName(t.imageURL))
                                .build()
                )*/

            memeTemplateLabelV.text = t.label
        }

    }
}