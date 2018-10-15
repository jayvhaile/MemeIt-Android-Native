package com.innov8.memeit.Adapters

import android.content.ContentUris.withAppendedId
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.MediaStore
import android.view.View
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.innov8.memegenerator.MemeEditorActivity
import com.innov8.memegenerator.Adapters.MyViewHolder
import com.innov8.memeit.Adapters.GifAdapter.Video
import com.innov8.memeit.CustomViews.CountView
import com.innov8.memeit.R
import com.memeit.backend.dataclasses.Meme


class PhotosAdapter(context: Context) : CursorAdapter<String>(context, R.layout.list_item_thumbnail_selectable) {
    val screenWidth = context.resources.displayMetrics.widthPixels

    val selectedItems = mutableListOf<String>()
    var multiSelectMode = false
        set(value) {
            field = value
            if (!field) selectedItems.clear()
            notifyDataSetChanged()
        }


    override fun createViewHolder(view: View): MyViewHolder<String> = PhotoViewHolder(view)

    override fun getItem(cursor: Cursor): String {
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
        return withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id).toString()
    }

    inner class PhotoViewHolder(itemView: View) : MyViewHolder<String>(itemView) {
        private val thumbnailV: SimpleDraweeView = itemView.findViewById(R.id.thumbnail)
        private val countView: CountView = itemView.findViewById(R.id.count_view)

        init {
            thumbnailV.setOnClickListener {
                MemeEditorActivity.startWithImage(context, getItem(item_position))
            }
            countView.setOnClickListener {
                if (countView.choosed) {
                    val t = getItem(item_position)
                    val index = selectedItems.indexOf(t)
                    val last = index == selectedItems.size - 1
                    selectedItems.remove(t)
                    if (last) notifyItemChanged(item_position)
                    else
                        notifyDataSetChanged()
                } else {
                    selectedItems.add(getItem(item_position))
                    notifyItemChanged(item_position)

                }
            }
        }

        override fun bind(t: String) {
            val tag = thumbnailV.tag as? String
            if (tag == null || tag != t) {
                thumbnailV.tag = t
                val r = ResizeOptions(screenWidth /
                        3, screenWidth / 3, 1024f)
                val req = ImageRequestBuilder.fromRequest(ImageRequest.fromUri(t))
                        .setLocalThumbnailPreviewsEnabled(true)
                        .setResizeOptions(r)
                        .build()
                thumbnailV.setImageRequest(req)
            }
            if (multiSelectMode) {
                countView.visibility = View.VISIBLE
                if (selectedItems.contains(t)) {
                    countView.count = selectedItems.indexOf(t) + 1
                    countView.choosed = true
                } else countView.choosed = false
            } else countView.visibility = View.GONE
        }
    }
}

class GifAdapter(context: Context) : CursorAdapter<Video>(context, R.layout.list_item_thumbnail) {
    val screenWidth = context.resources.displayMetrics.widthPixels

    data class Video(val uri: String, val data: String)

    override fun createViewHolder(view: View): MyViewHolder<Video> = GifHolder(view)

    override fun getItem(cursor: Cursor): Video {
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
        val uri = withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id).toString()
        val data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        return Video(uri, data)
    }

    inner class GifHolder(itemView: View) : MyViewHolder<Video>(itemView) {
        private val thumbnailV: SimpleDraweeView = itemView.findViewById(R.id.thumbnail)
        init {
            thumbnailV.setOnClickListener {
                MemeEditorActivity.startWithGif(context,getItem(item_position).data)
            }
        }
        override fun bind(t: Video) {
            val r = ResizeOptions(screenWidth / 3, screenWidth / 3, 1024f)
            val req = ImageRequestBuilder.fromRequest(ImageRequest.fromUri(t.uri))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .setResizeOptions(r)
                    .build()
            thumbnailV.setImageRequest(req)
        }
    }
}