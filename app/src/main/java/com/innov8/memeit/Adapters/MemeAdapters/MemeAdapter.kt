package com.innov8.memeit.Adapters.MemeAdapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.innov8.memeit.Adapters.ELEListAdapter
import com.innov8.memeit.Adapters.MemeAdapters.ViewHolders.MemeViewHolder
import com.innov8.memeit.Adapters.SwipeController
import com.innov8.memeit.CustomClasses.LoadingDrawable
import com.innov8.memeit.MemeItApp
import com.innov8.memeit.R
import com.innov8.memeit.generateUrl
import com.innov8.memeit.getCloudinaryImageUrlForId
import com.memeit.backend.dataclasses.HomeElement
import com.memeit.backend.dataclasses.Meme
import com.stfalcon.frescoimageviewer.ImageViewer
import com.github.ybq.android.spinkit.style.CubeGrid

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


    override fun onCreateHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val memeViewHolder = createHolder(parent, viewType)
        memeViewHolder.memeClickedListener = { memeID ->
            val list: List<Meme> = items.filter { it is Meme }
                    .map { it as Meme }
                    .toList()
            val hierarchy = GenericDraweeHierarchyBuilder.newInstance(context.resources)
                    .setProgressBarImage(LoadingDrawable(context))

            ImageViewer.Builder<Meme>(context, list)
                    .setFormatter { it.generateUrl() }
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

    fun make(): MyS = MyS()
    inner class MyS : SwipeController() {
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            return if (items.size > 0 && viewHolder is MemeViewHolder && items[viewHolder.itemPosition] is Meme) {
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
                            .setImageUrl(Uri.parse(getCloudinaryImageUrlForId(meme.imageId!!)))
                            .build()
                    val ap = DynamicLink.AndroidParameters.Builder("com.innov8.memeit").build()

                    val ll = FirebaseDynamicLinks.getInstance()
                            .createDynamicLink()
                            .setDomainUriPrefix("memeit.page.link")
                            .setLink(Uri.parse("${MemeItApp.SERVER_URL}meme/${meme.id}"))
                            .setAndroidParameters(ap)
                            .setSocialMetaTagParameters(s)
                            .buildDynamicLink().uri

                    ShareCompat.IntentBuilder.from(context as Activity)
                            .setText(ll.toString())
                            .setStream(ll)
                            .setType("image/*")
                            .startChooser()
                    /*FirebaseDynamicLinks.getInstance()
                            .createDynamicLink()
                            .setLongLink(ll)
                            .buildShortDynamicLink().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    log("fuccck", it.result.previewLink)
                                    log("fuccck", it.result.shortLink)
                                    log("fuccck", it.result.warnings)
                                    ShareCompat.IntentBuilder.from(context as Activity)
                                            .setStream(it.result.shortLink)
                                            .startChooser()

                                }
                                else{
                                    log("fuccck :error",it.exception?.message?:"unknown")
                                }
                            }*/
                }
                ItemTouchHelper.RIGHT -> {


                }
            }
        }
    }
}