package com.innov8.memeit.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memeit.Adapters.CommentsAdapter
import com.innov8.memeit.Loaders.CommentLoader
import com.innov8.memeit.MLHandler
import com.innov8.memeit.R
import com.innov8.memeit.loadImage
import com.innov8.memeit.snack
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.dataclasses.Comment
import com.memeit.backend.dataclasses.MUser
import com.memeit.backend.dataclasses.Meme
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : AppCompatActivity() {
    var isPostingComment: Boolean = false
    lateinit var commentsAdapter: CommentsAdapter

    var myUser: MUser = MemeItClient.myUser!!

    lateinit var ml: MLHandler<Comment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val meme: Meme = intent.getParcelableExtra(MEME_PARAM_KEY) ?: return

        commentsAdapter = CommentsAdapter(this)
        val commentLoader = CommentLoader(meme.id!!)
        ml = MLHandler(commentsAdapter, commentLoader)

        ml.onLoaded = { comment_srl?.isRefreshing = false }
        ml.onLoadFailed = { message ->
            comment_srl?.snack(message)
            comment_srl?.isRefreshing = false
        }
        comment_srl.setOnRefreshListener { ml.refresh() }
        comments_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        comments_list.itemAnimator = DefaultItemAnimator()
        comments_list.adapter = commentsAdapter

        comment_meme_image.memeClickedListener = {}
        comment_meme_image.resizeToFit = false
        comment_meme_image.meme = meme

        comment_pp.loadImage(myUser.profilePic)

        comment_button.setOnClickListener {
            val txt = comment_field.text.toString()
            if (isPostingComment || txt.isEmpty()) return@setOnClickListener
            val comment = Comment.createComment(meme.id, txt)
            isPostingComment = true
            MemeItMemes.postComment(comment).call({
                comment_field.setText("")
                ml.refresh()
                isPostingComment = false
            }, {
                comments_list.snack("Failed to load Comments")
            })
        }

    }

    companion object {
        const val MEME_PARAM_KEY = "meme"
    }
}
