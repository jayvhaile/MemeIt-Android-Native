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
import com.innov8.memegenerator.MemePosterActivity
import com.innov8.memegenerator.Adapters.MyViewHolder
import com.innov8.memeit.Adapters.VideoAdapter.Video
import com.innov8.memeit.R
import com.memeit.backend.dataclasses.Meme


class PhotosAdapter(context: Context) : CursorAdapter<String>(context, R.layout.list_item_thumbnail) {
    val screenWidth = context.resources.displayMetrics.widthPixels

    override fun createViewHolder(view: View): MyViewHolder<String> = PhotoViewHolder(view)

    override fun getItem(cursor: Cursor): String {
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
        return withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id).toString()
    }

    inner class PhotoViewHolder(itemView: View) : MyViewHolder<String>(itemView) {
        private val thumbnailV: SimpleDraweeView = itemView.findViewById(R.id.thumbnail)

        init {
            thumbnailV.setOnClickListener {
                val intent = Intent(context, MemeEditorActivity::class.java)
                intent.putExtra("uri", getItem(item_position))
                context.startActivity(intent)
            }

        }

        override fun bind(t: String) {
            val r = ResizeOptions(screenWidth /
                    3, screenWidth / 3, 1024f)
            val req = ImageRequestBuilder.fromRequest(ImageRequest.fromUri(t))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .setResizeOptions(r)
                    .build()
            thumbnailV.setImageRequest(req)
        }
    }
}
class VideoAdapter(context: Context) : CursorAdapter<Video>(context, R.layout.list_item_thumbnail) {
    val screenWidth = context.resources.displayMetrics.widthPixels
    data class Video(val uri:String,val data:String)
    override fun createViewHolder(view: View): MyViewHolder<Video> = VideoViewHolder(view)

    override fun getItem(cursor: Cursor): Video {
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
        val uri= withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id).toString()
        val data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        return Video(uri, data)
    }

    inner class VideoViewHolder(itemView: View) : MyViewHolder<Video>(itemView) {
        private val thumbnailV: SimpleDraweeView = itemView.findViewById(R.id.thumbnail)

        init {
            thumbnailV.setOnClickListener {
                val intent = Intent(context, MemeEditorActivity::class.java)
                intent.putExtra("uri", getItem(item_position).data)
                intent.putExtra("type",Meme.MemeType.GIF.toString())
                context.startActivity(intent)
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