package com.innov8.memegenerator.adapters

import android.content.Context
import android.view.View
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.innov8.memegenerator.R
import com.innov8.memegenerator.models.MemeTemplate
import com.innov8.memegenerator.utils.getDrawableIdByName

class MemeTemplatesListAdapter (context: Context): ListAdapter<MemeTemplate>(context,R.layout.list_item_meme_template) {
    override fun createViewHolder(view: View): MyViewHolder<MemeTemplate> {
        return MemeTemplateViewHolder(view)
    }


    inner class MemeTemplateViewHolder(view: View) : MyViewHolder<MemeTemplate>(view) {
        private val memeTemplateImageV: SimpleDraweeView = view.findViewById(R.id.meme_template_image)
        private val memeTemplateLabelV: TextView = view.findViewById(R.id.meme_template_label)
        init {
            itemView.setOnClickListener({OnItemClicked?.invoke(getItemAt(item_position))})
        }
        override fun bind(t: MemeTemplate) {

            if (t.dataSource == MemeTemplate.LOCAL_DATA_SOURCE) {
                memeTemplateImageV.setImageRequest(
                        ImageRequestBuilder.newBuilderWithResourceId(mContext.getDrawableIdByName(t.imageURL))
                                .build()
                )
            }else{
                memeTemplateImageV.setImageRequest(ImageRequest.fromUri(t.imageURL))

            }
            memeTemplateLabelV.text = t.label
        }

    }
}