package com.innov8.memeit.adapters

import android.app.Activity
import android.content.ContentUris.withAppendedId
import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.innov8.memegenerator.adapters.ListAdapter
import com.innov8.memegenerator.adapters.MyViewHolder
import com.innov8.memegenerator.MemeEditorActivity
import com.innov8.memeit.adapters.GifAdapter.Gif
import com.innov8.memeit.customViews.CountView
import com.innov8.memeit.R


class PhotosAdapter(context: Context) : CursorAdapter<String>(context, R.layout.list_item_thumbnail_selectable) {
    val screenWidth = context.resources.displayMetrics.widthPixels

    var onSelectedItemChanged:((List<String>)->Unit)?=null
    val selectedItems = mutableListOf<String>()
    var onCropListener: ((String) -> Unit)? = null
    var multiSelectMode = false
        set(value) {
            field = value
            if (!field) selectedItems.clear()
            onSelectedItemChanged?.invoke(selectedItems)
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
        private val cropView: ImageView = itemView.findViewById(R.id.thumbnail_crop)

        init {
            thumbnailV.setOnClickListener {
                if (multiSelectMode) {

                } else
                    MemeEditorActivity.startWithImage(context as Activity, getItem(item_position))
            }
            cropView.setOnClickListener {
                onCropListener?.invoke(getItem(item_position))
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
                onSelectedItemChanged?.invoke(selectedItems)
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
                cropView.visibility = View.GONE
                if (selectedItems.contains(t)) {
                    countView.count = selectedItems.indexOf(t) + 1
                    countView.choosed = true
                } else countView.choosed = false
            } else {
                cropView.visibility = View.VISIBLE
                countView.visibility = View.GONE
            }
        }
    }
}

class PhotosAdapterList(context: Context) : ListAdapter<String>(context, R.layout.list_item_thumbnail_horizontal) {
    val screenWidth = context.resources.displayMetrics.widthPixels

    var onCropListener: ((String) -> Unit)? = null


    override fun createViewHolder(view: View): MyViewHolder<String> = PhotoViewHolder(view)


    inner class PhotoViewHolder(itemView: View) : MyViewHolder<String>(itemView) {
        private val thumbnailV: SimpleDraweeView = itemView.findViewById(R.id.thumbnail)
        private val cropView: ImageView = itemView.findViewById(R.id.thumbnail_crop)

        init {
            thumbnailV.setOnClickListener {
                //MemeEditorActivity.startWithImage(context, items[item_position])
            }
            cropView.setOnClickListener {
                onCropListener?.invoke(items[item_position])
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

class GifAdapter(context: Context) : CursorAdapter<Gif>(context, R.layout.list_item_thumbnail) {
    val screenWidth = context.resources.displayMetrics.widthPixels

    data class Gif(val uri: String, val data: String)

    override fun createViewHolder(view: View): MyViewHolder<Gif> = GifHolder(view)

    override fun getItem(cursor: Cursor): Gif {
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
        val uri = withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id).toString()
        val data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        return Gif(uri, data)
    }

    inner class GifHolder(itemView: View) : MyViewHolder<Gif>(itemView) {
        private val thumbnailV: SimpleDraweeView = itemView.findViewById(R.id.thumbnail)

        init {

            thumbnailV.setOnClickListener {
                //                context.startActivity(Intent(context, MemePosterActivity::class.java).apply {
//                    putExtra("gif", getItem(item_position).data)
//                })
                MemeEditorActivity.startWithGif(context as Activity, getItem(item_position).data)
            }
        }

        override fun bind(t: Gif) {
            val r = ResizeOptions(screenWidth / 6, screenWidth / 6, 512f)
            val req = ImageRequestBuilder.fromRequest(ImageRequest.fromUri(t.uri))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .setResizeOptions(r)
                    .build()
            thumbnailV.setImageRequest(req)
        }
    }
}

class VideoAdapter(context: Context) : CursorAdapter<VideoAdapter.Video>(context, R.layout.list_item_thumbnail) {
    val screenWidth = context.resources.displayMetrics.widthPixels

    val ret by lazy { MediaMetadataRetriever() }

    data class Video(val uri: String, val data: String)

    override fun createViewHolder(view: View): MyViewHolder<Video> = VideoHolder(view)

    override fun getItem(cursor: Cursor): Video {
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID))
        val uri = withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id).toString()
        val data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
        return Video(uri, data)
    }

    inner class VideoHolder(itemView: View) : MyViewHolder<Video>(itemView) {
        private val thumbnailV: SimpleDraweeView = itemView.findViewById(R.id.thumbnail)

        init {

            thumbnailV.setOnClickListener {
                //                context.startActivity(Intent(context, MemePosterActivity::class.java).apply {
//                    putExtra("gif", getItem(item_position).data)
//                })
                MemeEditorActivity.startWithVideo(context as Activity, getItem(item_position).data)
            }
        }

        override fun bind(t: Video) {
          /*  ret.setDataSource(t.data)
            val dur = ret.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
            val b = ret.getFrameAtTime(1)
            val sb = Bitmap.createScaledBitmap(b, screenWidth / 6, screenWidth / 6, false)
            thumbnailV.setImageBitmap(sb)*/

            val r = ResizeOptions(screenWidth / 6, screenWidth / 6, 512f)
            val req = ImageRequestBuilder.fromRequest(ImageRequest.fromUri(t.uri))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .setResizeOptions(r)
                    .build()

//            thumbnailV.controller=
            thumbnailV.setImageRequest(req)
        }
    }
}