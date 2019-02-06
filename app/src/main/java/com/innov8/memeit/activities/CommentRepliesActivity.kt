package com.innov8.memeit.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.innov8.memeit.R
import com.innov8.memeit.adapters.CommentsAdapter
import com.innov8.memeit.adapters.ReplyAdapter
import com.innov8.memeit.commons.LoaderAdapterHandler
import com.innov8.memeit.commons.min
import com.innov8.memeit.commons.prefix
import com.innov8.memeit.loaders.ReplyLoader
import com.innov8.memeit.utils.*
import com.memeit.backend.MemeItClient.context
import com.memeit.backend.MemeItClient.myUser
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.models.Comment
import com.memeit.backend.models.Reply
import kotlinx.android.synthetic.main.activity_comment_replies.*
import kotlinx.android.synthetic.main.list_item_comment.*

class CommentRepliesActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val ARG_COMMENT = "comment"
        fun start(context: Context, comment: Comment) {
            context.startActivity(Intent(context, CommentRepliesActivity::class.java).apply {
                putExtra(ARG_COMMENT, comment)
            })
        }
    }

    private val comment: Comment
        get() = intent!!.getParcelableExtra(ARG_COMMENT)


    private val adapter by lazy {
        ReplyAdapter(this.applicationContext, comment.id!!)
    }

    private val loader by lazy {
        ReplyLoader(comment.id!!)
    }

    private val loaderAdapter by lazy {
        LoaderAdapterHandler(adapter, loader).apply {
            onLoaded = {
                reply_refresh.isRefreshing = false
            }
            onLoadFailed = {
                reply_refresh.isRefreshing = false
                reply_list.snack("Couldn't load replies")
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_replies)
        loaderAdapter.load()
        initCommentView()
        init()

    }

    private var isPostingReply = false

    private fun initCommentView(t: Comment = comment) {
        comment_poster_pp.loadImage(t.poster!!.imageUrl)
        comment_poster_pp.setText(t.poster!!.name.prefix())
        like_comment.setOnClickListener(this)
        dislike_comment.setOnClickListener(this)
        like.setOnClickListener(this)
        dislike.setOnClickListener(this)
        list_comment.onLinkClicked = generateTextLinkActions(this)
        bindComment()
    }

    private fun bindComment(t: Comment = comment) {
        list_comment.text = CommentsAdapter.applySpan("${t.poster!!.name} ${t.comment}", t.poster!!.name!!)
        list_item_date.text = t.date!!.formateAsDate()
        like_comment.text = t.likeCount.formatNumber()
        dislike_comment.text = t.dislikeCount.formatNumber()
        reply_count.text = t.replyCount.formatNumber("reply", "replies")
        like.setColorFilter(if (t.isLikedByMe) R.color.colorAccent.color(context) else Color.parseColor("#999999"))
        dislike.setColorFilter(if (t.isDislikedByMe) R.color.colorAccent.color(context) else Color.parseColor("#999999"))
    }

    private fun init() {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.title = "Replies"
            it.setDisplayHomeAsUpEnabled(true)
        }
        reply_refresh.setOnRefreshListener { loaderAdapter.refresh() }
        reply_list.makeLinear()
        reply_list.adapter = adapter

        comment_pp.loadImage(myUser!!.profilePic)

        comment_button.setOnClickListener { _ ->
            val txt = comment_field.text.toString()
            if (isPostingReply || txt.isEmpty()) return@setOnClickListener
            val reply = Reply(reply = txt)
            isPostingReply = true
            MemeItMemes.postReply(comment.id!!, reply).call({
                comment_field.setText("")
                loaderAdapter.refresh()
                comment.replyCount++
                bindComment()
                isPostingReply = false
            }, {
                reply_list.snack("Couldn't post reply")
            })
        }
        loaderAdapter.load()
    }

    override fun onClick(v: View) {
        val c = this.comment
        when (v.id) {
            R.id.like_comment, R.id.like -> {
                if (c.isLikedByMe) {
                    MemeItMemes.removeLikeComment(c.id!!).call {
                        c.likeCount = (c.likeCount - 1) min 0
                    }
                } else {
                    MemeItMemes.likeComment(c.id!!).call {
                        c.likeCount++
                        if (c.isDislikedByMe) {
                            c.isDislikedByMe = false
                            c.dislikeCount = (c.dislikeCount - 1) min 0
                        }
                        c.isLikedByMe = true
                    }
                }
            }
            R.id.dislike_comment, R.id.dislike -> {
                if (c.isDislikedByMe) {
                    MemeItMemes.removeDislikeComment(c.id!!).call {
                        c.dislikeCount = (c.dislikeCount - 1) min 0
                    }
                } else {
                    MemeItMemes.dislikeComment(c.id!!).call {
                        c.dislikeCount++
                        if (c.isLikedByMe) {
                            c.isLikedByMe = false
                            c.likeCount = (c.likeCount - 1) min 0
                        }
                        c.isDislikedByMe = true
                    }
                }
            }
        }
        bindComment(c)
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }

}
