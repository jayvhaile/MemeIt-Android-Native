package com.innov8.memeit.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import com.afollestad.materialdialogs.MaterialDialog
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memegenerator.Adapters.MyViewHolder
import com.innov8.memeit.Activities.ProfileActivity
import com.innov8.memeit.R
import com.innov8.memeit.commons.views.ProfileDraweeView
import com.innov8.memeit.formatDate
import com.innov8.memeit.loadImage
import com.innov8.memeit.prefix
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItMemes
import com.memeit.backend.OnCompleted
import com.memeit.backend.dataclasses.Comment
import okhttp3.ResponseBody
import java.security.InvalidParameterException

/**
 * Created by Jv on 7/5/2018.
 */

class CommentsAdapter(context: Context) : SimpleELEListAdapter<Comment>(context, R.layout.list_item_comment) {
    override fun createViewHolder(view: View): MyViewHolder<Comment> {
        return CommentViewHolder(view)
    }


    override var emptyDrawableId: Int = R.drawable.tag2
    override var errorDrawableId: Int = R.drawable.ic_no_internet
    override var emptyDescription: String = "No Tags"
    override var errorDescription: String = "Couldn't load Tags"
    override var emptyActionText: String? = ""
    override var errorActionText: String? = "Try Again"
    override val loadingDrawable = CubeGrid().apply {
        color = Color.rgb(255, 100, 0)
    }
    internal var isPostingComment: Boolean = false

    internal var size: Float = 0.toFloat()

    init {
        size = context.resources.getDimension(R.dimen.profile_mini_size)
    }


    inner class CommentViewHolder(itemView: View) : MyViewHolder<Comment>(itemView), View.OnClickListener {
        private val commentV: TextView
        private val dateV: TextView

        private val posterPicV: ProfileDraweeView
        private val posterNameV: TextView
        private val edit: ImageView
        private val delete: ImageView
        private val like: TextView
        private val dislike: TextView
        internal var ownCommentViews: Group


        init {
            commentV = itemView.findViewById(R.id.list_comment)
            dateV = itemView.findViewById(R.id.list_item_date)
            posterPicV = itemView.findViewById(R.id.comment_poster_pp)
            posterNameV = itemView.findViewById(R.id.list_name)
            edit = itemView.findViewById(R.id.edit)
            delete = itemView.findViewById(R.id.delete)
            like = itemView.findViewById(R.id.like_comment)
            dislike = itemView.findViewById(R.id.dislike_comment)
            ownCommentViews = itemView.findViewById(R.id.own_comment)
            posterPicV.setOnClickListener {
                val i = Intent(context, ProfileActivity::class.java)
                i.putExtra("user", getItemAt(item_position).poster.toUser())
                context.startActivity(i)
            }
            delete.setOnClickListener(this)
            edit.setOnClickListener(this)
            like.setOnClickListener(this)
            dislike.setOnClickListener(this)
        }


        override fun bind(mComment: Comment) {
            posterPicV.loadImage(mComment.poster.profileUrl, size, size)
            commentV.text = mComment.comment
            posterPicV.text = mComment.poster.name.prefix()
            posterNameV.text = mComment.poster.name
            dateV.text = formatDate(mComment.date!!)
            like.text = mComment.likeCount.toString()
            dislike.text = mComment.dislikeCount.toString()
            val isOwnComment = MemeItClient.myUser!!.id == mComment.posterID
            if (isOwnComment)
                ownCommentViews.visibility = View.GONE
            else
                ownCommentViews.visibility = View.VISIBLE
        }

        override fun onClick(v: View) {
            val comment = getItemAt(item_position)
            when (v.id) {
                R.id.like_comment -> if (comment.isLikedByMe) {
                    MemeItMemes.removeLikeComment(comment.commentID).enqueue(generateOnComplete(LIKE, REMOVE, item_position))
                } else {
                    MemeItMemes.likeComment(comment.commentID).enqueue(generateOnComplete(LIKE, ADD, item_position))
                }
                R.id.dislike_comment -> if (comment.isDislikedByMe) {
                    MemeItMemes.removeDislikeComment(comment.commentID).enqueue(generateOnComplete(DISLIKE, REMOVE, item_position))
                } else {
                    MemeItMemes.dislikeComment(comment.commentID).enqueue(generateOnComplete(DISLIKE, ADD, item_position))
                }
                R.id.delete -> MaterialDialog.Builder(context)
                        .title("Delete comment?")
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive { dialog, which ->
                            MemeItMemes.deleteComment(comment.memeID, comment.commentID)
                                    .enqueue(object : OnCompleted<ResponseBody>() {
                                        override fun onSuccess(responseBody: ResponseBody) {
                                            Toast.makeText(context, "Deleted.", Toast.LENGTH_SHORT).show()
                                            remove(comment)
                                        }

                                        override fun onError(error: String) {
                                            Toast.makeText(context, "An error has occured. Please try again.", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                        }.show()
                R.id.edit -> MaterialDialog.Builder(context)
                        .title("Edit comment")
                        .input("Comment", comment.comment, false, MaterialDialog.InputCallback { dialog, input ->
                            if (TextUtils.isEmpty(input))
                                return@InputCallback
                            val c = Comment.createCommentForUpdate(comment.commentID, input.toString())
                            MemeItMemes.updateComment(c)
                                    .enqueue(object : OnCompleted<ResponseBody>() {
                                        override fun onSuccess(o: ResponseBody) {
                                            Toast.makeText(context, "Edited.", Toast.LENGTH_SHORT).show()
                                            comment.comment = input.toString()
                                            notifyItemChanged(item_position)
                                        }

                                        override fun onError(error: String) {
                                            Toast.makeText(context, "Editing has failed because of a problem. Please try again.", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                        }).show()
            }
        }
    }

    private fun generateOnComplete(type: Int, addOrRemove: Int, position: Int): OnCompleted<ResponseBody> {
        return object : OnCompleted<ResponseBody>() {
            override fun onSuccess(o: ResponseBody) {
                val comment = getItemAt(position)
                if (type == LIKE) {
                    comment.isLikedByMe = addOrRemove == ADD
                    comment.likeCount = if (addOrRemove == ADD)
                        comment.likeCount!! + 1
                    else
                        comment.likeCount!! - 1
                    comment.dislikeCount = if (addOrRemove == ADD)
                        comment.dislikeCount!! - 1
                    else
                        comment.dislikeCount
                } else if (type == DISLIKE) {
                    comment.isDislikedByMe = addOrRemove == ADD
                    comment.dislikeCount = if (addOrRemove == ADD)
                        comment.dislikeCount!! + 1
                    else
                        comment.dislikeCount!! - 1
                    comment.likeCount = if (addOrRemove == ADD)
                        comment.likeCount!! - 1
                    else
                        comment.likeCount
                } else
                    throw InvalidParameterException("Parameter \"Type\" must be either LIKE or DISLIKE")
                notifyItemChanged(position)
            }

            override fun onError(error: String) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private val LIKE = 0
        private val DISLIKE = 1
        private val ADD = 0
        private val REMOVE = 1
    }
}
