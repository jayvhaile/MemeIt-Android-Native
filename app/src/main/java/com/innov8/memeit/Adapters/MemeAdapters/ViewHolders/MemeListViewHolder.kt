package com.innov8.memeit.Adapters.MemeAdapters.ViewHolders

import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.Group
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.drawee.view.SimpleDraweeView
import com.innov8.memegenerator.utils.toast
import com.innov8.memeit.*
import com.innov8.memeit.Activities.CommentsActivity
import com.innov8.memeit.Activities.ProfileActivity
import com.innov8.memeit.Activities.ReactorListActivity
import com.innov8.memeit.Adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.Adapters.MemeAdapters.MemeListAdapter
import com.innov8.memeit.CustomViews.ProfileDraweeView
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItMemes
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.dataclasses.HomeElement
import com.memeit.backend.dataclasses.Meme
import com.memeit.backend.dataclasses.Reaction
import com.memeit.backend.dataclasses.Reaction.ReactionType.*
import com.varunest.sparkbutton.SparkButton
import kotlin.IllegalStateException

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
    private val reactionCountFunny: TextView = itemView.findViewById(R.id.reacation_count_funny)
    private val reactionCountVeryFunny: TextView = itemView.findViewById(R.id.reacation_count_veryfunny)
    private val reactionCountAngry: TextView = itemView.findViewById(R.id.reacation_count_angry)
    private val reactionCountStupid: TextView = itemView.findViewById(R.id.reacation_count_stupid)


    init {
        memeImageV.setOnClickListener { memeClickedListener?.invoke(currentMeme.id!!) }
        commentBtnV.setOnClickListener {
            val meme = currentMeme
            val intent = Intent(memeAdapter.context, CommentsActivity::class.java)
            intent.putExtra(CommentsActivity.MEME_PARAM_KEY, meme)
            memeAdapter.context.startActivity(intent)
        }
        posterPicV.setOnClickListener {
            val i = Intent(memeAdapter.context, ProfileActivity::class.java)
            i.putExtra("user", currentMeme.poster?.toUser())
            memeAdapter.context.startActivity(i)
        }
        reactionCountV.setOnClickListener {
            val i = Intent(memeAdapter.context, ReactorListActivity::class.java)
            i.putExtra("mid", currentMeme.id)
            memeAdapter.context.startActivity(i)
        }

        val reactListener = OnClickListener { view ->
            val (reactRes, reactionType) = when (view.id) {
                R.id.react_funny -> MemeListAdapter.activeRID[0] to FUNNY
                R.id.react_veryfunny -> MemeListAdapter.activeRID[1] to VERY_FUNNY
                R.id.react_stupid -> MemeListAdapter.activeRID[2] to STUPID
                R.id.react_angry -> MemeListAdapter.activeRID[3] to ANGERING
                else -> throw IllegalStateException()
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
        favButton.setOnClickListener { _ ->
            val meme = currentMeme
            if (meme.isMyFavourite)
                MemeItMemes.removeMemeFromFavourite(meme.id!!).call({
                    meme.isMyFavourite = false
                    favButton.isChecked = false
                }, onError())
            else
                MemeItMemes.addMemeToFavourite(meme.id!!).call({
                    meme.isMyFavourite = true
                    favButton.playAnimation()
                    favButton.isChecked = true
                }, onError())
        }
        memeMenu.setOnClickListener { showMemeMenu() }
        memeTags.setOnClickListener { showFollowDialog() }

    }

    private fun toggleReactVisibility() {
        reactGroup.visibility = if (reactGroup.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        if (reactGroup.visibility == View.VISIBLE) {
            MemeItMemes.getReactionCountByType(currentMeme.id!!).call { reactions ->
                val x = reactions.map { it.getType() to it.count }.toMap()
                reactionCountFunny.text = (x[FUNNY] ?: 0).toString()
                reactionCountVeryFunny.text = (x[VERY_FUNNY] ?: 0).toString()
                reactionCountAngry.text = (x[ANGERING] ?: 0).toString()
                reactionCountStupid.text = (x[STUPID] ?: 0).toString()
            }
        }
    }

    private fun showFollowDialog() {
        val meme = currentMeme
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
                    MemeItUsers.followTags(selectedTags).call({ memeAdapter.context.toast("Tags Followed.") },
                            onError("Failed folloeing tags"))
                }
                .show()
    }

    fun onError(message: String = "", action: (() -> Unit)? = null): (String) -> Unit = {
        memeAdapter.context.toast("$message: $it")
        action?.invoke()
    }

    private fun react(reactionType: Reaction.ReactionType, finalReactRes: Int) {
        val pos = itemPosition
        MemeItMemes.reactToMeme(reactionType.create(currentMeme.id!!)).call({

            reactButton.setActiveImage(finalReactRes)
            reactButton.playAnimation()
            reactButton.isChecked = true

            val m = memeAdapter.items[pos] as Meme
            if (m.myReaction == null) {
                m.reactionCount++
                reactionCountV.text = String.format("%d people reacted", currentMeme.reactionCount)
            }
            m.myReaction = reactionType.create()

        }, onError("Reaction Failed"))
    }


    override fun bind(homeElement: HomeElement) {
        val meme = homeElement as Meme

        reactGroup.visibility = View.GONE

        posterNameV.text = meme.poster?.name
        posterPicV.text = meme.poster?.name.prefix()
        reactionCountV.text = String.format("%d people reacted", meme.reactionCount)
        commentCountV.text = meme.commentCount.toString()
        posterPicV.loadImage(meme.poster?.profileUrl)
        memeDateV.text = meme.date?.formateAsDate()
        if (meme.tags.isEmpty()) {
            memeTags.visibility = View.GONE
        } else {
            memeTags.visibility = View.VISIBLE
            memeTags.text = meme.tags.joinToString(", ") { "#$it" }
        }
        if (meme.myReaction !== null) {
            reactButton.setActiveImage(MemeListAdapter.activeRID[meme.myReaction!!.getType().ordinal])
            reactButton.isChecked = true
        } else {
            reactButton.isChecked = false
        }
        favButton.isChecked = meme.isMyFavourite
        memeImageV.layoutParams.width = screenWidth
        memeImageV.layoutParams.height = (screenWidth / meme.imageRatio).toInt().trim(200.dp, screenHeight - 200.dp)
        memeImageV.requestLayout()
        memeImageV.loadMeme(meme)
    }

    private fun showMemeMenu() {
        val menu = PopupMenu(memeAdapter.context, memeMenu)
        if (MemeItClient.myUser?.id == currentMeme.poster!!.id)
            menu.menuInflater.inflate(R.menu.meme_menu, menu.menu)
        else
            menu.menuInflater.inflate(R.menu.meme_menu_not_own, menu.menu)

        menu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_delete_meme -> {
                    MemeItMemes.deleteMeme(currentMeme.id!!).call(
                            {
                                memeAdapter.remove(Meme(currentMeme.id))
                            }, onError("Error deleting Meme")
                    )
                    return@OnMenuItemClickListener true
                }
                R.id.menu_report_meme -> {
                    MaterialDialog.Builder(memeAdapter.context)
                            .title("Report")
                            .items("Pornography", "Abuse", "Violence", "Not appropriate", "Other")
                            .itemsCallbackMultiChoice(null) { _, _, _ ->
                                true
                            }
                            .positiveText("Report")
                            .negativeText("Cancel")
                            .onPositive { _, _ ->
                                //todo jv finish this up this shit giving me a hard time
                            }
                            .show()

                }
            }
            false
        })
        menu.show()
    }

    private val currentMeme: Meme
        get() {
            return memeAdapter.items[itemPosition] as Meme
        }
}