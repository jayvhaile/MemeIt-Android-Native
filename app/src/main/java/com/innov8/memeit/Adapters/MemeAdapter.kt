package com.innov8.memeit.Adapters

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.innov8.memegenerator.loading_button_lib.customViews.CircularProgressButton
import com.innov8.memegenerator.utils.toast
import com.innov8.memeit.Activities.CommentsActivity
import com.innov8.memeit.Activities.ProfileActivity
import com.innov8.memeit.CustomClasses.CustomMethods
import com.innov8.memeit.CustomClasses.ImageUtils
import com.innov8.memeit.R
import com.memeit.backend.MemeItMemes
import com.memeit.backend.MemeItUsers
import com.memeit.backend.dataclasses.*
import com.memeit.backend.utilis.OnCompleteListener
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
            field = value
            if (value)
                notifyItemInserted(items.size)
            else
                notifyItemRemoved(items.size)
        }
    val screenWidth = context.resources.displayMetrics.widthPixels
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
            object : MemeViewHolder(view, this) {
                override fun bind(homeElement: HomeElement) {
                    //do nothing here
                }
            }
        } else
            createHolder(parent, viewType)
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
    override fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        if (viewType != MEME_TYPE)
            throw IllegalStateException("View Type must only be MEME_TYPE in ListMemeAdapter")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item_meme, parent, false)
        return MemeListViewHolder(view, this, screenWidth)
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
        return MemeGridViewHolder(view, this, screenWidth)
    }

    override fun createLayoutManager(): RecyclerView.LayoutManager = GridLayoutManager(context, GRID_SPAN_COUNT, RecyclerView.VERTICAL, false)


}

class HomeMemeAdapter(context: Context) : MemeAdapter(context) {
    override fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        when (viewType) {
            MEME_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.list_item_meme, parent, false)
                return MemeListViewHolder(view, this, screenWidth)
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
    abstract fun bind(homeElement: HomeElement)
}

class MemeListViewHolder(itemView: View, memeAdapter: MemeAdapter, val screen_width: Int) : MemeViewHolder(itemView, memeAdapter) {
    private val posterPicV: SimpleDraweeView = itemView.findViewById(R.id.follower_poster_pp)
    private val memeImageV: SimpleDraweeView = itemView.findViewById(R.id.meme_image)
    private val commentBtnV: ImageButton = itemView.findViewById(R.id.meme_comment)
    private val posterNameV: TextView = itemView.findViewById(R.id.meme_poster_name)
    private val reactionCountV: TextView = itemView.findViewById(R.id.meme_reactions)
    private val commentCountV: TextView = itemView.findViewById(R.id.meme_comment_count)
    private val memeMenu: ImageButton = itemView.findViewById(R.id.meme_options)
    private val reactButton: SparkButton = itemView.findViewById(R.id.react_button)
    private val favButton: SparkButton = itemView.findViewById(R.id.fav_button)
    private val reactGroup: Group = itemView.findViewById(R.id.react_group)
    private lateinit var memeId: String

    init {
        commentBtnV.setOnClickListener {
            val meme = memeAdapter.getMemeByID(memeId)
                    ?: throw IllegalStateException("Meme Should not be null")
            val intent = Intent(memeAdapter.context, CommentsActivity::class.java)
            intent.putExtra(CommentsActivity.MEME_PARAM_KEY, meme)
            memeAdapter.context.startActivity(intent)
        }
        posterPicV.setOnClickListener {
            val i = Intent(memeAdapter.context, ProfileActivity::class.java)
            i.putExtra("uid", memeAdapter.getMemeByID(memeId)?.poster?.id
                    ?: throw IllegalStateException("Meme Should not be null"))
            memeAdapter.context.startActivity(i)
        }

        val reactListener = View.OnClickListener { view ->
            var reactionType: Reaction.ReactionType? = null
            var reactRes = 0
            when (view.id) {
                R.id.react_funny -> {
                    reactionType = Reaction.ReactionType.FUNNY
                    reactRes = R.mipmap.laughing
                }
                R.id.react_veryfunny -> {
                    reactionType = Reaction.ReactionType.VERY_FUNNY
                    reactRes = R.mipmap.rofl
                }
                R.id.react_stupid -> {
                    reactionType = Reaction.ReactionType.STUPID
                    reactRes = R.mipmap.neutral
                }
                R.id.react_angry -> {
                    reactionType = Reaction.ReactionType.ANGERING
                    reactRes = R.mipmap.angry
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
            MemeItMemes.getInstance().addToFavourites(meme.memeId, object : OnCompleteListener<ResponseBody> {
                override fun onSuccess(responseBody: ResponseBody) {
                    favButton.playAnimation()
                    favButton.isChecked = true
                }

                override fun onFailure(error: OnCompleteListener.Error) {
                    Toast.makeText(this@MemeListViewHolder.memeAdapter.context, "favourite failed\n" + error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
        memeMenu.setOnClickListener { showMemeMenu() }
    }

    private fun react(reactionType: Reaction.ReactionType?, finalReactRes: Int) {
        MemeItMemes.getInstance().reactToMeme(Reaction.create(reactionType, memeId), object : OnCompleteListener<ResponseBody> {
            override fun onSuccess(responseBody: ResponseBody) {
                reactButton.setActiveImage(finalReactRes)
                reactButton.playAnimation()
            }

            override fun onFailure(error: OnCompleteListener.Error) {
                Log.w("react", error.message)
                Toast.makeText(this@MemeListViewHolder.memeAdapter.context, "reaction failed\n" + error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun toggleReactVisibility() {
        val v = if (reactGroup.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        reactGroup.visibility = v
    }

    override fun bind(homeElement: HomeElement) {
        val meme = homeElement as Meme
        reactGroup.visibility = View.GONE
        memeId = meme.memeId
        posterNameV.text = meme.poster.name
        reactionCountV.text = String.format("%d people reacted", meme.reactionCount)
        commentCountV.text = meme.commentCount.toString()
        ImageUtils.loadImageFromCloudinaryTo(posterPicV, meme.poster.profileUrl)
        adjust(meme.memeImageRatio)
        ImageUtils.loadImageFromCloudinaryTo(memeImageV, meme.memeImageUrl)
    }


    private fun adjust(ratio: Double) {
        val width = screen_width
        var height = (width / ratio).toInt()
        val max_height = CustomMethods.convertDPtoPX(memeAdapter.context, 500.0f).toInt()
        val min_height = CustomMethods.convertDPtoPX(memeAdapter.context, 200.0f).toInt()
        height = if (height < min_height) min_height else if (height > max_height) max_height else height
        memeImageV.layoutParams.width = width
        memeImageV.layoutParams.height = height
        memeImageV.requestLayout()
    }

    private fun showMemeMenu() {
        val menu = PopupMenu(memeAdapter.context, memeMenu)
        menu.menuInflater.inflate(R.menu.meme_menu, menu.menu)
        menu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_delete_meme ->
                    /*MemeItMemes.getInstance().deleteMeme(memeId, new OnCompleteListener<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        remove(Meme.forID(memeId));
                        //todo show snackbar instead of toast
                        Toast.makeText(mContext, "Meme Deleted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Error error) {
                        //todo show snackbar instead of toast
                        Toast.makeText(mContext, "Cannot Delete Meme " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });*/
                    return@OnMenuItemClickListener true
            }
            false
        })
        menu.show()
    }

    private fun getCurrentMeme(): Meme {
        return memeAdapter.items[itemPosition] as Meme
    }
}

class MemeGridViewHolder(itemView: View, memeAdapter: MemeAdapter, screen_width: Int) : MemeViewHolder(itemView, memeAdapter) {
    private val memeImageV: SimpleDraweeView = itemView.findViewById(R.id.meme_image)

    init {
        val width = screen_width / GridMemeAdapter.GRID_SPAN_COUNT
        val lp = FrameLayout.LayoutParams(width, width)
        memeImageV.layoutParams = lp
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

            val action = {
                MemeItUsers.getInstance().followUser(user.userID, object : OnCompleteListener<ResponseBody> {
                    override fun onSuccess(t: ResponseBody?) {
                        buttons[i].revertAnimation {
                            images[i].visibility = View.GONE
                            texts[i].visibility = View.GONE
                            buttons[i].visibility = View.GONE
                            memeAdapter.context.toast("Now Following User")
                        }
                    }

                    override fun onFailure(error: OnCompleteListener.Error?) {
                        buttons[i].revertAnimation {
                            memeAdapter.context.toast("Failed To Follow user " + error?.message)
                        }
                    }
                })
            }
            //todo remove this (this is just to see it loading)
            Handler().postDelayed(action, 5000)
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
        btns.forEachIndexed { i, btn ->
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
        val ad = homeElement as AdElement
    }
}