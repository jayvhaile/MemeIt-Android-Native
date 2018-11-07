package com.innov8.memeit.CustomViews

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ShareCompat
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.CloseableBitmap
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.innov8.memeit.*
import com.innov8.memeit.Activities.CommentsActivity
import com.innov8.memeit.Activities.MemeUpdateActivity
import com.innov8.memeit.Activities.ProfileActivity
import com.innov8.memeit.Activities.ReactorListActivity
import com.innov8.memeit.Adapters.MemeAdapters.MemeListAdapter
import com.innov8.memeit.commons.toast
import com.innov8.memeit.commons.views.ProfileDraweeView
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItMemes
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.dataclasses.Meme
import com.memeit.backend.dataclasses.Reaction
import com.memeit.backend.dataclasses.Report
import com.varunest.sparkbutton.SparkButton
import kotlinx.android.synthetic.main.list_item_meme2.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

class MemeView : FrameLayout {
    private val constraintSetReaction by lazy {
        ConstraintSet().apply {
            clone(context, R.layout.list_item_meme)

        }
    }
    private lateinit var constraintSetDefault: ConstraintSet

    var resizeToFit = true

    constructor(context: Context) : super(context) {
        initC()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initC()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initC()
    }

    private fun initC() {
        com.innov8.memeit.measure("constraint") {
            constraintSetDefault = ConstraintSet().apply {
                clone(itemView)
            }
        }
    }

    val itemView: ConstraintLayout = LayoutInflater.from(context).inflate(R.layout.list_item_meme2, this, false) as ConstraintLayout

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
    private val reactTint: View = itemView.findViewById(R.id.tint)
    private val memeTags: TextView = itemView.findViewById(R.id.meme_tags)
    private val memeDescription: TextView = itemView.findViewById(R.id.description)
    private val reactionCountFunny: TextView = itemView.findViewById(R.id.reacation_count_funny)
    private val reactionCountVeryFunny: TextView = itemView.findViewById(R.id.reacation_count_veryfunny)
    private val reactionCountAngry: TextView = itemView.findViewById(R.id.reacation_count_angry)
    private val reactionCountStupid: TextView = itemView.findViewById(R.id.reacation_count_stupid)


    var meme: Meme = Meme()
        set(value) {
            field = value
            onMemeUpdated()
        }

    var memeClickedListener: ((String) -> Unit)? = null
    var onRemoveMeme: ((Meme) -> Unit)? = null


    init {
        com.innov8.memeit.measure("init meme") {


            memeImageV.setOnClickListener { memeClickedListener?.invoke(meme.id!!) }
            commentBtnV.setOnClickListener {
                val meme = meme
                val intent = Intent(context, CommentsActivity::class.java)
                intent.putExtra(CommentsActivity.MEME_PARAM_KEY, meme)
                context.startActivity(intent)
            }
            posterPicV.setOnClickListener {
                val i = Intent(context, ProfileActivity::class.java)
                i.putExtra("user", meme.poster?.toUser())
                context.startActivity(i)
            }
            reactionCountV.setOnClickListener {
                val i = Intent(context, ReactorListActivity::class.java)
                i.putExtra("mid", meme.id)
                context.startActivity(i)
            }

            val reactListener = OnClickListener { view ->
                val (reactRes, reactionType) = when (view.id) {
                    R.id.react_funny -> MemeListAdapter.activeRID[0] to Reaction.ReactionType.FUNNY
                    R.id.react_veryfunny -> MemeListAdapter.activeRID[1] to Reaction.ReactionType.VERY_FUNNY
                    R.id.react_stupid -> MemeListAdapter.activeRID[2] to Reaction.ReactionType.STUPID
                    R.id.react_angry -> MemeListAdapter.activeRID[3] to Reaction.ReactionType.ANGERING
                    else -> throw IllegalStateException()
                }
                react(reactionType, reactRes)
                toggleReactVisibility()
            }
            itemView.findViewById<View>(R.id.react_funny).setOnClickListener(reactListener)
            itemView.findViewById<View>(R.id.react_veryfunny).setOnClickListener(reactListener)
            itemView.findViewById<View>(R.id.react_stupid).setOnClickListener(reactListener)
            itemView.findViewById<View>(R.id.react_angry).setOnClickListener(reactListener)
            reactButton.setOnClickListener {
                toggleReactVisibility()

            }
            favButton.isChecked = false
            favButton.setOnClickListener { _ ->
                val m = meme
                if (m.isMyFavourite)
                    MemeItMemes.removeMemeFromFavourite(m.id!!).call({
                        m.isMyFavourite = false
                        if (m == meme) favButton.isChecked = false
                    }, onError())
                else
                    MemeItMemes.addMemeToFavourite(m.id!!).call({
                        m.isMyFavourite = true
                        favButton.playAnimation()
                        if (m == meme) favButton.isChecked = true
                    }, onError())
            }
            memeMenu.setOnClickListener { showMemeMenu() }
            memeTags.setOnClickListener { showFollowDialog() }
            this.addView(itemView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            this.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            this.setBackgroundColor(Color.RED)

            meme_share.setOnClickListener { onShare() }
        }
    }

    private fun toggleReactVisibility() {
        if (reactTint.visibility == View.VISIBLE) {
            hideReaction()
        } else {
            showReaction()
            MemeItMemes.getReactionCountByType(meme.id!!).call { reactions ->
                val x = reactions.map { it.getType() to it.count }.toMap()
                reactionCountFunny.text = (x[Reaction.ReactionType.FUNNY] ?: 0).toString()
                reactionCountVeryFunny.text = (x[Reaction.ReactionType.VERY_FUNNY] ?: 0).toString()
                reactionCountAngry.text = (x[Reaction.ReactionType.ANGERING] ?: 0).toString()
                reactionCountStupid.text = (x[Reaction.ReactionType.STUPID] ?: 0).toString()
            }
        }
    }

    private fun hideReaction() {
        TransitionManager.beginDelayedTransition(itemView)
        constraintSetDefault.applyTo(itemView)
    }

    private fun showReaction() {
        val h = (screenWidth / meme.imageRatio).toInt().trim(200.dp, screenHeight - 200.dp)
        constraintSetReaction.constrainWidth(R.id.meme_image, screenWidth)
        constraintSetReaction.constrainHeight(R.id.meme_image, h)
        TransitionManager.beginDelayedTransition(itemView)
        constraintSetReaction.applyTo(itemView)
    }

    private fun onShare() {
        val ap = DynamicLink.AndroidParameters.Builder("com.innov8.memeit").build()
        val ll = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setDomainUriPrefix("memeit.page.link")
                .setLink(Uri.parse("${MemeItApp.SERVER_URL}meme/${meme.id}"))
                .setAndroidParameters(ap)
                .buildDynamicLink().uri
        val req = ImageRequestBuilder.newBuilderWithSource(Uri.parse(meme.generateUrl()))
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.BITMAP_MEMORY_CACHE)
                .build()
        launch(UI) {
            val res = withContext(CommonPool) { Fresco.getImagePipeline().fetchDecodedImage(req, context).result }
            if (res != null && res.get() is CloseableBitmap) {
                val bitmap = (res.get() as CloseableBitmap).underlyingBitmap
                val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap!!, "memeit", "memeit")
                val url = Uri.parse(path)
                ShareCompat.IntentBuilder.from(context as Activity)
                        .setText(ll.toString())
                        .setStream(url)
                        .setType("image/*")
                        .startChooser()
            } else {
                context.toast("Image not downloaded yet")
            }

        }

    }


    private fun showFollowDialog() {
        val meme = meme
        MaterialDialog.Builder(context)
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
                    MemeItUsers.followTags(selectedTags).call({ context.toast("Tags Followed.") },
                            onError("Failed folloeing tags"))
                }
                .show()
    }

    fun onError(message: String = "", action: (() -> Unit)? = null): (String) -> Unit = {
        snack("$message: $it")
        action?.invoke()
    }

    private fun react(reactionType: Reaction.ReactionType, finalReactRes: Int) {
        val m = meme
        MemeItMemes.reactToMeme(reactionType.create(meme.id!!)).call({
            reactButton.setActiveImage(finalReactRes)
            reactButton.playAnimation()
            reactButton.isChecked = true

            if (m.myReaction == null) {
                m.reactionCount++
                if (m == meme) reactionCountV.text = String.format("%d people reacted", m.reactionCount)

            }
            m.myReaction = reactionType.create()
            if (m == meme) reactButton.setActiveImage(m.myReaction!!.getDrawableID())
        }, onError("Reaction Failed"))
    }

    private fun showMemeMenu() {
        val menu = PopupMenu(context, memeMenu)
        if (MemeItClient.myUser?.id == meme.poster!!.id)
            menu.menuInflater.inflate(R.menu.meme_menu, menu.menu)
        else
            menu.menuInflater.inflate(R.menu.meme_menu_not_own, menu.menu)

        menu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_delete_meme -> {
                    MemeItMemes.deleteMeme(meme.id!!).call(
                            {
                                onRemoveMeme?.invoke(Meme(meme.id))
                            }, onError("Error deleting Meme")
                    )
                    return@OnMenuItemClickListener true
                }
                R.id.menu_edit_meme -> {
                    MemeUpdateActivity.startWithMeme(context, meme)
                }
                R.id.menu_report_meme -> {
                    var message = ""
                    val rt = Report.ReportTypes.values()
                    MaterialDialog.Builder(context)
                            .title("Report")
                            .items("Pornography", "Abuse", "Violence", "Not appropriate", "Other")
                            .itemsCallbackMultiChoice(null) { d, which: Array<out Int>, _ ->
                                if (which.contains(4)) {
                                    //todo add a way for them to add their own report message
                                } else message = which.filter { it != 4 }
                                        .joinToString(", ") { rt[it].message }
                                true
                            }
                            .positiveText("Report")
                            .negativeText("Cancel")
                            .onPositive { _, _ ->
                                if (message.isNotBlank()) {
                                    MemeItMemes.reportMeme(Report.MemeReport(meme.id!!, message)).call({ _ ->
                                        context.toast("Meme reported! We will look into it!")
                                    }) {
                                        context.toast("Meme report failed, Please Try Again!")
                                    }
                                }
                            }
                            .show()

                }
            }
            false
        })
        menu.show()
    }


    private fun onMemeUpdated() {
        posterNameV.text = meme.poster?.name
        posterPicV.text = meme.poster?.name.prefix()
        reactionCountV.text = String.format("%d people reacted", meme.reactionCount)
        commentCountV.text = meme.commentCount.toString()
        posterPicV.loadImage(meme.poster?.profileUrl)
        memeDateV.text = meme.date?.formateAsDate()

        if (meme.myReaction !== null) {
            reactButton.setActiveImage(meme.myReaction!!.getDrawableID())
            reactButton.isChecked = true
        } else {
            reactButton.isChecked = false
        }

        favButton.isChecked = meme.isMyFavourite
        if (resizeToFit) {
            val h = (screenWidth / meme.imageRatio).toInt().trim(200.dp, screenHeight - 200.dp)
            constraintSetDefault.constrainWidth(R.id.meme_image, screenWidth)
            constraintSetDefault.constrainHeight(R.id.meme_image, h)
            constraintSetDefault.applyTo(itemView)
        }
        if (meme.tags.isEmpty()) {
            memeTags.text = ""
            memeTags.visibility = View.GONE
        } else {
            memeTags.text = meme.tags.joinToString(", ") { "#$it" }
            memeTags.visibility = View.VISIBLE
        }
        if (meme.description == null || meme.description!!.isBlank()) {
            memeDescription.visibility = View.GONE
            memeDescription.text = ""
        } else {
            memeDescription.visibility = View.VISIBLE
            memeDescription.text = meme.description
        }
        memeImageV.loadMeme(meme)
    }
}

fun View.visibleBy(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}