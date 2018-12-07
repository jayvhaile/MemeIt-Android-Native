package com.innov8.memeit.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memeit.Adapters.CommentsAdapter
import com.innov8.memeit.Loaders.CommentLoader
import com.innov8.memeit.Utils.LoaderAdapterHandler
import com.innov8.memeit.R
import com.innov8.memeit.Utils.loadImage
import com.innov8.memeit.Utils.snack
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.models.Comment
import com.memeit.backend.models.Meme
import com.memeit.backend.models.MyUser
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.loading_view_layout.*

class CommentsActivity : AppCompatActivity() {
    private var isPostingComment: Boolean = false
    private lateinit var commentsAdapter: CommentsAdapter

    var myUser: MyUser = MemeItClient.myUser!!

    lateinit var loaderAdapter: LoaderAdapterHandler<Comment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_view_layout)
        loader_view.setContentView(R.layout.activity_comments)
        val m: Meme? = intent.getParcelableExtra(MEME_PARAM_KEY)
        if (m != null) {
            initComments(m.id!!)
            initMeme(m)
            loader_view.setLoaded()
        } else {
            val mid = intent.getStringExtra(MEMEID_PARAM_KEY)
            if (mid == null) {
                finish()
                return
            }
            initComments(mid)
            loader_view.onRetry = { loadByID(mid) }
            loadByID(mid)
        }

    }

    private fun loadByID(mid: String) {
        MemeItMemes.getMemeById(mid).call({
            initMeme(it)
            loader_view.setLoaded()
        }) {
            loader_view.setError()
        }
    }

    private fun initComments(mid: String) {
        commentsAdapter = CommentsAdapter(this).apply {
            showErrorAtTop=true
        }
        val commentLoader = CommentLoader(mid)
        loaderAdapter = LoaderAdapterHandler(commentsAdapter, commentLoader)

        loaderAdapter.onLoaded = { comment_srl?.isRefreshing = false }
        loaderAdapter.onLoadFailed = { message ->
            comment_srl?.snack(message)
            comment_srl?.isRefreshing = false
        }

        comment_srl.setOnRefreshListener { loaderAdapter.refresh() }
        comments_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        comments_list.itemAnimator = DefaultItemAnimator()
        comments_list.adapter = commentsAdapter

        comment_pp.loadImage(myUser.profilePic)

        comment_button.setOnClickListener { _ ->
            val txt = comment_field.text.toString()
            if (isPostingComment || txt.isEmpty()) return@setOnClickListener
            val comment = Comment.createComment(mid, txt)
            isPostingComment = true
            MemeItMemes.postComment(comment).call({
                comment_field.setText("")
                loaderAdapter.refresh()
                isPostingComment = false
            }, {
                comments_list.snack("Failed to load Comments")
            })
        }
        loaderAdapter.load()
    }

    private fun initMeme(meme: Meme) {
        comment_meme_image.showCommentButton=false
        comment_meme_image.resizeToFit = false
        comment_meme_image.meme = meme
    }

    companion object {
        const val MEME_PARAM_KEY = "meme"
        const val MEMEID_PARAM_KEY = "mid"

        fun startWithMeme(context: Context, meme: Meme) {
            context.startActivity(Intent(context, CommentsActivity::class.java).apply {
                putExtra(MEME_PARAM_KEY, meme)
            })
        }

        fun startWithMemeId(context: Context, memeId: String) {
            context.startActivity(Intent(context, CommentsActivity::class.java).apply {
                putExtra(MEMEID_PARAM_KEY, memeId)
            })
        }
    }
}
