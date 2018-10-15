package com.innov8.memeit.Adapters.MemeAdapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ShareCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.android.MediaManager
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.github.ybq.android.spinkit.style.CubeGrid
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.innov8.memegenerator.utils.toast
import com.innov8.memeit.*
import com.innov8.memeit.R
import com.innov8.memeit.Adapters.MemeAdapters.ViewHolders.MemeViewHolder
import com.innov8.memeit.Adapters.SwipeController
import com.innov8.memeit.CustomClasses.LoadingDrawable
import com.memeit.backend.dataclasses.*
import com.stfalcon.frescoimageviewer.ImageViewer

abstract class MemeAdapter(val context: Context) : RecyclerView.Adapter<MemeViewHolder>() {
    companion object {
        const val LIST_ADAPTER: Byte = 1
        const val GRID_ADAPTER: Byte = 2
        const val HOME_ADAPTER: Byte = 3
        fun create(type: Byte, context: Context): MemeAdapter = when (type) {
            LIST_ADAPTER -> MemeListAdapter(context)
            GRID_ADAPTER -> GridMemeAdapter(context)
            HOME_ADAPTER -> HomeMemeAdapter(context)
            else ->
                throw IllegalArgumentException("Use one of (LIST_ADAPTER,GRID_ADAPTER,HOME_ADAPTER)")
        }

        const val LOADING_TYPE = 5864
    }

    var loading: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                if (field)
                    notifyItemInserted(items.size)
                else
                    notifyItemRemoved(items.size)
            }

        }
    val items = mutableListOf<HomeElement>()
    fun addAll(homeElements: List<HomeElement>) {
        if (homeElements.isEmpty()) return
        val start = items.size
        items.addAll(homeElements)
        notifyItemRangeInserted(start, homeElements.size)
    }

    fun add(homeElement: HomeElement) {
        items.add(homeElement)
        notifyItemInserted(items.size - 1)
    }

    fun remove(homeElement: HomeElement) {
        if (items.contains(homeElement)) {
            val index = items.indexOf(homeElement)
            items.remove(homeElement)
            notifyItemRemoved(index)
        }
    }

    fun clear() {
        items.clear()
        log("setSe", "cleared")
        notifyDataSetChanged()
    }

    fun setAll(homeElements: List<HomeElement>) {
        items.clear()
        items.addAll(homeElements)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        return if (viewType == LOADING_TYPE) {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_list_meme_loading, parent, false)
            val progress = view.findViewById<ProgressBar>(R.id.meme_loading)
            val d = CubeGrid()
            d.color = Color.rgb(255, 100, 0)
            progress.indeterminateDrawable = d
            object : MemeViewHolder(view, this) {
                override fun bind(homeElement: HomeElement) {
                    //do nothing here
                }
            }
        } else {
            val memeViewHolder = createHolder(parent, viewType)
            memeViewHolder.memeClickedListener = { memeID ->
                val list: List<Meme> = items.filter { it is Meme }
                        .map { it as Meme }
                        .toList()
                val hierarchy = GenericDraweeHierarchyBuilder.newInstance(context.resources)
                        .setProgressBarImage(LoadingDrawable(context))

                ImageViewer.Builder<Meme>(context, list)
                        .setFormatter {
                            MediaManager.get()
                                    .url()
                                    .source(it.imageId)
                                    .generate()
                        }
                        .setCustomDraweeHierarchyBuilder(hierarchy)
                        .setBackgroundColor(Color.WHITE)
                        .setStartPosition(list.map { it.id }.indexOf(memeID))
                        .show()
            }
            memeViewHolder
        }
    }

    override fun getItemCount(): Int = items.size + if (loading) 1 else 0
    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        if (getItemViewType(position) != LOADING_TYPE) {
            holder.itemPosition = position
            holder.bind(items[position])
        }
    }

    override fun getItemViewType(position: Int): Int =
            if (position >= items.size) LOADING_TYPE else items[position].itemType


    abstract fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder

    open fun createLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

    fun make(): MyS = MyS()
    inner class MyS : SwipeController() {
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val v = viewHolder as MemeViewHolder
            return if (items.size > 0 && items[v.itemPosition] is Meme) {
                makeMovementFlags(0, /*ItemTouchHelper.LEFT or */ItemTouchHelper.RIGHT)
            } else {
                makeMovementFlags(0, 0)
            }
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = (viewHolder as MemeViewHolder).itemPosition
            val meme = items[pos] as Meme
            //val meme = items[pos] as Meme
            when (direction) {
                ItemTouchHelper.LEFT -> {
                    val s = DynamicLink.SocialMetaTagParameters.Builder()
                            .setTitle("MemeIt")
                            .setDescription("hello")
                            .setImageUrl(Uri.parse(getCloudinaryImageUrlforId(meme.imageId!!)))
                            .build()
                    val d = FirebaseDynamicLinks.getInstance()
                            .createDynamicLink()
                            .setLink(Uri.parse("${MemeItApp.SERVER_URL}meme/${meme.id}"))
                            .setSocialMetaTagParameters(s)
                            .buildDynamicLink()
                    ShareCompat.IntentBuilder.from(context as Activity)
                            .setText(d.uri.toString())
                            .startChooser()

                }
                ItemTouchHelper.RIGHT -> {



                }
            }
        }
    }
}