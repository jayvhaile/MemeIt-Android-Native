package com.innov8.memeit.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequest
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memeit.commons.MyViewHolder
import com.innov8.memegenerator.MemeEditorActivity
import com.innov8.memeit.R
import com.innov8.memeit.commons.ELEFilterableListAdapter
import com.innov8.memeit.utils.LoadingDrawable
import com.innov8.memeit.utils.generatePreviewUrl
import com.memeit.backend.MemeItClient
import com.memeit.backend.models.MemeTemplate

class TemplateAdapter(context: Context,
                      override val sorter: Comparator<in MemeTemplate> = Comparator { o1, o2 ->
                          when {
                              o2.usageCount ?: 0 > o1.usageCount ?: 0 -> 1
                              o2.usageCount ?: 0 < o1.usageCount ?: 0 -> -1
                              o2.createdDate ?: 0 > o1.createdDate ?: 0 -> 1
                              o2.createdDate ?: 0 < o1.createdDate ?: 0 -> -1
                              else -> 0
                          }
                      })
    : ELEFilterableListAdapter<MemeTemplate, TemplateAdapter.TemplateViewHolder>(context) {
    override var emptyDrawableId: Int = R.drawable.ic_add
    override var errorDrawableId: Int = R.drawable.ic_no_internet
    override var emptyDescription: String = "There is no template yet"
    override var errorDescription: String = "Failed to load Templates"
    override var errorActionText: String? = "Try Again"
    override var emptyActionText: String? = null
    override val loadingDrawable = CubeGrid().apply {
        color = Color.rgb(255, 100, 0)
    }


    var type: String? = null
    var category: String? = null
    private var mine = false
    var filterWord = ""
    override val filterable: Boolean
        get() = type != null ||
                category != null ||
                mine ||
                filterWord.isNotBlank()

    override val filterer: (MemeTemplate) -> Boolean = {
        (filterWord.isBlank() || it.label.contains(filterWord) || it.tags.any { w -> w.toLowerCase().contains(filterWord.toLowerCase()) }) &&
                (type == null || it.memeType == type) &&
                (category == null || it.category == category) &&
                (!mine || it.pid == MemeItClient.myUser!!.id)
    }

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        return TemplateViewHolder(inflater.inflate(R.layout.list_item_template, parent, false))
    }

    override fun onBindHolder(holder: TemplateViewHolder, position: Int) {
        holder.item_position = position
        holder.bind(getItemAt(position))
    }

    override fun getItemType(position: Int): Int = 0
    inner class TemplateViewHolder(itemView: View) : MyViewHolder<MemeTemplate>(itemView) {
        private val templateImageV: SimpleDraweeView = itemView.findViewById(R.id.template_image)
        private val labelV: TextView = itemView.findViewById(R.id.template_label)
        private val gifV: TextView = itemView.findViewById(R.id.meme_gif)

        init {
            itemView.setOnClickListener {
                MemeEditorActivity.startWithTemplate(context as Activity, getItemAt(item_position))
            }
            templateImageV.hierarchy.setProgressBarImage(LoadingDrawable(context))
        }


        @SuppressLint("SetTextI18n")
        override fun bind(t: MemeTemplate) {
            labelV.text = t.label
            templateImageV.setImageRequest(ImageRequest.fromUri(t.generatePreviewUrl()))
            gifV.visibility = if (t.memeType == "GIF") View.VISIBLE else View.GONE
        }
    }
}