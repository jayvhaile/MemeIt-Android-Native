package com.innov8.memeit.CustomViews

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.image.CloseableBitmap
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.innov8.memegenerator.utils.addWaterMark
import com.innov8.memeit.Activities.*
import com.innov8.memeit.Adapters.MemeAdapters.MemeListAdapter
import com.innov8.memeit.MemeItApp
import com.innov8.memeit.R
import com.innov8.memeit.Utils.*
import com.innov8.memeit.commons.dp
import com.innov8.memeit.commons.models.TypefaceManager
import com.innov8.memeit.commons.toast
import com.innov8.memeit.commons.views.MemeItTextView
import com.innov8.memeit.commons.views.MemeItTextView.LinkMode.*
import com.innov8.memeit.commons.views.ProfileDraweeView
import com.luseen.autolinklibrary.AutoLinkTextView
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.models.Meme
import com.memeit.backend.models.Reaction
import com.memeit.backend.models.Report
import com.varunest.sparkbutton.SparkButton
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import java.io.File
import java.io.FileOutputStream

class MemeView : FrameLayout {
    private lateinit var constraintSetDefault: ConstraintSet

    var resizeToFit = true
    var showCommentButton = true
        set(value) {
            field = value
            applyVisible(R.id.meme_comment_count, if (field) View.VISIBLE else View.GONE)
            applyVisible(R.id.meme_comment, if (field) View.VISIBLE else View.GONE, true)
        }

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
        com.innov8.memeit.Utils.measure("constraint") {
            constraintSetDefault = ConstraintSet().apply {
                clone(itemView)
            }
        }
    }

    val itemView: ConstraintLayout = LayoutInflater.from(context).inflate(R.layout.list_item_meme, this, false) as ConstraintLayout
    private val reactionArray = intArrayOf(R.id.react_funny, R.id.react_veryfunny, R.id.react_angry, R.id.react_stupid)
    private val posterPicV: ProfileDraweeView = itemView.findViewById(R.id.notif_icon)
    private val memeImageV: MemeDraweeView = itemView.findViewById(R.id.meme_image)
    private val commentBtnV: ImageButton = itemView.findViewById(R.id.meme_comment)
    private val posterNameV: TextView = itemView.findViewById(R.id.meme_poster_name)
    private val memeDateV: TextView = itemView.findViewById(R.id.meme_time)
    private val reactionCountV: TextView = itemView.findViewById(R.id.meme_reactions)
    private val commentCountV: TextView = itemView.findViewById(R.id.meme_comment_count)
    private val memeMenu: ImageButton = itemView.findViewById(R.id.meme_options)
    private val memeShare: ImageView = itemView.findViewById(R.id.meme_share)
    private val reactButton: SparkButton = itemView.findViewById(R.id.react_button)
    private val favButton: SparkButton = itemView.findViewById(R.id.fav_button)
    private val reactTint: View = itemView.findViewById(R.id.tint)
    private val memeDescription: MemeItTextView = itemView.findViewById(R.id.description)
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

        memeImageV.onClick = {
            memeClickedListener?.invoke(meme.id!!)
        }
        commentBtnV.setOnClickListener {
            CommentsActivity.startWithMeme(context, meme)
        }
        posterPicV.setOnClickListener {
            val i = Intent(context, ProfileActivity::class.java)
            i.putExtra("user", meme.poster)
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
        favButton.setOnClickListener {
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
        this.addView(itemView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        this.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        this.setBackgroundColor(Color.RED)

        memeShare.setOnClickListener { onShare() }

        memeDescription.apply {
            onLinkClicked = generateTextLinkActions(context)
        }
    }

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
                connect(it, ConstraintSet.BOTTOM, R.id.react_button, ConstraintSet.BOTTOM)
                connect(it, ConstraintSet.START, R.id.react_button, ConstraintSet.START)
                connect(it, ConstraintSet.END, R.id.react_button, ConstraintSet.END)
                connect(it, ConstraintSet.TOP, R.id.react_button, ConstraintSet.TOP)

                constrainWidth(it, 1)
                constrainHeight(it, 1)
            }
            setVisibility(R.id.tint, View.GONE)
        }
        if (animate) TransitionManager.beginDelayedTransition(itemView, makeTransition())
        constraintSetDefault.applyTo(itemView)
    }

    private fun makeTransition(): TransitionSet {
        return TransitionSet().apply {
            reactionArray.mapIndexed { index, i ->
                ChangeBounds().apply {
                    addTarget(i)
                    startDelay = index * 50L
                    interpolator = AccelerateDecelerateInterpolator()
                }
            }.forEach { addTransition(it) }
            addTransition(Fade(Fade.IN).apply {
                addTarget(R.id.tint)
            })
            addTransition(Fade(Fade.OUT).apply {
                addTarget(R.id.tint)
            })
            duration = 200L
        }
    }

    private fun showReaction() {
        constraintSetDefault.apply {
            reactionArray.forEach {
                clear(it)
                connect(it, ConstraintSet.BOTTOM, R.id.meme_image, ConstraintSet.BOTTOM, 32.dp(context))
                val s = 42.dp(context)
                constrainWidth(it, s)
                constrainHeight(it, s)
            }
            createHorizontalChain(ConstraintSet.PARENT_ID, ConstraintSet.LEFT,
                    ConstraintSet.PARENT_ID, ConstraintSet.RIGHT,
                    reactionArray,
                    null,
                    ConstraintSet.CHAIN_SPREAD)
            setVisibility(R.id.tint, View.VISIBLE)
        }
        TransitionManager.beginDelayedTransition(itemView, makeTransition())
        constraintSetDefault.applyTo(itemView)
    }

    private fun onShare() {
        val req = ImageRequestBuilder.newBuilderWithSource(Uri.parse(meme.generateUrl()))
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.BITMAP_MEMORY_CACHE)
                .build()
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val res = withContext(Dispatchers.Default) { Fresco.getImagePipeline().fetchDecodedImage(req, context).result }
            if (res != null && res.get() is CloseableBitmap) {
                val tf = TypefaceManager.byName("Ubuntu")
                val bitmap = withContext(Dispatchers.Default) {
                    (res.get() as CloseableBitmap)
                            .underlyingBitmap
                            .addWaterMark(tf)
                }

                val dir = File(context.cacheDir, "/share")
                val file = File(dir, "${meme.id}.jpg")
                if (!file.exists()) {
                    dir.mkdirs()
                    file.createNewFile()
                    withContext(Dispatchers.Default) {
                        val fos = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                        fos.flush()
                        fos.close()
                    }
                }
                val fileUri = FileProvider.getUriForFile(
                        context,
                        "com.innov8.memeit.fileprovider",
                        file)
                val intent = ShareCompat.IntentBuilder.from(context as Activity)
                        .setSubject("${MemeItApp.SERVER_DOMAIN}/share/${meme.id}")
                        .setStream(fileUri)
                        .setType("image/*")
                        .createChooserIntent()
                        .apply {
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                context.startActivity(intent)
            } else {
                context.toast("Image not downloaded yet")
            }
        }

    }

    private fun saveToGallery() {
        val req = ImageRequestBuilder.newBuilderWithSource(Uri.parse(meme.generateUrl()))
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.BITMAP_MEMORY_CACHE)
                .build()

        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val res = withContext(Dispatchers.Default) { Fresco.getImagePipeline().fetchDecodedImage(req, context).result }
            if (res != null && res.get() is CloseableBitmap) {
                val tf = TypefaceManager.byName("Ubuntu")
                val bitmap = withContext(Dispatchers.Default) {
                    (res.get() as CloseableBitmap)
                            .underlyingBitmap
                            .addWaterMark(tf)
                }
                val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MemeIt")
                dir.mkdirs()
                val file = File(dir, "${meme.id}.jpg")
                if (!file.exists()) {
                    dir.mkdirs()
                    file.createNewFile()
                    withContext(Dispatchers.Default) {
                        val fos = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                        fos.flush()
                        fos.close()
                    }
                    context?.addFileToMediaStore(file)
                }
                context.toast("Image saved to Gallery!")

            } else {
                context.toast("Image not downloaded yet")
            }
        }
    }


    private val reqCode = 154
    private fun onSave() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val wes = ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

            if (wes) {
                saveToGallery()
            } else {
                (context as Activity).requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), reqCode)
            }
        } else saveToGallery()

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
                if (m == meme) reactionCountV.text = String.format("%d reactions", m.reactionCount)

            }
            m.myReaction = reactionType.create()
            if (m == meme) reactButton.setActiveImage(m.myReaction!!.getDrawableID())
        }, onError("Reaction Failed"))
    }

    private fun reportMeme(message: String) {
        MemeItMemes.reportMeme(Report.MemeReport(meme.id!!, message)).call({ _ ->
            context.toast("Meme reported! We will look into it!")
        }) {
            snack("Reporting Failed",
                    "Try Again",
                    { reportMeme(message) }
            )
        }
    }

    private fun showMemeMenu() {
        val menu = PopupMenu(context, memeMenu)
        if (MemeItClient.myUser?.id == meme.poster!!.uid)
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
                R.id.menu_save_meme -> onSave()
                R.id.menu_report_meme -> {
                    val rt = Report.ReportTypes.values()
                    MaterialDialog.Builder(context)
                            .title("Report")
                            .items("Pornography", "Abuse", "Violence", "Not appropriate")
                            .itemsCallbackMultiChoice(null) { _, _, _ ->
                                true
                            }
                            .positiveText("Report")
                            .negativeText("Cancel")
                            .onPositive { dialog, _ ->
                                dialog.selectedIndices?.filter { it != 4 }
                                        ?.joinToString(", ") { rt[it].message }
                                        ?.let {
                                            if (it.isNotBlank()) reportMeme(it)
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
        reactionCountV.text = String.format("%d reactions", meme.reactionCount)
        commentCountV.text = meme.commentCount.toString()
        posterPicV.loadImage(meme.poster?.imageUrl)
        memeDateV.text = meme.date?.formateAsDate()

        if (meme.myReaction !== null) {
            reactButton.setActiveImage(meme.myReaction!!.getDrawableID())
            reactButton.isChecked = true
        } else {
            reactButton.isChecked = false
        }
        favButton.isChecked = meme.isMyFavourite
        if (resizeToFit) {
            val h = (screenWidth / meme.imageRatio).toInt().trim(200.dp, screenHeightOriented - 112.dp)
            constraintSetDefault.constrainHeight(R.id.meme_image, h)
            constraintSetDefault.applyTo(itemView)
        }
        applyVisible(R.id.meme_gif, if (meme.getType() == Meme.MemeType.GIF) View.VISIBLE else View.GONE)
        applyVisible(R.id.description, if (meme.description.isNullOrBlank()) View.GONE else View.VISIBLE)
        constraintSetDefault.applyTo(itemView)
        if (!meme.description.isNullOrBlank()) {
            memeDescription.text = meme.description!!

        }
        memeImageV.loadMeme(meme)
    }

    private fun applyVisible(id: Int, visibility: Int, apply: Boolean = false) {
        constraintSetDefault.apply {
            setVisibility(id, visibility)
            if (apply) applyTo(itemView)
        }
    }
}