package com.innov8.memeit.Adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequest
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memegenerator.Adapters.MyViewHolder
import com.innov8.memegenerator.MemeEditorActivity
import com.innov8.memeit.Models.Draft
import com.innov8.memeit.R
import java.io.File

class DraftsAdapter(context: Context) : SimpleELEListAdapter<Draft>(context, R.layout.list_item_draft) {
    override fun createViewHolder(view: View): MyViewHolder<Draft> = DraftViewHolder(view)

    override var emptyDrawableId: Int = R.drawable.ic_comment
    override var errorDrawableId: Int = R.drawable.ic_no_internet
    override var emptyDescription: String = ""
    override var errorDescription: String = "Couldn't load Drafts"
    override var emptyActionText: String? = ""
    override var errorActionText: String? = "Try Again"
    override val loadingDrawable = CubeGrid().apply {
        color = Color.rgb(255, 100, 0)
    }


    inner class DraftViewHolder(itemView: View) : MyViewHolder<Draft>(itemView) {
        private val draftImageV: SimpleDraweeView = itemView.findViewById(R.id.draft_image)
        private val draftDeleteV: View = itemView.findViewById(R.id.draft_delete)

        init {
            draftDeleteV.setOnClickListener {
                getItemAt(item_position).let { draft ->
                    draft.delete()
                    remove(draft)
                }
            }
            draftImageV.setOnClickListener {
                MemeEditorActivity.startWithMemeTemplate(context as Activity, getItemAt(item_position).filePath)
            }
        }

        override fun bind(t: Draft) {
            draftImageV.setImageRequest(ImageRequest.fromFile(File(t.savedMemeTemplateProperty.previewImageUrl)))
        }

    }
}