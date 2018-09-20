package com.innov8.memeit.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.cloudinary.android.MediaManager
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.view.SimpleDraweeView
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memegenerator.utils.fromDPToPX
import com.innov8.memegenerator.utils.toast
import com.innov8.memeit.*
import com.innov8.memeit.Activities.CommentsActivity
import com.innov8.memeit.Activities.ProfileActivity
import com.innov8.memeit.Activities.ReactorListActivity
import com.innov8.memeit.Adapters.ListMemeAdapter.Companion.activeRID
import com.innov8.memeit.CustomClasses.LoadingDrawable
import com.innov8.memeit.CustomViews.ProfileDraweeView
import com.memeit.backend.MemeItMemes
import com.memeit.backend.MemeItUsers
import com.memeit.backend.dataclasses.*
import com.memeit.backend.utilis.OnCompleteListener
import com.stfalcon.frescoimageviewer.ImageViewer
import com.varunest.sparkbutton.SparkButton
import okhttp3.ResponseBody

abstract class MemeAdapter(val context: Context) : RecyclerView.Adapter<MemeViewHolder>() {
    companion object {
        const val LIST_ADAPTER: Byte = 1
        const val GRID_ADAPTER: Byte = 2
        const val HOME_ADAPTER: Byte = 3
        fun create(type: Byte, context: Context): MemeAdapter = when (type) {
            LIST_ADAPTER -> ListMemeAdapter(context)
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
    val screenWidth = context.resources.displayMetrics.widthPixels
    val screenHeight = context.resources.displayMetrics.heightPixels
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
                                    .source(it.memeImageUrl)
                                    .generate()
                        }
                        .setCustomDraweeHierarchyBuilder(hierarchy)
                        .setBackgroundColor(Color.WHITE)
                        .setStartPosition(list.map { it.memeId }.indexOf(memeID))
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
            return if (items[v.itemPosition] is Meme) {
                makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            } else {
                makeMovementFlags(0, 0)
            }
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = (viewHolder as MemeViewHolder).itemPosition
            val meme = items[pos] as Meme
            when (direction) {
                ItemTouchHelper.LEFT -> {
                    context.toast("left")
                }
                ItemTouchHelper.RIGHT -> {
                    context.toast("right")
                }
            }
        }
    }
}

class ListMemeAdapter(context: Context) : MemeAdapter(context) {
    companion object {
        val activeRID = intArrayOf(R.drawable.laughing, R.drawable.rofl, R.drawable.neutral, R.drawable.angry)
    }

    override fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        if (viewType != MEME_TYPE)
            throw IllegalStateException("View Type must only be MEME_TYPE in ListMemeAdapter")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item_meme, parent, false)
        return MemeListViewHolder(view, this)
    }
}

class GridMemeAdapter(context: Context) : MemeAdapter(context) {
    companion object {
        const val GRID_SPAN_COUNT = 3
    }

    override fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        if (viewType != MEME_TYPE)
            throw IllegalStateException("View Type must only be MEME_TYPE in GridMemeAdapter")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item_meme_grid, parent, false)
        return MemeGridViewHolder(view, this)
    }

    override fun createLayoutManager(): RecyclerView.LayoutManager = GridLayoutManager(context, GRID_SPAN_COUNT, RecyclerView.VERTICAL, false)


}

class HomeMemeAdapter(context: Context) : MemeAdapter(context) {
    val usersPool = RecyclerView.RecycledViewPool()
    val tagsPool = RecyclerView.RecycledViewPool()
    val temlplatesPool = RecyclerView.RecycledViewPool()
    override fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        when (viewType) {
            MEME_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.list_item_meme, parent, false)
                return MemeListViewHolder(view, this)
            }
            USER_SUGGESTION_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val v = inflater.inflate(R.layout.list_item_list, parent, false)
                return UserSuggestionHolder(v, this)
            }
            TAG_SUGGESTION_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val v = inflater.inflate(R.layout.list_item_list, parent, false)
                return TagSuggestionHolder(v, this)
            }
            MEME_TEMPLATE_SUGGESTION_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val v = inflater.inflate(R.layout.list_item_list, parent, false)
                return MemeTemplateSuggestionHolder(v, this)
            }
            AD_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val v = inflater.inflate(R.layout.list_item_ad, parent, false)
                return AdHolder(v, this)
            }
            else -> {
                throw IllegalArgumentException("ViewType must be one of the four")
            }
        }
    }
}


abstract class MemeViewHolder(itemView: View, val memeAdapter: MemeAdapter) : RecyclerView.ViewHolder(itemView) {
    var itemPosition = 0
    var memeClickedListener: ((String) -> Unit)? = null
    abstract fun bind(homeElement: HomeElement)
}


class MemeListViewHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    private val posterPicV: ProfileDraweeView = itemView.findViewById(R.id.notif_icon)
    private val memeImageV: SimpleDraweeView = itemView.findViewById(R.id.meme_image)
    private val commentBtnV: ImageButton = itemView.findViewById(R.id.meme_comment)
    private val posterNameV: TextView = itemView.findViewById(R.id.meme_poster_name)
    private val memeDateV: TextView = itemView.findViewById(R.id.meme_time)
    private val reactionCountV: TextView = itemView.findViewById(R.id.meme_reactions)
    private val commentCountV: TextView = itemView.findViewById(R.id.meme_comment_count)
    private val memeMenu: ImageButton = itemView.findViewById(R.id.meme_options)
    private val reactButton: SparkButton = itemView.findViewById(R.id.react_button)
    private val favButton: SparkButton = itemView.findViewById(R.id.fav_button)
    private val reactGroup: Group = itemView.findViewById(R.id.react_group)
    private val memeTags: TextView = itemView.findViewById(R.id.meme_tags)


    init {
//        memeImageV.setOnClickListener { memeClickedListener?.invoke(getCurrentMeme().memeId) }
        commentBtnV.setOnClickListener {
            val meme = getCurrentMeme()
            val intent = Intent(memeAdapter.context, CommentsActivity::class.java)
            intent.putExtra(CommentsActivity.MEME_PARAM_KEY, meme)
            memeAdapter.context.startActivity(intent)
        }
        posterPicV.setOnClickListener {
            val i = Intent(memeAdapter.context, ProfileActivity::class.java)
            i.putExtra("user", getCurrentMeme().poster.toUser())
            memeAdapter.context.startActivity(i)
        }
        reactionCountV.setOnClickListener {
            val i = Intent(memeAdapter.context, ReactorListActivity::class.java)
            i.putExtra("mid", getCurrentMeme().memeId)
            memeAdapter.context.startActivity(i)
        }

        val reactListener = OnClickListener { view ->
            var reactionType: Reaction.ReactionType? = null
            var reactRes = 0
            when (view.id) {
                R.id.react_funny -> {
                    reactionType = Reaction.ReactionType.FUNNY
                    reactRes = activeRID[0]
                }
                R.id.react_veryfunny -> {
                    reactionType = Reaction.ReactionType.VERY_FUNNY
                    reactRes = activeRID[1]
                }
                R.id.react_stupid -> {
                    reactionType = Reaction.ReactionType.STUPID
                    reactRes = activeRID[2]
                }
                R.id.react_angry -> {
                    reactionType = Reaction.ReactionType.ANGERING
                    reactRes = activeRID[3]
                }
            }
            react(reactionType, reactRes)
            toggleReactVisibility()
        }
        itemView.findViewById<View>(R.id.react_funny).setOnClickListener(reactListener)
        itemView.findViewById<View>(R.id.react_veryfunny).setOnClickListener(reactListener)
        itemView.findViewById<View>(R.id.react_stupid).setOnClickListener(reactListener)
        itemView.findViewById<View>(R.id.react_angry).setOnClickListener(reactListener)
        /*reactButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext, "long", Toast.LENGTH_SHORT).show();
                toggleReactVisibility();
                return true;
            }
        });*/
        reactButton.setOnClickListener {
            toggleReactVisibility()

        }
        favButton.isChecked = false
        favButton.setOnClickListener {
            val meme = getCurrentMeme()
            if (meme.isMyFavourite)
                MemeItMemes.getInstance().removeFromFavourites(meme.memeId, object : OnCompleteListener<ResponseBody> {
                    override fun onSuccess(responseBody: ResponseBody) {
                        meme.isMyFavourite = false
                        favButton.isChecked = false
                    }

                    override fun onFailure(error: OnCompleteListener.Error) {
                        Toast.makeText(this@MemeListViewHolder.memeAdapter.context, "favourite failed\n" + error.message, Toast.LENGTH_SHORT).show()
                    }
                })
            else
                MemeItMemes.getInstance().addToFavourites(meme.memeId, object : OnCompleteListener<ResponseBody> {
                    override fun onSuccess(responseBody: ResponseBody) {
                        meme.isMyFavourite = true
                        favButton.playAnimation()
                        favButton.isChecked = true
                    }

                    override fun onFailure(error: OnCompleteListener.Error) {
                        Toast.makeText(this@MemeListViewHolder.memeAdapter.context, "favourite failed\n" + error.message, Toast.LENGTH_SHORT).show()
                    }
                })

        }
        memeMenu.setOnClickListener { showMemeMenu() }
        memeTags.setOnClickListener { showFollowDialog() }

    }

    private fun toggleReactVisibility() {
        reactGroup.visibility=if (reactGroup.visibility == VISIBLE) GONE else VISIBLE
    }

    private fun showFollowDialog() {
        val meme = getCurrentMeme()
        MaterialDialog.Builder(memeAdapter.context)
                .title("Choose Tags to follow")
                .items(meme.tags.map { "#$it" })
                .itemsCallbackMultiChoice(null) { _, _, _ ->
                    true
                }
                .positiveText("Follow")
                .negativeText("Cancel")
                .onPositive { dialog, _ ->
                    val si = dialog.selectedIndices
                    val selectedTags = meme.tags.filterIndexed { index, _ ->
                        si?.contains(index) ?: false
                    }.toTypedArray()
                    log(selectedTags)
                    MemeItUsers.getInstance().followTags(selectedTags, null)
                }
                .show()
    }

    private fun react(reactionType: Reaction.ReactionType?, finalReactRes: Int) {
        MemeItMemes.getInstance().reactToMeme(Reaction.create(reactionType, getCurrentMeme().memeId), object : OnCompleteListener<ResponseBody> {
            override fun onSuccess(responseBody: ResponseBody) {
                getCurrentMeme().myReaction = Reaction.create(reactionType, null)
                reactButton.setActiveImage(finalReactRes)
                reactButton.playAnimation()
                reactButton.isChecked = true
            }

            override fun onFailure(error: OnCompleteListener.Error) {
                Toast.makeText(this@MemeListViewHolder.memeAdapter.context, "reaction failed\n" + error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun bind(homeElement: HomeElement) {
        val meme = homeElement as Meme

        reactGroup.visibility = GONE
        posterNameV.text = meme.poster.name
        posterPicV.text = meme.poster.name.prefix()
        reactionCountV.text = String.format("%d people reacted", meme.reactionCount)
        commentCountV.text = meme.commentCount.toString()
        posterPicV.loadImage(meme.poster.profileUrl)
        memeDateV.text = meme.date.formateAsDate()
        if (meme.tags.isEmpty()) {
            memeTags.visibility = GONE
        } else {
            memeTags.visibility = VISIBLE
            memeTags.text = meme.tags.joinToString(", ") { "#$it" }
        }
        if (meme.myReaction !== null) {
            reactButton.setActiveImage(activeRID[meme.myReaction.type.ordinal])
            reactButton.isChecked = true
        } else {
            reactButton.isChecked = false
        }
        favButton.isChecked = meme.isMyFavourite
        val w = memeAdapter.screenWidth
        var h = (w / meme.memeImageRatio).toInt()

        val maxHeight = memeAdapter.screenHeight - 200.fromDPToPX(memeAdapter.context)
        val minHeight = 200.fromDPToPX(memeAdapter.context)
        h = if (h < minHeight) minHeight else if (h > maxHeight) maxHeight else h
        memeImageV.layoutParams.width = w
        memeImageV.layoutParams.height = h
        memeImageV.requestLayout()
        memeImageV.load(meme.memeImageUrl, w, h, meme.type)
        log("type", meme.type.toString())
    }

    private fun showMemeMenu() {
        val menu = PopupMenu(memeAdapter.context, memeMenu)
        if (MemeItUsers.getInstance().getMyUser(memeAdapter.context).userID.equals(getCurrentMeme().poster.id))
            menu.menuInflater.inflate(R.menu.meme_menu, menu.menu)
        else
            menu.menuInflater.inflate(R.menu.meme_menu_not_own, menu.menu)

        menu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_delete_meme -> {
                    MemeItMemes.getInstance().deleteMeme(getCurrentMeme().memeId, object : OnCompleteListener<ResponseBody> {
                        override fun onSuccess(t: ResponseBody?) {
                            memeAdapter.remove(Meme.forID(getCurrentMeme().memeId))
                            //todo show snackbar instead of toast
                            memeAdapter.context.toast("Meme Deleted")
                        }

                        override fun onFailure(error: OnCompleteListener.Error?) {
                            //todo show snackbar instead of toast
                            memeAdapter.context.toast("Cannot Delete Meme ${error?.message}")
                        }
                    })
                    return@OnMenuItemClickListener true
                }
                R.id.menu_report_meme -> {
                    var reportCondidions: Array<String> = arrayOf("Pornography", "Abuse", "Violence", "Not appropriate", "Other")
                    MaterialDialog.Builder(memeAdapter.context)
                            .title("Report")
                            .items("Pornography", "Abuse", "Violence", "Not appropriate", "Other")
                            .itemsCallbackMultiChoice(null) { _, _, _ ->
                                true
                            }
                            .positiveText("Report")
                            .negativeText("Cancel")
                            .onPositive { dialog, _ ->
                                //todo jv finish this up this shit giving me a hard time
                            }
                            .show()

                }
            }
            false
        })
        menu.show()
    }

    private fun getCurrentMeme(): Meme {
        return memeAdapter.items[itemPosition] as Meme
    }
}

class MemeGridViewHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    private val memeImageV: SimpleDraweeView = itemView.findViewById(R.id.meme_image)
    val width = memeAdapter.screenWidth / GridMemeAdapter.GRID_SPAN_COUNT

    init {
        val lp = FrameLayout.LayoutParams(width, width)
        memeImageV.layoutParams = lp
        memeImageV.setOnClickListener { memeClickedListener?.invoke(getCurrentMeme().memeId) }
        memeImageV.hierarchy.setProgressBarImage(LoadingDrawable(memeAdapter.context))
    }

    override fun bind(homeElement: HomeElement) {
        val meme = homeElement as Meme
        memeImageV.load(meme.memeImageUrl,width,width,meme.type)
    }

    private fun getCurrentMeme(): Meme {
        return memeAdapter.items[itemPosition] as Meme
    }
}

class UserSuggestionHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    val list: RecyclerView = itemView.findViewById(R.id.list_recyc)
    val title: TextView = itemView.findViewById(R.id.list_title)
    private val adapter: UserSugAdapter = UserSugAdapter(memeAdapter.context)

    init {
        list.makeLinear(RecyclerView.HORIZONTAL)
        list.adapter = adapter
        title.text = "User Suggestions"
        memeAdapter as HomeMemeAdapter

        list.setRecycledViewPool(memeAdapter.usersPool)
    }

    override fun bind(homeElement: HomeElement) {
        val a = homeElement as UserSuggestion
        adapter.setAll(a.users)
    }
}

class TagSuggestionHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    val list: RecyclerView = itemView.findViewById(R.id.list_recyc)
    val title: TextView = itemView.findViewById(R.id.list_title)
    private val adapter: TagsAdapter = TagsAdapter(memeAdapter.context)

    init {
        list.makeLinear(RecyclerView.HORIZONTAL)
        list.adapter = adapter
        title.text = "Recommended Tags"
        memeAdapter as HomeMemeAdapter
        list.setRecycledViewPool(memeAdapter.tagsPool)
    }

    override fun bind(homeElement: HomeElement) {
        val a = homeElement as TagSuggestion
        adapter.setAll(a.tags)
    }
}

class MemeTemplateSuggestionHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    val list: RecyclerView = itemView.findViewById(R.id.list_recyc)
    val title: TextView = itemView.findViewById(R.id.list_title)

    private val adapter: TemplateSugAdapter = TemplateSugAdapter(memeAdapter.context)

    init {
        list.makeLinear(RecyclerView.HORIZONTAL)
        list.adapter = adapter
        title.text = "Meme Templates to Edit"
        memeAdapter as HomeMemeAdapter
        list.setRecycledViewPool(memeAdapter.temlplatesPool)

    }

    override fun bind(homeElement: HomeElement) {
        val a = homeElement as MemeTemplateSuggestion
        adapter.setAll(a.templates)
    }

}

class AdHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    override fun bind(homeElement: HomeElement) {
//        val ad = homeElement as AdElement
    }
}