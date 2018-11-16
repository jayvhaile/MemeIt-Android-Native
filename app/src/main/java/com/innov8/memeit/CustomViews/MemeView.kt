package com.innov8.memeit.CustomViews

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Guideline
import androidx.core.app.ShareCompat
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
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
import com.innov8.memeit.R.id.*
import com.innov8.memeit.commons.dp
import com.innov8.memeit.commons.toast
import com.innov8.memeit.commons.views.ProfileDraweeView
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItMemes
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.dataclasses.Meme
import com.memeit.backend.dataclasses.Reaction
import com.memeit.backend.dataclasses.Report
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

class MemeView : ConstraintLayout {

    private val constraintSetDefault: ConstraintSet

    var resizeToFit = true

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val dp16 = 16.dp(context)
    private val dp8 = 8.dp(context)
    private val dp4 = 4.dp(context)
    private val dp2 = 2.dp(context)
    private val pid = ConstraintLayout.LayoutParams.PARENT_ID
    private val wpc = ConstraintLayout.LayoutParams.WRAP_CONTENT
    private val mc = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
    private val ps = R.dimen.profile_mini_size.dimenI(context)

    companion object {
        private const val notif_icon = 0x7f090160
        private const val meme_poster_name = 0x7f090161
        private const val meme_time = 0x7f090162
        private const val meme_share = 0x7f090163
        private const val meme_options = 0x7f090164
        private const val meme_image = 0x7f090165
        private const val tint = 0x7f090166
        private const val reaction_count_funny = 0x7f090167
        private const val reaction_count_veryfunny = 0x7f090168
        private const val reaction_count_angry = 0x7f090169
        private const val reaction_count_stupid = 0x7f09016a
        private const val react_button = 0x7f09016b
        private const val meme_reactions = 0x7f09016c
        private const val fav_button = 0x7f09016d
        private const val meme_comment = 0x7f09016e
        private const val meme_comment_count = 0x7f09016f
        private const val meme_tags = 0x7f090170
        private const val description = 0x7f09017a
        private const val react_funny = 0x7f09017b
        private const val react_veryfunny = 0x7f09017c
        private const val react_angry = 0x7f09017d
        private const val react_stupid = 0x7f09017e
        private const val guideline12 =  0x7f090150
    }

    private val posterPicV = ProfileDraweeView(context).apply {
        id =  notif_icon
        setPadding(dp4, dp4, dp4, dp4)
        setOnClickListener {
            val i = Intent(context, ProfileActivity::class.java)
            i.putExtra("user", meme.poster?.toUser())
            context.startActivity(i)
        }
    }
    private val posterNameV = TextView(context).apply {
        id =  meme_poster_name
        textSize = 16f
        setTextColor(Color.rgb(49, 49, 49))
    }
    private val memeDateV = TextView(context).apply {
        id =  meme_time
        textSize = 12f
        setTextColor(Color.rgb(150, 150, 150))
    }
    private val memeShare = ImageView(context).apply {
        setImageResource(R.drawable.ic_share_black_24dp)
        id =  meme_share
        setOnClickListener { onShare() }
    }
    private val memeMenu = ImageView(context).apply {
        setImageResource(R.drawable.ic_more_vert_black_24dp)
        id =  meme_options
        setOnClickListener { showMemeMenu() }
    }
    private val memeImageV = MemeDraweeView(context).apply {
        id =  meme_image
        hierarchy.actualImageScaleType = ScalingUtils.ScaleType.FIT_CENTER
        setOnClickListener { memeClickedListener?.invoke(meme.id!!) }
        visibility = View.INVISIBLE

    }
    private val reactTint = View(context).apply {
        id =  tint
        setBackgroundColor(Color.argb(12 * 16, 0, 0, 0))
        visibility = View.GONE
    }
    private val reactionCountFunny = TextView(context).apply {
        text = "..."
        setTextColor(Color.WHITE)
        id =  reaction_count_funny
    }
    private val reactionCountVeryFunny = TextView(context).apply {
        text = "..."
        setTextColor(Color.WHITE)
        id =  reaction_count_veryfunny
    }
    private val reactionCountAngry = TextView(context).apply {
        text = "..."
        setTextColor(Color.WHITE)
        id =  reaction_count_stupid
    }
    private val reactionCountStupid = TextView(context).apply {
        text = "..."
        setTextColor(Color.WHITE)
        id =  reaction_count_angry
    }
    private val reactButton = SparkButton(context, R.drawable.laughing, R.drawable.laughing_inactive_light).apply {
        id =  react_button
        imageSize = 28.dp(context)
        primaryColor = R.color.spark_primary_emoji.color(context)
        primaryColor = R.color.spark_secondary.color(context)
        setOnClickListener {
            toggleReactVisibility()
        }
        this.init()
    }
    private val reactionCountV = TextView(context).apply {
        id =  meme_reactions
        setPadding(dp8, 0, 0, 0)
        gravity = Gravity.CENTER
        setOnClickListener {
            val i = Intent(context, ReactorListActivity::class.java)
            i.putExtra("mid", meme.id)
            context.startActivity(i)
        }
    }
    private val favButton = SparkButton(context, R.drawable.ic_favorite_red, R.drawable.ic_favourite).apply {
        id =  fav_button
        imageSize = 24.dp(context)
        primaryColor = R.color.spark_primary.color(context)
        primaryColor = R.color.spark_secondary.color(context)
        setOnClickListener { _ ->
            val m = meme
            if (m.isMyFavourite)
                MemeItMemes.removeMemeFromFavourite(m.id!!).call({
                    m.isMyFavourite = false
                    if (m == meme) isChecked = false
                }, onError())
            else
                MemeItMemes.addMemeToFavourite(m.id!!).call({
                    m.isMyFavourite = true
                    playAnimation()
                    if (m == meme) isChecked = true
                }, onError())

        }
        this.init()
    }
    private val memeCommentButton = ImageView(context).apply {
        id =  meme_comment
        scaleType = ImageView.ScaleType.FIT_CENTER
        setImageResource(R.drawable.ic_comment)
        setOnClickListener {
            val meme = meme
            val intent = Intent(context, CommentsActivity::class.java)
            intent.putExtra(CommentsActivity.MEME_PARAM_KEY, meme)
            context.startActivity(intent)
        }
    }
    private val commentCountV = TextView(context).apply {
        id =  meme_comment_count
    }
    private val memeTags = TextView(context).apply {
        id =  meme_tags
        setTextColor(Color.rgb(19, 148, 253))
        setOnClickListener { showFollowDialog() }
        text = "#tags"
    }
    private val memeDescription = TextView(context).apply {
        id = description
        setTextColor(Color.rgb(49, 49, 49))
        text = "description"
    }
    private val reactionArray = intArrayOf( react_funny,  react_veryfunny,  react_angry,  react_stupid)


    init {
        val g = Guideline(context).apply {
            id =  guideline12
        }
        this.addView(g, ConstraintLayout.LayoutParams(wpc, wpc).apply {
            orientation = ConstraintLayout.LayoutParams.HORIZONTAL
            guideBegin = 60.dp(context)
        })
        this.addView(posterPicV, ConstraintLayout.LayoutParams(ps, ps).apply {
            leftMargin = dp16
            topMargin = dp4
            bottomMargin = dp4
            bottomToTop =  guideline12
            leftToLeft = pid
            topToTop = pid
        })

        this.addView(posterNameV, ConstraintLayout.LayoutParams(wpc, wpc).apply {
            leftMargin = dp16
            topMargin = dp8
            leftToRight =  notif_icon
            topToTop = pid
        })

        this.addView(memeDateV, ConstraintLayout.LayoutParams(wpc, wpc).apply {
            topMargin = dp2
            leftToLeft =  meme_poster_name
            topToBottom =  meme_poster_name

        })

        this.addView(memeShare, ConstraintLayout.LayoutParams(wpc, mc).apply {
            rightMargin = dp16
            rightToLeft =  meme_options
            topToTop = pid
            bottomToTop =  guideline12

        })

        this.addView(memeMenu, ConstraintLayout.LayoutParams(wpc, mc).apply {
            rightMargin = dp8
            rightToRight = pid
            topToTop = pid
            bottomToTop =  guideline12
        })

        this.addView(memeImageV, ConstraintLayout.LayoutParams(mc, 205.dp(context)).apply {
            rightToRight = pid
            leftToLeft = pid
            topToBottom =  guideline12
        })
        this.addView(reactTint, ConstraintLayout.LayoutParams(mc, mc).apply {
            rightToRight = pid
            leftToLeft = pid
            topToTop =  meme_image
            bottomToBottom =  meme_image
        })


        //========================================================

        val reactListener = OnClickListener { view ->
            val (reactRes, reactionType) = when (view.id) {
                react_funny -> MemeListAdapter.activeRID[0] to Reaction.ReactionType.FUNNY
                react_veryfunny -> MemeListAdapter.activeRID[1] to Reaction.ReactionType.VERY_FUNNY
                react_stupid -> MemeListAdapter.activeRID[2] to Reaction.ReactionType.STUPID
                react_angry -> MemeListAdapter.activeRID[3] to Reaction.ReactionType.ANGERING
                else -> throw IllegalStateException()
            }
            react(reactionType, reactRes)
            toggleReactVisibility()
        }
        val da = intArrayOf(R.drawable.laughing, R.drawable.rofl, R.drawable.neutral, R.drawable.angry)
        reactionArray.mapIndexed { index, rid ->
            ImageView(context).apply {
                id = rid
                setImageResource(da[index])
                setOnClickListener(reactListener)
            }
        }.forEach {
            this.addView(it, ConstraintLayout.LayoutParams(1, 1).apply {
                bottomToBottom =  react_button
                topToTop =  react_button
                leftToLeft =  react_button
                rightToRight =  react_button
            })

        }


        arrayOf(reactionCountFunny, reactionCountVeryFunny, reactionCountAngry, reactionCountStupid)
                .forEachIndexed { index, TextView ->
                    this.addView(TextView, ConstraintLayout.LayoutParams(wpc, wpc).apply {
                        topToBottom = reactionArray[index]
                        leftToLeft = reactionArray[index]
                        rightToRight = reactionArray[index]
                    })
                }
        //========================================================
        this.addView(memeDescription, ConstraintLayout.LayoutParams(mc, wpc).apply {
            leftMargin = dp16
            rightMargin = dp16
            topMargin = dp8
            rightToRight = pid
            leftToLeft = pid
            topToBottom =  meme_image
        })

        this.addView(memeTags, ConstraintLayout.LayoutParams(wpc, wpc).apply {
            leftMargin = dp16
            topMargin = dp8
            leftToLeft = pid
            topToBottom =  description
        })
        val dp56 = 56.dp(context)
        this.addView(reactButton, ConstraintLayout.LayoutParams(dp56, dp56).apply {
            leftMargin = dp16
            leftToLeft = pid
            topToBottom =  meme_tags
            bottomToBottom = pid
        })




        this.addView(reactionCountV, ConstraintLayout.LayoutParams(wpc, 24.dp(context)).apply {
            bottomToBottom =  react_button
            leftToRight =  react_button
            topToTop =  react_button
        })

        this.addView(favButton, ConstraintLayout.LayoutParams(dp56, dp56).apply {
            rightMargin = dp16
            topMargin = dp8
            bottomMargin = dp8

            bottomToBottom =  react_button
            rightToLeft =  meme_comment
            topToTop =  react_button
        })

        val dp24 = 24.dp(context)
        this.addView(memeCommentButton, ConstraintLayout.LayoutParams(dp24, dp24).apply {
            rightMargin = dp8

            bottomToBottom =  react_button
            rightToLeft =  meme_comment_count
            topToTop =  react_button
        })



        this.addView(commentCountV, ConstraintLayout.LayoutParams(wpc, wpc).apply {
            rightMargin = dp16
            bottomToBottom =  react_button
            rightToRight = pid
            topToTop =  react_button
        })

        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        setBackgroundResource(R.drawable.bottom_nav_back)

        constraintSetDefault = ConstraintSet().apply {
            clone(this)
        }

    }

    var meme: Meme = Meme()
        set(value) {
            field = value
            onMemeUpdated()
        }

    var memeClickedListener: ((String) -> Unit)? = null
    var onRemoveMeme: ((Meme) -> Unit)? = null


    private fun toggleReactVisibility() {
        if (reactTint.visibility == View.VISIBLE) {
            hideReaction()
        } else {
            showReaction()
            val start = System.currentTimeMillis()
            MemeItMemes.getReactionCountByType(meme.id!!).call { reactions ->
                val apply = {
                    val x = reactions.map { it.getType() to it.count }.toMap()
                    reactionCountFunny.text = (x[Reaction.ReactionType.FUNNY] ?: 0).toString()
                    reactionCountVeryFunny.text = (x[Reaction.ReactionType.VERY_FUNNY]
                            ?: 0).toString()
                    reactionCountAngry.text = (x[Reaction.ReactionType.ANGERING] ?: 0).toString()
                    reactionCountStupid.text = (x[Reaction.ReactionType.STUPID] ?: 0).toString()
                }
                val dur = System.currentTimeMillis() - start
                val d = 500
                if (dur < d)
                    Handler().postDelayed(apply, d - dur)
                else apply()

            }
        }
    }

    private fun hideReaction(animate: Boolean = true) {
        constraintSetDefault.apply {
            reactionArray.forEach {
                clear(it)
                connect(it, ConstraintSet.BOTTOM,  react_button, ConstraintSet.BOTTOM)
                connect(it, ConstraintSet.START,  react_button, ConstraintSet.START)
                connect(it, ConstraintSet.END,  react_button, ConstraintSet.END)
                connect(it, ConstraintSet.TOP,  react_button, ConstraintSet.TOP)

                constrainWidth(it, 1)
                constrainHeight(it, 1)
            }
            setVisibility( tint, View.GONE)
        }
        if (animate) TransitionManager.beginDelayedTransition(this, makeTransition())
        constraintSetDefault.applyTo(this)
    }


    private fun makeTransition(): TransitionSet {
        return TransitionSet().apply {
            reactionArray.mapIndexed { index, i ->
                ChangeBounds().apply {
                    addTarget(i)
                    startDelay = index * 60L
                    interpolator = AccelerateDecelerateInterpolator()
                }
            }.forEach { addTransition(it) }
            addTransition(Fade(Fade.IN).apply {
                addTarget( tint)
            })
            addTransition(Fade(Fade.OUT).apply {
                addTarget( tint)
            })
            duration = 300L
        }
    }

    private fun showReaction() {
        constraintSetDefault.apply {
            reactionArray.forEach {
                clear(it)
                connect(it, ConstraintSet.BOTTOM,  meme_image, ConstraintSet.BOTTOM, 32.dp(context))
                val s = 42.dp(context)
                constrainWidth(it, s)
                constrainHeight(it, s)
            }
            createHorizontalChain(ConstraintSet.PARENT_ID, ConstraintSet.LEFT,
                    ConstraintSet.PARENT_ID, ConstraintSet.RIGHT,
                    reactionArray,
                    null,
                    ConstraintSet.CHAIN_SPREAD)
            setVisibility( tint, View.VISIBLE)
        }
        TransitionManager.beginDelayedTransition(this, makeTransition())
        constraintSetDefault.applyTo(this)
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
                menu_delete_meme -> {
                    MemeItMemes.deleteMeme(meme.id!!).call(
                            {
                                onRemoveMeme?.invoke(Meme(meme.id))
                            }, onError("Error deleting Meme")
                    )
                    return@OnMenuItemClickListener true
                }
                menu_edit_meme -> {
                    MemeUpdateActivity.startWithMeme(context, meme)
                }
                menu_report_meme -> {
                    var message = ""
                    val rt = Report.ReportTypes.values()
                    MaterialDialog.Builder(context)
                            .title("Report")
                            .items("Pornography", "Abuse", "Violence", "Not appropriate", "Other")
                            .itemsCallbackMultiChoice(null) { _, which: Array<out Int>, _ ->
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
        if (reactTint.visibility == View.VISIBLE) {
            hideReaction()
        }
        posterNameV.text = meme.poster?.name
        posterPicV.setText(meme.poster?.name.prefix())
        reactionCountV.text = String.format("%d people reacted", meme.reactionCount)
        commentCountV.text = meme.commentCount.toString()
        posterPicV.loadImage(meme.poster?.profileUrl)
        memeDateV.text = meme.date?.formateAsDate()

        if (meme.myReaction !== null) {
            reactButton.setActiveImage(meme.myReaction!!.getDrawableID())
            reactButton.setChecked(true)
        } else {
            reactButton.setChecked(false)
        }

        favButton.setChecked(meme.isMyFavourite)
        if (resizeToFit) {
            val h = (screenWidth / meme.imageRatio).toInt().trim(200.dp, screenHeight - 200.dp)
            constraintSetDefault.constrainWidth( meme_image, screenWidth)
            constraintSetDefault.constrainHeight( meme_image, h)
            constraintSetDefault.connect( meme_image, ConstraintSet.TOP,  guideline12, ConstraintSet.BOTTOM)
            constraintSetDefault.applyTo(this)

        }
        if (meme.tags.isEmpty()) {
            memeTags.text = ""
            constraintSetDefault.connect( description, ConstraintSet.TOP,  description, ConstraintSet.BOTTOM)

            applyVisible( meme_tags, View.GONE)
        } else {
            constraintSetDefault.connect( description, ConstraintSet.TOP,  description, ConstraintSet.BOTTOM)

            memeTags.text = meme.tags.joinToString(", ") { "#$it" }
            applyVisible( meme_tags, View.VISIBLE)
        }
        if (meme.description == null || meme.description!!.isBlank()) {
            constraintSetDefault.connect( description, ConstraintSet.TOP,  meme_image, ConstraintSet.BOTTOM)
            applyVisible( description, View.GONE)
            memeDescription.text = ""
        } else {
            constraintSetDefault.connect( description, ConstraintSet.TOP,  meme_image, ConstraintSet.BOTTOM)

            applyVisible( description, View.VISIBLE)
            memeDescription.text = meme.description
        }
        memeImageV.loadMeme(meme)
    }

    private fun applyVisible(id: Int, visibility: Int) {
        constraintSetDefault.apply {
            setVisibility(id, visibility)
            applyTo(this@MemeView)
        }
    }
}