package com.innov8.memeit.admin.Adapters

import android.content.Context
import android.graphics.Color
import android.view.View
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequest
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memeit.commons.MyViewHolder
import com.innov8.memeit.R
import com.innov8.memeit.commons.SimpleELEListAdapter
import com.innov8.memeit.commons.toast
import com.innov8.memeit.utils.generatePreviewUrl
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.models.MemeTemplate

class TemplateManagerAdapter(context: Context) : SimpleELEListAdapter<MemeTemplate>(context, R.layout.list_item_template_manager) {
    override var emptyDrawableId: Int = R.drawable.no_comments
    override var errorDrawableId: Int = R.drawable.ic_no_internet
    override var emptyDescription: String = "No Templates yet."
    override var errorDescription: String = "Couldn't load templates"
    override var emptyActionText: String? = ""
    override var errorActionText: String? = "Try Again"
    override val loadingDrawable = CubeGrid().apply {
        color = Color.rgb(255, 100, 0)
    }

    override fun createViewHolder(view: View): MyViewHolder<MemeTemplate> {
        return TemplateManagerViewHolder(view)
    }

    inner class TemplateManagerViewHolder(itemView: View) : MyViewHolder<MemeTemplate>(itemView) {
        private val templateV: SimpleDraweeView = itemView.findViewById(R.id.template)
        private val approve: View = itemView.findViewById(R.id.approve)
        private val decline: View = itemView.findViewById(R.id.decline)

        init {
            approve.setOnClickListener { view ->
                MemeItMemes.approveTemplate(getItemAt(adapterPosition)._id!!).call({
                    context.toast("Approved")
                }) {
                    context.toast("Approve Failed, $it")
                }
            }
            decline.setOnClickListener {
                MemeItMemes.declineTemplate(getItemAt(adapterPosition)._id!!).call({
                    context.toast("Declined")
                }) {
                    context.toast("Decline Failed, $it")
                }
            }
        }

        override fun bind(t: MemeTemplate) {
            templateV.setImageRequest(ImageRequest.fromUri(t.generatePreviewUrl()))
        }
    }
}