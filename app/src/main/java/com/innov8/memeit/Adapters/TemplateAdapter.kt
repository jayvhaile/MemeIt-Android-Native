package com.innov8.memeit.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.work.WorkInfo
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequest
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memegenerator.Adapters.MyViewHolder
import com.innov8.memeit.R
import com.innov8.memeit.Workers.startTemplateDownloadWork
import com.memeit.backend.models.MemeTemplate
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import java.io.File

class TemplateAdapter(context: Context,
                      override val sorter: Comparator<in MemeTemplate> = Comparator { o1, o2 ->
                          when {
                              o2.usageCount > o1.usageCount -> 1
                              o2.usageCount < o1.usageCount -> -1
                              o2.createdDate > o1.createdDate -> 1
                              o2.createdDate < o1.createdDate -> -1
                              else -> 0
                          }
                      })
    : ELEFilterableListAdapter<MemeTemplate, TemplateAdapter.TemplateViewHolder>(context) {
    override val filterer: (MemeTemplate) -> Boolean = {
        it.label.contains(filterWord) || it.tags.any { w -> w.contains(filterWord) }
    }
    override var emptyDrawableId: Int = R.drawable.tag2
    override var errorDrawableId: Int = R.drawable.ic_no_internet
    override var emptyDescription: String = "Template not found"
    override var errorDescription: String = "Failed to load Templates"
    override var errorActionText: String? = "Try Again"
    override var emptyActionText: String? = null
    override val loadingDrawable = CubeGrid().apply {
        color = Color.rgb(255, 100, 0)
    }

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        return TemplateViewHolder(inflater.inflate(0, parent, false))
    }

    override fun onBindHolder(holder: TemplateViewHolder, position: Int) {
        holder.item_position = position
        holder.bind(getItemAt(position))
    }

    private fun getState(wis: List<WorkInfo>, id: String): WorkInfo.State? {
        val wi = wis.filter { it.tags.contains(id) }.map {
            it.state
        }
        return when {
            wi.isEmpty() -> return null
            wi.all { it == WorkInfo.State.SUCCEEDED } -> WorkInfo.State.SUCCEEDED
            wi.any { it == WorkInfo.State.RUNNING } -> WorkInfo.State.RUNNING
            wi.all { it == WorkInfo.State.FAILED } -> WorkInfo.State.FAILED
            else -> WorkInfo.State.ENQUEUED
        }
    }

    var workInfos = listOf<WorkInfo>()
        set(value) {
            val sb: DiffUtil.Callback = object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return getItemAt(oldItemPosition).id!! == getItemAt(newItemPosition).id!!
                }

                override fun getOldListSize(): Int = getCount()

                override fun getNewListSize(): Int = getCount()

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return getState(field, getItemAt(oldItemPosition).id!!) == getState(value, getItemAt(newItemPosition).id!!)
                }
            }
            val df = DiffUtil.calculateDiff(sb)
            field = value
            df.dispatchUpdatesTo(this)
        }

    var saved = arrayOf<String>()
    override fun getItemType(position: Int): Int = 0

    inner class TemplateViewHolder(itemView: View) : MyViewHolder<MemeTemplate>(itemView) {
        private val templateImageV: SimpleDraweeView = itemView.findViewById(R.id.template_image)
        private val progressV: ProgressBar = itemView.findViewById(R.id.template_progress)
        private val labelV: TextView = itemView.findViewById(R.id.template_label)
        private val saveV: TextView = itemView.findViewById(R.id.template_save)

        init {
            saveV.setOnClickListener {
                if (saveV.text == "Save") {
                    startTemplateDownloadWork(getItemAt(item_position))
                }
            }
        }


        @SuppressLint("SetTextI18n")
        override fun bind(t: MemeTemplate) {
            labelV.text = t.label
            val id = t.id!!
            val state = getState(workInfos, id)
            when (state) {
                WorkInfo.State.ENQUEUED -> {
                    progressV.visibility = View.GONE
                    saveV.text = "Saving..."
                }
                WorkInfo.State.RUNNING -> {
                    progressV.visibility = View.VISIBLE
                    progressV.isIndeterminate = true
                    saveV.text = "Saving..."

                }
                WorkInfo.State.SUCCEEDED -> {
                    saveV.text = "Saved"
                    progressV.visibility = View.GONE
                }
                else -> {
                    progressV.visibility = View.GONE
                    saveV.text = "Save"
                }
            }
            if (saved.contains("$id.json")) saveV.text = "Saved"
            val url = t.memeTemplateProperty.previewImageUrl
                    ?: t.memeTemplateProperty.images[0]
            //todo use cloudinary
            templateImageV.setImageRequest(ImageRequest.fromUri(url))
        }
    }
}