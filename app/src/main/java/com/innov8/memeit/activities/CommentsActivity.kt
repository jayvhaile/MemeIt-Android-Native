package com.innov8.memeit.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memeit.adapters.CommentsAdapter
import com.innov8.memeit.loaders.CommentLoader
import com.innov8.memeit.R
import com.innov8.memeit.commons.LoaderAdapterHandler
import com.innov8.memeit.utils.loadImage
import com.innov8.memeit.utils.showMemeZoomView
import com.innov8.memeit.utils.snack
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
    private val commentsAdapter by lazy {
        CommentsAdapter(this).apply {
            showErrorAtTop = true
        }
    }
    private val commentLoader by lazy {
        CommentLoader(mid)
    }


    private val loaderAdapter by lazy {
        LoaderAdapterHandler(commentsAdapter, commentLoader).apply {
            onLoaded = { comment_srl?.isRefreshing = false }
            onLoadFailed = { message ->
                comment_srl?.snack(message)
                comment_srl?.isRefreshing = false
            }
        }
    }
    var myUser: MyUser = MemeItClient.myUser!!
    lateinit var mid: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_view_layout)
        loader_view.setContentView(R.layout.activity_comments)
        val m: Meme? = intent.getParcelableExtra(MEME_PARAM_KEY)
        if (m != null) {
            mid = m.id!!
            initComments()
            initMeme(m)
            loader_view.setLoaded()
        } else {
            intent.getStringExtra(MEMEID_PARAM_KEY)?.let {
                mid = it
            } ?: let {
                finish()
                return
            }
            initComments()
            loader_view.onRetry = { loadByID() }
            loadByID()
        }

    }

    private fun loadByID() {
        MemeItMemes.getMemeById(mid).call({
            initMeme(it)
            loader_view.setLoaded()
        }) {
            loader_view.setError()
        }
    }

    private fun initComments() {
        comment_srl.setOnRefreshListener { loaderAdapter.refresh() }
        comments_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        comments_list.itemAnimator = DefaultItemAnimator()
        comments_list.adapter = commentsAdapter

        comment_pp.loadImage(myUser.profilePic)

        comment_button.setOnClickListener { _ ->
            val txt = comment_field.text.toString()
            if (isPostingComment || txt.isEmpty()) return@setOnClickListener
            val comment = Comment(memeID = mid, comment = txt)
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
        comment_meme_image.showCommentButton = false
        comment_meme_image.resizeToFit = false
        comment_meme_image.meme = meme
        comment_meme_image.memeClickedListener ={

            showMemeZoomView(listOf(meme))
        }

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

        fun startWithMemeIntent(context: Context, meme: Meme) =
                Intent(context, CommentsActivity::class.java).apply {
                    putExtra(MEME_PARAM_KEY, meme)
                }


        fun startWithMemeIdIntent(context: Context, memeId: String) =
                Intent(context, CommentsActivity::class.java).apply {
                    putExtra(MEMEID_PARAM_KEY, memeId)
                }


    }
}
