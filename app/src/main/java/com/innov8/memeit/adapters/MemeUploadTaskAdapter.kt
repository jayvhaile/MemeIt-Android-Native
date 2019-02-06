package com.innov8.memeit.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.work.WorkInfo
import androidx.work.WorkInfo.*
import androidx.work.WorkManager
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memeit.commons.MyViewHolder
import com.innov8.memeit.R
import com.innov8.memeit.commons.SimpleELEListAdapter


class MemeUploadTaskAdapter(context: Context) : SimpleELEListAdapter<WorkInfo>(context, R.layout.list_item_ongoing_uploads) {
    override var emptyDrawableId: Int = R.drawable.empty_list
    override var errorDrawableId: Int = R.drawable.empty_list
    override var emptyDescription: String = "No Upload Running"
    override var errorDescription: String = "Could not load Upload tasks"
    override var emptyActionText: String? = null
    override var errorActionText: String? = null
    override val loadingDrawable: Drawable = CubeGrid().apply {
        color = Color.rgb(255, 100, 0)
    }
    init {
        hasMore=false
    }


    override fun createViewHolder(view: View): MyViewHolder<WorkInfo> {

        return MemeUploadTaskViewHolder(view)
    }

    inner class MemeUploadTaskViewHolder(itemView: View) : MyViewHolder<WorkInfo>(itemView) {
        private val imageV: ImageView = itemView.findViewById(R.id.meme_preview)
        private val statusV: TextView = itemView.findViewById(R.id.upload_status)
        private val cancelV: ImageView = itemView.findViewById(R.id.delete_upload)

        init {
            cancelV.setOnClickListener {
                WorkManager.getInstance().cancelWorkById(items[adapterPosition].id)
            }
        }

        override fun bind(t: WorkInfo) {
            statusV.text = when (t.state) {
                State.BLOCKED -> "Waiting for Connection"
                State.CANCELLED -> "Cancelled"
                State.ENQUEUED -> "Queued"
                State.FAILED -> "Failed"
                State.RUNNING -> "Running"
                State.SUCCEEDED -> "Meme Uploaded Successfully"
            }
            val p = t.outputData.getString("path") ?: ""
            imageV.setImageURI(Uri.parse(p))
            cancelV.visibility = when (t.state) {
                State.BLOCKED, State.ENQUEUED, State.RUNNING -> View.VISIBLE
                else -> View.GONE
            }
        }

    }
}