package com.innov8.memeit.Adapters

import android.content.ContentUris.withAppendedId
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.innov8.memegenerator.MemeEditorActivity
import com.innov8.memegenerator.MemePosterActivity
import com.innov8.memeit.log
import com.innov8.memeit.R

class PhotosAdapter(val context: Context) : RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder>() {
    var cursor: Cursor? = null
    var OnItemClicked:((String)->Unit)?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_thumbnail, parent, false)
        return PhotoViewHolder(view)
    }

    override fun getItemCount(): Int = cursor?.count ?: 0


    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.item_position=position
        holder.bind(getURI(position))
    }

    fun getURI(position: Int): String {
        val c = cursor!!
        c.moveToPosition(position)
        val id = c.getLong(c.getColumnIndex(MediaStore.Images.Media._ID))
        val uri = withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        return uri.toString()
    }
    fun getURIa(position: Int): String {
        val c = cursor!!
        c.moveToPosition(position)
        val u=c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA))
        log(u)
        log(Uri.parse(u).toString())
        return u
    }

    fun swapCursor(cursor: Cursor?) {
        this.cursor = cursor
        notifyDataSetChanged()
    }
    val screenWidth = context.resources.displayMetrics.widthPixels

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnailV: SimpleDraweeView = itemView.findViewById(R.id.thumbnail)
        init {
            thumbnailV.setOnClickListener {
                val c = cursor!!
                c.moveToPosition(item_position)
                val mime = c.getString(c.getColumnIndex(MediaStore.Images.Media.MIME_TYPE))
                log("fiki",mime)
                if(mime=="image/gif"){
                    val uri= c.getString(c.getColumnIndex(MediaStore.Video.Media.DATA))
                    val intent= Intent(context, MemePosterActivity::class.java)
                    intent.putExtra("gif",uri)
                    context.startActivity(intent)
                }else{
                    val id = c.getLong(c.getColumnIndex(MediaStore.Images.Media._ID))
                    val uri = withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    val intent= Intent(context, MemeEditorActivity::class.java)
                    intent.putExtra("uri",uri.toString())
                    context.startActivity(intent)
                }
            }
        }
        var item_position:Int=0;

        fun bind(uri: String) {
            val r = ResizeOptions(screenWidth/
                    3,screenWidth/3,1024f)
            val req=ImageRequestBuilder.fromRequest(ImageRequest.fromUri(uri))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .setResizeOptions(r)
                    .build()
            thumbnailV.setImageRequest(req)
        }
    }
}
class VideoAdapter(val context: Context) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {
    var cursor: Cursor? = null
    var OnItemClicked:((String)->Unit)?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_thumbnail, parent, false)
        return VideoViewHolder(view)
    }

    override fun getItemCount(): Int = cursor?.count ?: 0


    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.item_position=position
        holder.bind(getURI(position))
    }

    fun getURI(position: Int): String {
        val c = cursor!!
        c.moveToPosition(position)
        val id = c.getLong(c.getColumnIndex(MediaStore.Video.Media._ID))
        val uri = withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
        return uri.toString()
    }
    fun getDataURI(position: Int): String {
        val c = cursor!!
        c.moveToPosition(position)
        val uri= c.getString(c.getColumnIndex(MediaStore.Video.Media.DATA))
        return uri
    }

    fun swapCursor(cursor: Cursor?) {
        this.cursor = cursor
        notifyDataSetChanged()
    }
    val screenWidth = context.resources.displayMetrics.widthPixels

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnailV: SimpleDraweeView = itemView.findViewById(R.id.thumbnail)
        init {
            thumbnailV.setOnClickListener {
                val intent= Intent(context, MemePosterActivity::class.java)
                intent.putExtra("gif",getDataURI(item_position))
                context.startActivity(intent)
            }
        }
        var item_position:Int=0;
        fun bind(uri: String) {
            val r = ResizeOptions(screenWidth/3,screenWidth/3,1024f)
            val req=ImageRequestBuilder.fromRequest(ImageRequest.fromUri(uri))
                    .setLocalThumbnailPreviewsEnabled(false)
                   // .setResizeOptions(r)
                    .build()
            val controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(req)
                    .setAutoPlayAnimations(true)
                    .setOldController(thumbnailV.controller)
                    .build()
            thumbnailV.controller=controller
            val x=thumbnailV.controller?.animatable
            log(x==null)
        }

    }
}