package com.innov8.memeit.Adapters.MemeAdapters

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memeit.Adapters.ELEListAdapter
import com.innov8.memeit.Adapters.MemeAdapters.ViewHolders.MemeViewHolder
import com.innov8.memeit.CustomClasses.LoadingDrawable
import com.innov8.memeit.R
import com.innov8.memeit.generateUrl
import com.memeit.backend.dataclasses.HomeElement
import com.memeit.backend.dataclasses.Meme
import com.stfalcon.frescoimageviewer.ImageViewer

abstract class MemeAdapter(context: Context) : ELEListAdapter<HomeElement, MemeViewHolder>(context) {
    override var emptyDrawableId: Int = R.drawable.ic_add
    override var errorDrawableId: Int = R.drawable.ic_no_internet
    override var emptyDescription: String = "No Memes"
    override var errorDescription: String = "Couldn't load Memes"
    override var emptyActionText: String? = ""
    override var errorActionText: String? = "Try Again"
    override val loadingDrawable = CubeGrid().apply {
        color = Color.rgb(255, 100, 0)
    }


    override fun getItemType(position: Int): Int = items[position].itemType

    companion object {
        const val LIST_ADAPTER: Byte = 1
        const val LIST_FAVORITE_ADAPTER: Byte = 4
        const val GRID_ADAPTER: Byte = 2
        const val HOME_ADAPTER: Byte = 3
        fun create(type: Byte, context: Context): MemeAdapter = when (type) {
            LIST_ADAPTER -> MemeListAdapter(context)
            LIST_FAVORITE_ADAPTER -> MemeListAdapter(context).apply {
                emptyDrawableId = R.drawable.ic_favorite_black_24dp
                emptyDescription = "Favorite Collection is Empty"
            }
            GRID_ADAPTER -> GridMemeAdapter(context).apply {
                emptyDrawableId = R.drawable.ic_favorite_black_24dp
                emptyDescription = "Meme upload list is empty"
            }
            HOME_ADAPTER -> HomeMemeAdapter(context)
            else ->
                throw IllegalArgumentException("Use one of (LIST_ADAPTER,GRID_ADAPTER,HOME_ADAPTER)")
        }

        const val LOADING_TYPE = 5864
    }


    override fun onCreateHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val memeViewHolder = createHolder(parent, viewType)
        memeViewHolder.memeClickedListener = { memeID ->
            val list: List<Meme> = items.filter { it is Meme }
                    .map { it as Meme }
                    .toList()
            val hierarchy = GenericDraweeHierarchyBuilder.newInstance(context.resources)
                    .setProgressBarImage(LoadingDrawable(context))

            val overlayView = inflater.inflate(R.layout.overlay, null, false)
            val overlayName = overlayView.findViewById<TextView>(R.id.overlay_name)
            val overlayDesc = overlayView.findViewById<TextView>(R.id.overlay_description)
            val overlayTags = overlayView.findViewById<TextView>(R.id.overlay_tags)
            ImageViewer.Builder<Meme>(context, list)
                    .setFormatter { it.generateUrl() }
                    .setOverlayView(overlayView)
                    .setImageChangeListener {
                        overlayName.text = list[it].poster?.name
                        overlayDesc.text = list[it].description
                        overlayTags.text = list[it].tags.joinToString(", ") { tag -> "#$tag" }
                    }
                    .setCustomDraweeHierarchyBuilder(hierarchy)
                    .setBackgroundColor(Color.parseColor("#f6000000"))
                    .setStartPosition(list.map { it.id }.indexOf(memeID))
                    .hideStatusBar(false)
                    .show()

        }
        return memeViewHolder
    }

    override fun onBindHolder(holder: MemeViewHolder, position: Int) {
        holder.itemPosition = position
        holder.bind(items[position])
    }

    abstract fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder

    open fun createLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)


}