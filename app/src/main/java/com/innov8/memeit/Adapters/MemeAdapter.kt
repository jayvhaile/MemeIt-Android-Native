package com.innov8.memeit.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.cloudinary.android.MediaManager
import com.facebook.drawee.view.SimpleDraweeView
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memegenerator.loading_button_lib.customViews.CircularProgressButton
import com.innov8.memegenerator.utils.fromDPToPX
import com.innov8.memegenerator.utils.log
import com.innov8.memegenerator.utils.toast
import com.innov8.memeit.Activities.CommentsActivity
import com.innov8.memeit.Activities.ProfileActivity
import com.innov8.memeit.Adapters.ListMemeAdapter.Companion.activeRID
import com.innov8.memeit.CustomClasses.CustomMethods
import com.innov8.memeit.CustomClasses.ImageUtils
import com.innov8.memeit.CustomClasses.LoadingDrawable
import com.innov8.memeit.R
import com.innov8.memeit.launchTask
import com.innov8.memeit.runAsync
import com.memeit.backend.MemeItClient
import com.memeit.backend.dataclasses.*
import com.stfalcon.frescoimageviewer.ImageViewer
import com.varunest.sparkbutton.SparkButton
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

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
            field = value
            if (value)
                notifyItemInserted(items.size)
            else
                notifyItemRemoved(items.size)
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
            val progress=view.findViewById<ProgressBar>(R.id.meme_loading)
            val d=CubeGrid()
            d.color= Color.rgb(255,100,0)
            progress.indeterminateDrawable=d
            object : MemeViewHolder(view, this) {
                override fun bind(homeElement: HomeElement) {
                    //do nothing here
                }
            }
        } else {
            val memeViewHolder = createHolder(parent, viewType)
            memeViewHolder.memeClickedListener = { it ->
                val list: List<Meme> = items.filter { it is Meme }
                        .map { it as Meme }
                        .toList()
                ImageViewer.Builder<Meme>(context, list)
                        .setFormatter {
                            MediaManager.get()
                                    .url()
                                    .source(it.memeImageUrl)
                                    .generate()
                        }.setStartPosition(it)
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

    fun getMemeByID(mid: String): Meme? = items.find { it is Meme && it.memeId == mid } as Meme?
    open fun createLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)


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
    override fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        when (viewType) {
            MEME_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.list_item_meme, parent, false)
                return MemeListViewHolder(view, this)
            }
            USER_SUGGESTION_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val v = inflater.inflate(R.layout.list_item_user_suggestion, parent, false)
                return UserSuggestionHolder(v, this)
            }
            MEME_TEMPLATE_SUGGESTION_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val v = inflater.inflate(R.layout.list_item_meme_template_suggestion, parent, false)
                return MemeTemplateSuggestionHolder(v, this)
            }
            AD_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val v = inflater.inflate(R.layout.list_item_user_suggestion, parent, false)
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
    var memeClickedListener: ((Int) -> Unit)? = null
    val memeInterface = MemeItClient.getInstance().`interface`
    abstract fun bind(homeElement: HomeElement)
}

class MemeListViewHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    private val posterPicV: SimpleDraweeView = itemView.findViewById(R.id.follower_poster_pp)
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
        memeImageV.hierarchy.setProgressBarImage(LoadingDrawable())
        memeImageV.setOnClickListener { memeClickedListener?.invoke(itemPosition) }
        commentBtnV.setOnClickListener {
            val meme = getCurrentMeme()
            val intent = Intent(memeAdapter.context, CommentsActivity::class.java)
            intent.putExtra(CommentsActivity.MEME_PARAM_KEY, meme)
            memeAdapter.context.startActivity(intent)
        }
        posterPicV.setOnClickListener {
            val i = Intent(memeAdapter.context, ProfileActivity::class.java)
            i.putExtra("uid", getCurrentMeme().poster.id)
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
        reactButton.setOnClickListener { toggleReactVisibility() }
        favButton.isChecked = false
        favButton.setOnClickListener {
            val meme = getCurrentMeme()
            launchTask{
                val result = runAsync {
                    if (meme.isMyFavourite)
                        memeInterface.removeMemeFromFavourite(meme.memeId).execute()
                    else
                        memeInterface.addMemeToFavourite(meme.memeId).execute()
                }
                if (result.isSuccessful) {
                    meme.isMyFavourite = false
                    favButton.isChecked = false
                } else
                    memeAdapter.context.toast("Failed favourite ${result.message()}")
            }

        }
        memeMenu.setOnClickListener { showMemeMenu() }
        memeTags.movementMethod = LinkMovementMethod.getInstance()
        memeTags.setOnClickListener { showFollowDialog() }

    }

    private fun showFollowDialog() {
        val meme = getCurrentMeme()
        MaterialDialog.Builder(memeAdapter.context)
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
                    launch{ runAsync { memeInterface.followTags(selectedTags) } }
                }
                .show()
    }

    private fun react(reactionType: Reaction.ReactionType?, finalReactRes: Int) {

        launchTask{
            val result = runAsync{memeInterface.reactToMeme(Reaction.create(reactionType, getCurrentMeme().memeId)).execute()}
            if(result.isSuccessful){
                getCurrentMeme().myReaction = Reaction.create(reactionType, null)
                reactButton.setActiveImage(finalReactRes)
                reactButton.playAnimation()
                reactButton.isChecked = true
            }else
                memeAdapter.context.toast("Reation Failed\n${result.message()}")

        }
    }

    private fun toggleReactVisibility() {
        val v = if (reactGroup.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        reactGroup.visibility = v
    }

    override fun bind(homeElement: HomeElement) {
        val meme = homeElement as Meme
        adjust(meme.memeImageRatio)
        reactGroup.visibility = View.GONE
        posterNameV.text = meme.poster.name
        reactionCountV.text = String.format("%d people reacted", meme.reactionCount)
        commentCountV.text = meme.commentCount.toString()
        ImageUtils.loadImageFromCloudinaryTo(posterPicV, meme.poster.profileUrl)

        memeDateV.text = CustomMethods.convertDate(meme.date)
        if (meme.tags.isEmpty()) {
            memeTags.visibility = View.GONE
        } else {
            memeTags.visibility = View.VISIBLE
            memeTags.text = meme.tags.joinToString(", ") { "#$it" }
        }
        if (meme.myReaction !== null) {
            reactButton.setActiveImage(activeRID[meme.myReaction.type.ordinal])
            reactButton.isChecked = true
        } else {
            reactButton.isChecked = false
        }
        favButton.isChecked = meme.isMyFavourite
        ImageUtils.loadImageFromCloudinaryTo(memeImageV, meme.memeImageUrl)
    }


    private fun adjust(ratio: Double) {
        val width = memeAdapter.screenWidth
        var height = (width / ratio).toInt()

        val maxHeight = memeAdapter.screenHeight - 200.fromDPToPX(memeAdapter.context)
        val minHeight = 200.fromDPToPX(memeAdapter.context)
        height = if (height < minHeight) minHeight else if (height > maxHeight) maxHeight else height
        memeImageV.layoutParams.width = width
        memeImageV.layoutParams.height = height
        memeImageV.requestLayout()
    }

    private fun showMemeMenu() {
        val menu = PopupMenu(memeAdapter.context, memeMenu)
        menu.menuInflater.inflate(R.menu.meme_menu, menu.menu)
        menu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_delete_meme -> {
                    launchTask{
                        val result= async{memeInterface.deleteMeme(getCurrentMeme().memeId).execute()}.await()
                        if(result.isSuccessful){
                            memeAdapter.remove(Meme.forID(getCurrentMeme().memeId))
                            memeAdapter.context.toast("Meme Deleted") //todo show snackbar instead of toast
                        }
                    }
                    return@OnMenuItemClickListener true
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

    init {
        val width = memeAdapter.screenWidth / GridMemeAdapter.GRID_SPAN_COUNT
        val lp = FrameLayout.LayoutParams(width, width)
        memeImageV.layoutParams = lp
        memeImageV.setOnClickListener { memeClickedListener?.invoke(itemPosition) }
    }

    override fun bind(homeElement: HomeElement) {
        val meme = homeElement as Meme
        ImageUtils.loadImageFromCloudinaryTo(memeImageV, meme.memeImageUrl)
    }
}

class UserSuggestionHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    private val images: List<SimpleDraweeView> = listOf(
            itemView.findViewById(R.id.suggestion_pp1),
            itemView.findViewById(R.id.suggestion_pp2),
            itemView.findViewById(R.id.suggestion_pp3),
            itemView.findViewById(R.id.suggestion_pp4)
    )
    private val texts: List<TextView> = listOf(
            itemView.findViewById(R.id.suggestion_name1),
            itemView.findViewById(R.id.suggestion_name2),
            itemView.findViewById(R.id.suggestion_name3),
            itemView.findViewById(R.id.suggestion_name4)
    )
    private val buttons: List<CircularProgressButton> = listOf(
            itemView.findViewById(R.id.suggestion_follow_btn1),
            itemView.findViewById(R.id.suggestion_follow_btn2),
            itemView.findViewById(R.id.suggestion_follow_btn3),
            itemView.findViewById(R.id.suggestion_follow_btn4)
    )


    init {
        val listener: (View) -> Unit = {
            val i = it.tag?.toString()?.toInt() ?: 0
            val user = (memeAdapter.items[itemPosition] as UserSuggestion).users[i]
            buttons[i].startAnimation()
            launchTask {
                val result = runAsync { memeInterface.followUser(user.userID).execute() }
                if (result.isSuccessful)
                    buttons[i].revertAnimation {
                        images[i].visibility = View.GONE
                        texts[i].visibility = View.GONE
                        buttons[i].visibility = View.GONE
                        memeAdapter.context.toast("Now Following User")
                    }
                else
                    buttons[i].revertAnimation {
                        memeAdapter.context.toast("Failed To Follow user " + result.message())
                    }
            }

        }
        buttons.forEachIndexed { i, btn ->
            btn.setOnClickListener {
                it.tag = i
                it.setOnClickListener(listener)
            }
        }
    }

    override fun bind(homeElement: HomeElement) {
        val userSuggestion = homeElement as UserSuggestion
        for (i in 0 until 4) {
            if (i < userSuggestion.users.size) {
                images[i].visibility = View.VISIBLE
                texts[i].visibility = View.VISIBLE
                buttons[i].visibility = View.VISIBLE
                images[i].setImageURI(userSuggestion.users[i].imageUrl)//todo replace with loadimageto
                texts[i].text = userSuggestion.users[i].name
            } else {
                images[i].visibility = View.GONE
                texts[i].visibility = View.GONE
                buttons[i].visibility = View.GONE
            }
        }
    }
}

class MemeTemplateSuggestionHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    private val imgs: List<SimpleDraweeView> = listOf(
            itemView.findViewById(R.id.suggestion_pp1),
            itemView.findViewById(R.id.suggestion_pp2),
            itemView.findViewById(R.id.suggestion_pp3),
            itemView.findViewById(R.id.suggestion_pp4)
    )
    private val btns: List<CircularProgressButton> = listOf(
            itemView.findViewById(R.id.suggestion_follow_btn1),
            itemView.findViewById(R.id.suggestion_follow_btn2),
            itemView.findViewById(R.id.suggestion_follow_btn3),
            itemView.findViewById(R.id.suggestion_follow_btn4)
    )

    init {
        btns.forEachIndexed { _, btn ->
            btn.setOnClickListener {

            }
        }
    }

    override fun bind(homeElement: HomeElement) {
        val templateSuggestion = homeElement as MemeTemplateSuggestion
        for (i in 0 until 4) {
            if (i < templateSuggestion.templates.size) {
                imgs[i].visibility = View.VISIBLE
                btns[i].visibility = View.VISIBLE
                imgs[i].setImageURI(templateSuggestion.templates[i])
            } else {
                imgs[i].visibility = View.GONE
                btns[i].visibility = View.GONE
            }
        }
    }
}

class AdHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    override fun bind(homeElement: HomeElement) {
//        val ad = homeElement as AdElement
    }
}