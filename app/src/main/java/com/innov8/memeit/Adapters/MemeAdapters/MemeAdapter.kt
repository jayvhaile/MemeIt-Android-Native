package com.innov8.memeit.Adapters.MemeAdapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memeit.Activities.MemeChooser
import com.innov8.memeit.Adapters.ELEListAdapter
import com.innov8.memeit.Adapters.MemeAdapters.ViewHolders.MemeViewHolder
import com.innov8.memeit.R
import com.innov8.memeit.Utils.showMemeZoomView
import com.memeit.backend.models.HomeElement
import com.memeit.backend.models.Meme

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
        const val LIST_ADAPTER: Byte = 0
        const val LIST_ADAPTER_USER_POSTS: Byte = 1
        const val LIST_ADAPTER_MY_POSTS: Byte = 2
        const val LIST_FAVORITE_ADAPTER: Byte = 3
        const val GRID_ADAPTER_MY_POSTS: Byte = 4
        const val GRID_ADAPTER_USER_POSTS: Byte = 5
        const val HOME_ADAPTER: Byte = 6
        fun create(type: Byte, context: Context): MemeAdapter = when (type) {
            LIST_ADAPTER -> MemeListAdapter(context)
            LIST_ADAPTER_USER_POSTS -> MemeListAdapter(context).apply {
                emptyDescription = "User has no uploads yet."
                showErrorAtTop = true
            }
            LIST_ADAPTER_MY_POSTS -> MemeListAdapter(context).apply {
                emptyDescription = "Your meme uploads would appear here"
                emptyActionText = "Tap here to create one"
                onEmptyAction = {
                    this.context.startActivity(Intent(this.context, MemeChooser::class.java))
                }
                showErrorAtTop = true
            }
            LIST_FAVORITE_ADAPTER -> MemeListAdapter(context).apply {
                emptyDrawableId = R.drawable.ic_favorite_black_24dp
                emptyDescription = "Favorite Collection is Empty"
            }
            GRID_ADAPTER_USER_POSTS -> GridMemeAdapter(context).apply {
                emptyDescription = "User has no uploads yet."
                showErrorAtTop = true
            }
            GRID_ADAPTER_MY_POSTS -> GridMemeAdapter(context).apply {
                emptyDescription = "Your meme uploads would appear here"
                emptyActionText = "Tap here to create one"
                onEmptyAction = {
                    this.context.startActivity(Intent(this.context, MemeChooser::class.java))
                }
                showErrorAtTop = true
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
            context.showMemeZoomView(list, memeID)
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