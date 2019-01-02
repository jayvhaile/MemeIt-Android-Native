package com.innov8.memeit.adapters

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.set
import androidx.core.text.toSpannable
import com.afollestad.materialdialogs.MaterialDialog
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memegenerator.adapters.MyViewHolder
import com.innov8.memeit.R
import com.innov8.memeit.activities.ProfileActivity
import com.innov8.memeit.commons.min
import com.innov8.memeit.commons.views.MemeItTextView
import com.innov8.memeit.commons.views.ProfileDraweeView
import com.innov8.memeit.utils.*
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.models.Comment
import kotlinx.android.synthetic.main.list_item_meme.view.*

/**
 * Created by Jv on 7/5/2018.
 */

class CommentsAdapter(context: Context) : SimpleELEListAdapter<Comment>(context, R.layout.list_item_comment) {
    override fun createViewHolder(view: View): MyViewHolder<Comment> {
        return CommentViewHolder(view)
    }


    override var emptyDrawableId: Int = R.drawable.no_comments
    override var errorDrawableId: Int = R.drawable.ic_no_internet
    override var emptyDescription: String = "No comments yet."
    override var errorDescription: String = "Couldn't load comments"
    override var emptyActionText: String? = ""
    override var errorActionText: String? = "Try Again"
    override val loadingDrawable = CubeGrid().apply {
        color = Color.rgb(255, 100, 0)
    }

    internal val linkActions by lazy {
        generateTextLinkActions(context)
    }

    inner class CommentViewHolder(itemView: View) : MyViewHolder<Comment>(itemView), View.OnClickListener {
        private val commentV: MemeItTextView = itemView.findViewById(R.id.list_comment)
        private val dateV: TextView = itemView.findViewById(R.id.list_item_date)
        private val posterPicV: ProfileDraweeView = itemView.findViewById(R.id.comment_poster_pp)
        private val edit: ImageView = itemView.findViewById(R.id.edit)
        private val delete: ImageView = itemView.findViewById(R.id.delete)
        private val likeCount: TextView = itemView.findViewById(R.id.like_comment)
        private val dislikeCount: TextView = itemView.findViewById(R.id.dislike_comment)
        private val like: ImageView = itemView.findViewById(R.id.like)
        private val dislike: ImageView = itemView.findViewById(R.id.dislike)


        init {
            posterPicV.setOnClickListener {
                val i = Intent(context, ProfileActivity::class.java)
                i.putExtra("user", getItemAt(item_position).poster)
                context.startActivity(i)
            }
            delete.setOnClickListener(this)
            edit.setOnClickListener(this)
            likeCount.setOnClickListener(this)
            dislikeCount.setOnClickListener(this)
            like.setOnClickListener(this)
            dislike.setOnClickListener(this)
            commentV.onLinkClicked = linkActions
        }

        private fun applySpan(text: String, vararg words: String): Spannable =
                text.toSpannable().apply {
                    words.forEach {
                        val i = text.indexOf(it)
                        this[i..i + it.length] = StyleSpan(Typeface.BOLD)
                        this[i..i + it.length] = RelativeSizeSpan(1.05f)
                    }
                }


        override fun bind(t: Comment) {
            posterPicV.loadImage(t.poster!!.imageUrl)
            commentV.text = applySpan("${t.poster!!.name} ${t.comment}", t.poster!!.name!!)
            posterPicV.setText(t.poster!!.name.prefix())
            dateV.text = t.date!!.formateAsDate()
            likeCount.text = t.likeCount.toString()
            dislikeCount.text = t.dislikeCount.toString()
            val isOwnComment = MemeItClient.myUser!!.id == t.poster!!.uid
            if (isOwnComment) {
                edit.visibility = View.VISIBLE
                delete.visibility = View.VISIBLE
            } else {
                edit.visibility = View.GONE
                delete.visibility = View.GONE
            }
            like.setColorFilter(if (t.isLikedByMe) R.color.colorAccent.color(context) else Color.parseColor("#999999"))
            dislike.setColorFilter(if (t.isDislikedByMe) R.color.colorAccent.color(context) else Color.parseColor("#999999"))
        }

        override fun onClick(v: View) {
            val comment = getItemAt(item_position)
            val pos = item_position
            when (v.id) {
                R.id.like_comment, R.id.like -> {
                    if (comment.isLikedByMe) {
                        MemeItMemes.removeLikeComment(comment.id!!).call {
                            comment.likeCount = (comment.likeCount - 1) min 0
                            notifyItemChanged(pos)
                        }
                    } else {
                        MemeItMemes.likeComment(comment.id!!).call {
                            comment.likeCount++
                            if (comment.isDislikedByMe) {
                                comment.isDislikedByMe = false
                                comment.dislikeCount = (comment.dislikeCount - 1) min 0
                            }
                            comment.isLikedByMe = true
                            notifyItemChanged(pos)
                        }
                    }
                }
                R.id.dislike_comment, R.id.dislike -> {
                    if (comment.isDislikedByMe) {
                        MemeItMemes.removeDislikeComment(comment.id!!).call {
                            comment.dislikeCount = (comment.dislikeCount - 1) min 0
                            dislikeCount.text = comment.dislikeCount.toString()
                        }
                    } else {
                        MemeItMemes.dislikeComment(comment.id!!).call {
                            comment.dislikeCount++
                            if (comment.isLikedByMe) {
                                comment.isLikedByMe = false
                                comment.likeCount = (comment.likeCount - 1) min 0
                            }
                            comment.isDislikedByMe = true
                            notifyItemChanged(pos)
                        }
                    }
                }
                R.id.delete -> MaterialDialog.Builder(context)
                        .title("Delete comment?")
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive { _, _ ->
                            MemeItMemes.deleteComment(comment.memeID!!, comment.id!!).call({
                                remove(comment)
                                notifyDataSetChanged()
                                itemView.snack("Deleted.")
                            }) {
                                itemView.snack("An error has occurred. Please try again.\n$it")
                            }
                        }.show()
                R.id.edit -> MaterialDialog.Builder(context)
                        .title("Edit comment")
                        .input("Comment", comment.comment, false, MaterialDialog.InputCallback { _, input ->
                            if (TextUtils.isEmpty(input))
                                return@InputCallback
                            val c = Comment(id = comment.id, comment = input.toString())
                            MemeItMemes.updateComment(c).call({
                                itemView.snack("Edited.")
                                comment.comment = input.toString()
                                notifyItemChanged(item_position)
                            }) {
                                itemView.snack("Editing has failed because of a problem. Please try again.\n$it" +
                                        "")

                            }
                        }).show()
            }
        }
    }
}
