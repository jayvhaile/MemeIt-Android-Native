package com.innov8.memeit.Activities

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.innov8.memeit.*
import com.innov8.memeit.Adapters.CommentsAdapter
import com.innov8.memeit.CustomClasses.LoadingDrawable
import com.innov8.memeit.Loaders.CommentLoader
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItClient.context
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.dataclasses.Comment
import com.memeit.backend.dataclasses.MyUser
import com.memeit.backend.dataclasses.Meme
import com.stfalcon.frescoimageviewer.ImageViewer
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : AppCompatActivity() {
    private var isPostingComment: Boolean = false
    private lateinit var commentsAdapter: CommentsAdapter

    var myUser: MyUser = MemeItClient.myUser!!

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

        comment_meme_image.memeClickedListener = { _ ->
            val list = listOf(meme)
            val hierarchy = GenericDraweeHierarchyBuilder.newInstance(context.resources)
                    .setProgressBarImage(LoadingDrawable(context))

            val overlayView = layoutInflater.inflate(R.layout.overlay, null, false)
            val overlayName = overlayView.findViewById<TextView>(R.id.overlay_name)
            val overlayDesc = overlayView.findViewById<TextView>(R.id.overlay_description)
            val overlayTags = overlayView.findViewById<TextView>(R.id.overlay_tags)
            val preview=ImageViewer.Builder<Meme>(this, list)
                    .setFormatter { it.generateUrl() }
                    .setOverlayView(overlayView)
                    .setImageChangeListener {
                        overlayName.text = list[it].poster?.name
                        overlayDesc.text = list[it].description
                        overlayTags.text = list[it].tags.joinToString(", ") { tag -> "#$tag" }
                    }
                    .setCustomDraweeHierarchyBuilder(hierarchy)
                    .setBackgroundColor(Color.parseColor("#f6000000"))
                    .hideStatusBar(false)
                    .build()
            preview.show()
        }
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
        ml.load()

    }

    companion object {
        const val MEME_PARAM_KEY = "meme"
    }
}
