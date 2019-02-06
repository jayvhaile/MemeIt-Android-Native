package com.innov8.memegenerator.adapters

import android.content.Context
import android.view.View
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.innov8.memegenerator.R
import com.memeit.backend.models.Sticker
import com.innov8.memeit.commons.MyViewHolder
import com.innov8.memeit.commons.SimpleELEListAdapter
import com.innov8.memeit.commons.dp

class StickersAdapter(context: Context) : SimpleELEListAdapter<Sticker>(context, R.layout.list_item_sticker) {
    override fun createViewHolder(view: View): MyViewHolder<Sticker> {
        return StickerViewHolder(view)
    }

    override var emptyDrawableId: Int = R.drawable.ic_bottom_sticker
    override var errorDrawableId: Int = R.drawable.ic_bottom_sticker
    override var emptyDescription: String = "There are no stickers yet."
    override var errorDescription: String = "Couldn't load stickers"
    override var emptyActionText: String? = ""
    override var errorActionText: String? = "Try Again"

    var onItemClick: ((Sticker) -> Unit)? = null
    private val size = (80 - 2 * 18).dp(context)

    inner class StickerViewHolder(itemView: View) : MyViewHolder<Sticker>(itemView) {
        private val stickerV: SimpleDraweeView = itemView.findViewById(R.id.sticker_view)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(items[adapterPosition])
            }
        }

        override fun bind(t: Sticker) {
            val req = ImageRequestBuilder.fromRequest(ImageRequest.fromUri(t.getUrl(context)))
                    .setResizeOptions(ResizeOptions.forSquareSize(size))
                    .build()
            stickerV.setImageRequest(req)
        }

    }
}