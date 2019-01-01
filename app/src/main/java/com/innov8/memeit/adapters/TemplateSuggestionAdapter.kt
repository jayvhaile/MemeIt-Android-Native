package com.innov8.memeit.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequest
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memegenerator.MemeEditorActivity
import com.innov8.memegenerator.adapters.MyViewHolder
import com.innov8.memeit.R
import com.innov8.memeit.utils.generatePreviewUrl
import com.memeit.backend.models.MemeTemplate

class TemplateSuggestionAdapter(context: Context) : SimpleELEListAdapter<MemeTemplate>(context, R.layout.list_item_template_sug) {
    override var emptyDrawableId: Int = R.drawable.tag2
    override var errorDrawableId: Int = R.drawable.ic_no_internet
    override var emptyDescription: String = "There is no template yet"
    override var errorDescription: String = "Failed to load Templates"
    override var errorActionText: String? = "Try Again"
    override var emptyActionText: String? = null
    override val loadingDrawable = CubeGrid().apply {
        color = Color.rgb(255, 100, 0)
    }

    init {
        hasMore = false
    }

    override fun createViewHolder(view: View): MyViewHolder<MemeTemplate> {
        return TemplateSuggestionViewHolder(view)
    }

    inner class TemplateSuggestionViewHolder(itemView: View) : MyViewHolder<MemeTemplate>(itemView) {
        private val templateImageV: SimpleDraweeView = itemView.findViewById(R.id.template_image)
        private val labelV: TextView = itemView.findViewById(R.id.template_label)

        init {
            itemView.setOnClickListener {
                MemeEditorActivity.startWithTemplate(context as Activity, getItemAt(item_position))
            }
        }


        @SuppressLint("SetTextI18n")
        override fun bind(t: MemeTemplate) {
            labelV.text = t.label
            templateImageV.setImageRequest(ImageRequest.fromUri(t.generatePreviewUrl()))
        }
    }
}