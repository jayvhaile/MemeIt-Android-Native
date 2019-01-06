package com.innov8.memegenerator

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.text.Layout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import com.innov8.memegenerator.fragments.*
import com.innov8.memegenerator.interfaces.*
import com.innov8.memegenerator.memeEngine.*
import com.innov8.memegenerator.utils.*
import com.innov8.memegenerator.workers.startTemplateDownload
import com.innov8.memeit.commons.dp
import com.innov8.memeit.commons.loadBitmapfromStream
import com.innov8.memeit.commons.makeFullScreen
import com.innov8.memeit.commons.toast
import com.memeit.backend.models.*
import com.waynejo.androidndkgif.GifDecoder
import kotlinx.android.synthetic.main.meme_editor.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import java.io.File
import java.util.*

class MemeEditorActivity : AppCompatActivity(), ItemSelectedInterface, EditorStateChangedListener {

    private val constraintSet1 = ConstraintSet()
    private val opened
        get() = closeableFragments[current] != null

    private lateinit var closeableFragments: Map<String, CloseableFragment>
    private var current = "none"
    private var type: Meme.MemeType = Meme.MemeType.IMAGE
    private var templateID: String? = null

    private val onEditorStateChangedListeners = mutableListOf<EditorStateChangedListener>()
    private lateinit var interactionHandler: EditorHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.meme_editor)

        interactionHandler = EditorHandler(memeEditorView)

        constraintSet1.clone(contraint_layout)
        closeableFragments = mapOf(
                LayoutEditorFragment().make("layout", 152) { layoutEditListener = interactionHandler },
                StickerChooserFragment().make("sticker", 80 + 56) { stickerEditInterface = interactionHandler },
                PaintOptionsFragment().make("paint", 128) { paintEditInterface = interactionHandler },
                "text" to CloseableFragment(TextEditorFragment()
                        .apply { textEditListener = interactionHandler }, 152.dp(this),
                        TextPresetFragment().apply {
                            textEditListener = interactionHandler
                        }, 80.dp(this))

        )
        initListeners()
        handleIntent()
    }


    private fun initListeners() {

        memeEditorView.itemSelectedInterface = this
        memeEditorView.paintHandler.actionManager.onActionListChanged = {
            (closeableFragments["paint"]?.bottomFragment as? PaintOptionsFragment)?.updateUndoState()
        }
        onEditorStateChangedListeners.add(memeEditorView)
        onEditorStateChangedListeners.add(this)
        layout.setOnClickListener {
            open("layout")
        }
        text.setOnClickListener {
            open("text")
        }
        sticker.setOnClickListener {
            open("sticker")
        }
        paint.setOnClickListener {
            open("paint")
        }

        back.setOnClickListener {
            onBackPressed()
        }
        done.setOnClickListener {
            onDone()
        }
        done.setOnLongClickListener {
            onDoneTemplate()
            true
        }
        add_text.setOnClickListener {
            memeEditorView.addMemeItemView(MemeTextItem(this, 400, 150))
        }

    }

    private fun handleIntent() {
        val result = when (mode) {
            MODE_DRAFT -> handleDraftIntent()
            MODE_TEMPLATE -> handleTemplateIntent()
            MODE_SINGLE_IMAGE -> handleImageIntent()
            MODE_MULTI_IMAGE -> handleImagesIntent()
            MODE_GIF_IMAGE -> handleGifIntent()
            MODE_VIDEO -> handleVideoIntent()
            else -> false
        }
        if (!result) {
            toast("Image not found")
            finish()
        }
    }

    private fun handleTemplateIntent(): Boolean {
        val json = intent.getStringExtra(PARAM_TEMPLATE)
        val mt = MemeTemplate.readFromString(json)
        return mt._id?.let { id ->
            templateID = id
            if (!loadSavedTemplate(mt._id!!)) {
                val dialog = MaterialDialog.Builder(this)
                        .title("Downloading Template...")
                        .progress(true, 100)
                        .build()
                dialog.show()
                startTemplateDownload(this, mt, { savedTemplate ->
                    GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                        savedTemplate.memeTemplateProperty.let {
                            val loaded = withContext(Dispatchers.Default) {
                                it.loadImages(this@MemeEditorActivity)
                            }
                            memeEditorView.applyProperty(loaded)
                            layoutFrag?.memeLayout = memeEditorView.memeLayout!!
                            dialog.dismiss()
                        }
                    }
                }) {
                    dialog.dismiss()
                    toast(it)
                    finish()
                }

            }
            true
        } ?: false
    }

    private fun loadSavedTemplate(id: String): Boolean {
        val file = File(MemeTemplate.getSavedJsonDir(this), "$id.json")
        return if (file.exists()) {
            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                val result = withContext(Dispatchers.Default) {
                    MemeTemplate.readFromFile(file)
                }
                result?.memeTemplateProperty?.let {
                    val loaded = withContext(Dispatchers.Default) {
                        it.loadImages(this@MemeEditorActivity)
                    }
                    memeEditorView.applyProperty(loaded)
                    layoutFrag?.memeLayout = memeEditorView.memeLayout!!
                }
            }
            true
        } else false
    }

    private fun handleDraftIntent(): Boolean {
        val path = intent.getStringExtra(PARAM_DRAFT_PATH)
        val file = File(path)
        return if (file.exists()) {
            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                val result = withContext(Dispatchers.Default) {
                    SavedMemeTemplateProperty.readFromFile(file)
                }
                result?.let {
                    val loaded = withContext(Dispatchers.Default) {
                        it.loadImages(this@MemeEditorActivity)
                    }
                    memeEditorView.applyProperty(loaded)
                    layoutFrag?.memeLayout = memeEditorView.memeLayout!!
                }
            }
            true
        } else false
    }

    private fun handleImageIntent(): Boolean {
        val path = intent.getStringExtra(PARAM_SINGLE_PATH) ?: null
        return if (path != null) {
            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                withContext(Dispatchers.Default) {
                    contentResolver.openInputStream(Uri.parse(path))?.let {
                        this@MemeEditorActivity.loadBitmapfromStream(it, 400, 800)
                    }
                }?.let {
                    memeEditorView.loadBitmab(it)
                    layoutFrag?.memeLayout = memeEditorView.memeLayout!!
                }
            }
            true
        } else false
    }

    private fun handleImagesIntent(): Boolean {
        val paths = intent.getStringArrayExtra(PARAM_MULTI_PATH) ?: null
        val lp: LayoutProperty = intent.getParcelableExtra(PARAM_MULTI_LAYOUT)


        return if (paths != null) {
            val (w, h) = calcReqSize(lp, paths.size)
            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                withContext(Dispatchers.Default) {
                    try {
                        paths.map { url ->
                            contentResolver.openInputStream(Uri.parse(url))?.let {
                                this@MemeEditorActivity.loadBitmapfromStream(it, w, h)
                            }
                        }
                    } catch (e: Exception) {
                        null
                    }
                }?.let { bitmaps ->
                    val x = mutableListOf<Bitmap>()
                    bitmaps.filter { it != null }.forEach { x.add(it!!) }
                    memeEditorView.setLayout(MemeLayout.fromProperty(x, lp))
                    layoutFrag?.memeLayout = memeEditorView.memeLayout!!

                }
            }
            true
        } else false
    }

    private fun handleGifIntent(): Boolean {
        val path = intent.getStringExtra(PARAM_GIF_PATH) ?: null
        return if (path != null) {
            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {

                val b = withContext(Dispatchers.Default) {
                    val x = GifDecoder()
                    val i = x.loadUsingIterator(path)
                    if (i.hasNext())
                        i.next().bitmap
                    else null
                }
                b?.let {
                    type = Meme.MemeType.GIF
                    memeEditorView.loadBitmab(it)
                    layoutFrag?.memeLayout = memeEditorView.memeLayout!!
                }
            }
            true
        } else false
    }

    private fun handleVideoIntent(): Boolean {
        val path = intent.getStringExtra(PARAM_VIDEO_PATH) ?: null
        return if (path != null) {
            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {


                val b = withContext(Dispatchers.Default) {
                    val ret = MediaMetadataRetriever()
                    ret.setDataSource(path)
                    ret.getFrameAtTime(1)

                }
                b?.let {
                    type = Meme.MemeType.GIF
                    memeEditorView.loadBitmab(it)
                    layoutFrag?.memeLayout = memeEditorView.memeLayout!!
                }
            }
            true
        } else false
    }


    private fun onDone() {
        when (mode) {
            MODE_DRAFT, MODE_TEMPLATE, MODE_SINGLE_IMAGE, MODE_MULTI_IMAGE -> {
                val bitmap = memeEditorView.captureMeme()
                GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                    val path = withContext(Dispatchers.Default) {
                        bitmap.save(getTempMemeUploadDir(this@MemeEditorActivity), max = 1000, tag = "meme")
                    }
                    startActivity(Intent(this@MemeEditorActivity, Class.forName("com.innov8.memeit.activities.MemePosterActivity")).apply {
                        putExtra("image", path)
                        putExtra("texts", memeEditorView.getTexts().toTypedArray())
                        putExtra("tid", templateID)
                    })
                }
            }
            MODE_GIF_IMAGE -> {
                val memeLayout = (memeEditorView.memeLayout as? SingleImageLayout)!!
                val file = File(filesDir, "${UUID.randomUUID()}_meme.webp")
                GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                    val pd = MaterialDialog.Builder(this@MemeEditorActivity)
                            .title("Please wait a while")
                            .content("Processing Gif")
                            .progress(true, 0)
                            .build()
                    pd.show()
                    val overlay = memeEditorView.captureItems()
                    withContext(Dispatchers.Default) {
                        compileGifMeme(GifInfo(intent.getStringExtra(PARAM_GIF_PATH),
                                overlay,
                                memeLayout.getMarginRect(),
                                memeEditorView.paint,
                                file.absolutePath)
                        )
                    }
                    pd.dismiss()
                    startActivity(Intent(this@MemeEditorActivity, Class.forName("com.innov8.memeit.activities.MemePosterActivity")).apply {
                        putExtra("gif", file.absolutePath)
                        putExtra("texts", memeEditorView.getTexts().toTypedArray())
                    })
                }
            }
            MODE_VIDEO -> {
                /*val memeLayout = (memeEditorView.memeLayout as? SingleImageLayout)!!
                val file = File(filesDir, "${UUID.randomUUID()}_meme.webp")
                GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                    val pd = MaterialDialog.Builder(this@MemeEditorActivity)
                            .title("Please wait a while")
                            .content("Processing Gif")
                            .progress(true, 0)
                            .build()
                    pd.show()
                    val overlay = memeEditorView.captureItems()
                    withContext(Dispatchers.Default) {
                        mp4ToWebp(intent.getStringExtra(PARAM_VIDEO_PATH), overlay,
                                memeLayout.getMarginRect(),
                                memeEditorView.paint,
                                file.absolutePath)
                    }
                    pd.dismiss()
                    startActivity(Intent(this@MemeEditorActivity, Class.forName("com.innov8.memeit.activities.MemePosterActivity")).apply {
                        putExtra("gif", file.absolutePath)
                        putExtra("texts", memeEditorView.getTexts().toTypedArray())
                    })
                }*/
            }
        }
    }

    private fun onDoneTemplate() {
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val p = memeEditorView.generateProperty()
            val check = p
                    .memeItemsProperty
                    .filterIsInstance(MemeStickerItemProperty::class.java)
                    .map { it.sticker }
                    .any { it is UserSticker }

            if (check) {
                toast("Templates cannot contain custom made stickers")
                return@launch
            }
            val result = withContext(Dispatchers.Default) {
                p.saveImages(MemeTemplate.getTempUploadDir(this@MemeEditorActivity))
                        .saveToString()
            }
            startActivity(Intent(this@MemeEditorActivity, Class.forName("com.innov8.memeit.activities.MemeTemplatePosterActivity")).apply {
                putExtra("json", result)
            })
        }

    }

    private fun saveDraft(onFinished: () -> Unit) {
        val p = memeEditorView.generateProperty()
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            //todo don't re-save the images if it is started with draft mode
            val file = File(MemeTemplate.getDraftsJsonDir(this@MemeEditorActivity), "${UUID.randomUUID()}.json")
            file.createNewFile()

            val sp = withContext(Dispatchers.Default) { p.saveImages(MemeTemplate.getDraftsDir(this@MemeEditorActivity)) }

            withContext(Dispatchers.Default) {
                sp.saveToFile(file)
            }
            memeEditorView.clearMemeItems()
            memeEditorView.setLayout(null)
            onFinished()
        }
    }


    val mode by lazy {
        intent.getIntExtra(PARAM_MODE, -1)
    }

    private val layoutFrag: LayoutEditorFragment?
        get() {
            return (closeableFragments["layout"]?.bottomFragment as? LayoutEditorFragment)
        }


    private inline fun <T : Fragment> T.make(title: String, size: Int, block: T.() -> Unit) =
            title to CloseableFragment(this.apply(block), size.dp(this@MemeEditorActivity))


    private fun open(tag: String) {
        val cf = closeableFragments[tag]
        if (cf == null) {
            current = "none"
            return
        }

        val constraintSetOpened = ConstraintSet()
        constraintSetOpened.clone(contraint_layout)
        val transaction = supportFragmentManager.beginTransaction()

        if (cf.topFragment != null) {
            constraintSetOpened.setGuidelineBegin(R.id.guideline_top, 0)
            constraintSetOpened.constrainHeight(R.id.top_overlay, cf.topSize)
            constraintSetOpened.clear(R.id.top_overlay, ConstraintSet.BOTTOM)
            transaction.replace(R.id.top_overlay, cf.topFragment)
        }
        if (cf.bottomFragment != null) {
            constraintSetOpened.setGuidelineEnd(R.id.guideline_bottom, 0)
            constraintSetOpened.constrainHeight(R.id.bottom_overlay, cf.bottomSize)
            constraintSetOpened.clear(R.id.bottom_overlay, ConstraintSet.TOP)
            transaction.replace(R.id.bottom_overlay, cf.bottomFragment)
        }
        if (!opened) {
            TransitionManager.beginDelayedTransition(contraint_layout)
            constraintSetOpened.applyTo(contraint_layout)

        }
        transaction.commit()
        current = tag

        onEditorStateChangedListeners.forEach { it.onEditorOpened(tag, cf) }
    }

    private fun close() {
        TransitionManager.beginDelayedTransition(contraint_layout)
        constraintSet1.applyTo(contraint_layout)
        current = "none"
        onEditorStateChangedListeners.forEach { it.onEditorClosed() }

    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) makeFullScreen()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        makeFullScreen()
    }

    override fun onBackPressed() {
        if (opened) close()
        else
            MaterialDialog.Builder(this)
                    .title("Save as Draft?")
                    .content("Drafts let you save your edits,so you can come back later.")
                    .positiveText("Save Draft")
                    .negativeText("Cancel")
                    .onPositive { _, _ ->
                        saveDraft {
                            super.onBackPressed()
                        }
                    }
                    .onNegative { _, _ ->
                        super.onBackPressed()
                    }
                    .show()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        open(savedInstanceState?.getString("current") ?: "none")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("current", current)
    }

    override fun onTextItemSelected(textStyleProperty: MemeTextStyleProperty) {
        (closeableFragments["text"]?.bottomFragment as? TextEditorFragment)?.textStyleProperty = textStyleProperty
    }

    override fun onEditorOpened(tag: String, cf: CloseableFragment) {
        if (tag == "layout")
            memeEditorView.memeLayout?.generateProperty()?.let {
                layoutFrag?.applyLayoutProperty(it)
            }

    }

    override fun onEditorClosed() {

    }

    companion object {
        private const val MODE_DRAFT = 0
        private const val MODE_TEMPLATE = 1
        private const val MODE_SINGLE_IMAGE = 2
        private const val MODE_MULTI_IMAGE = 3
        private const val MODE_GIF_IMAGE = 4
        private const val MODE_VIDEO = 5

        private const val PARAM_MODE = "mode"
        private const val PARAM_TEMPLATE = "template"
        private const val PARAM_DRAFT_PATH = "draft_path"
        private const val PARAM_SINGLE_PATH = "single path"
        private const val PARAM_MULTI_PATH = "multi path"
        private const val PARAM_MULTI_LAYOUT = "multi layout"
        private const val PARAM_GIF_PATH = "gif path"
        private const val PARAM_VIDEO_PATH = "video path"

        fun startWithTemplate(context: Activity, memeTemplate: MemeTemplate) {
            startThis(context) {
                putExtra(PARAM_MODE, MODE_TEMPLATE)
                putExtra(PARAM_TEMPLATE, memeTemplate.saveToString())
            }
        }

        fun startWithDraft(context: Activity, filepath: String) {
            startThis(context) {
                putExtra(PARAM_MODE, MODE_DRAFT)
                putExtra(PARAM_DRAFT_PATH, filepath)
            }
        }

        fun startWithGif(context: Activity, path: String) {
            startThis(context) {
                putExtra(PARAM_MODE, MODE_GIF_IMAGE)
                putExtra(PARAM_GIF_PATH, path)
            }
        }

        fun startWithVideo(context: Activity, path: String) {
            startThis(context) {
                putExtra(PARAM_MODE, MODE_VIDEO)
                putExtra(PARAM_VIDEO_PATH, path)
            }
        }

        fun startWithImage(context: Activity, path: String) {
            startThis(context) {
                putExtra(PARAM_MODE, MODE_SINGLE_IMAGE)
                putExtra(PARAM_SINGLE_PATH, path)
            }
        }

        fun startWithImages(context: Activity, paths: List<String>, layoutProperty: LayoutProperty) {
            startThis(context) {
                putExtra(PARAM_MODE, MODE_MULTI_IMAGE)
                putExtra(PARAM_MULTI_PATH, paths.toTypedArray())
                putExtra(PARAM_MULTI_LAYOUT, layoutProperty)
            }
        }

        private inline fun startThis(context: Activity, applyToIntent: Intent.() -> Unit) =
                context.startActivity(Intent(context, MemeEditorActivity::class.java).apply(applyToIntent))

    }
}

class EditorHandler(private val memeEditorView: MemeEditorView) :
        TextEditListener,
        LayoutEditInterface,
        StickerEditInterface,
        PaintEditInterface {


    override fun onAddText(memeTextItem: MemeTextItem) {
        memeEditorView.addMemeItemView(memeTextItem)
    }

    override fun onApplyAll(textStyleProperty: MemeTextStyleProperty, applySize: Boolean) {
        (memeEditorView.focusedItem as? MemeTextItem)?.applyTextStyleProperty(textStyleProperty, applySize)
    }

    override fun onTextColorChanged(color: Int) {
        (memeEditorView.focusedItem as? MemeTextItem)?.setTextColor(color)
    }

    override fun onTextFontChanged(font: String) {
        (memeEditorView.focusedItem as? MemeTextItem)
                ?.setTypeface(font)
    }

    override fun onTextSetBold(bold: Boolean) {
        (memeEditorView.focusedItem as? MemeTextItem)?.setBold(bold)
    }

    override fun onTextSetItalic(italic: Boolean) {
        (memeEditorView.focusedItem as? MemeTextItem)?.setItalic(italic)
    }

    override fun onTextSetAllCap(allCap: Boolean) {
        (memeEditorView.focusedItem as? MemeTextItem)?.setAllCaps(allCap)
    }

    override fun onTextSetStroked(stroked: Boolean) {
        (memeEditorView.focusedItem as? MemeTextItem)?.setStroke(stroked)
    }

    override fun onTextStrokeChanged(strokeSize: Float) {
        (memeEditorView.focusedItem as? MemeTextItem)?.setStrokeWidth(strokeSize)
    }

    override fun onTextStrokrColorChanged(strokeColor: Int) {
        (memeEditorView.focusedItem as? MemeTextItem)?.setStrokeColor(strokeColor)
    }

    override fun onTextSizeChanged(size: Float) {
        (memeEditorView.focusedItem as? MemeTextItem)?.setTextSize(size)
    }

    override fun onTextBgColorChanged(bgColor: Int) {
        (memeEditorView.focusedItem as? MemeTextItem)?.setBgColor(bgColor)
    }

    override fun onTextAlignmentChanged(alignment: Layout.Alignment) {
        (memeEditorView.focusedItem as? MemeTextItem)?.setAlignment(alignment)
    }
    //==============================================================================================

    override fun onLayoutSet(memeLayout: MemeLayout) {
        memeEditorView.setLayout(memeLayout.copy().apply {
            backgroudColor = memeEditorView.memeLayout?.backgroudColor ?: Color.WHITE
        })
    }

    override fun onAllMarginSet(left: Int, top: Int, right: Int, bottom: Int) {
        memeEditorView.memeLayout?.setMargin(left, top, right, bottom)
    }

    override fun onLeftMargin(size: Int) {
        memeEditorView.memeLayout?.leftMargin = size
    }

    override fun onRightMargin(size: Int) {
        memeEditorView.memeLayout?.rightMargin = size
    }

    override fun onTopMargin(size: Int) {
        memeEditorView.memeLayout?.topMargin = size
    }

    override fun onBottomMargin(size: Int) {
        memeEditorView.memeLayout?.bottomMargin = size
    }

    override fun onVertivalSpacing(size: Int) {
        val ml = memeEditorView.memeLayout
        when (ml) {
            is LinearImageLayout -> {
                if (ml.orientation == 1) ml.spacing = size
            }
            is GridImageLayout -> ml.vSpacing = size
        }
    }

    override fun onHorizontalSpacing(size: Int) {
        val ml = memeEditorView.memeLayout
        when (ml) {
            is LinearImageLayout -> {
                if (ml.orientation == 0) ml.spacing = size
            }
            is GridImageLayout -> ml.hSpacing = size
        }
    }

    override fun onBackgroundColorChanged(color: Int) {
        memeEditorView.memeLayout?.backgroudColor = color
    }

    //==============================================================================================

    override fun onAddSticker(memeStickerView: MemeStickerView) {
        memeEditorView.addMemeItemView(memeStickerView)
    }

    //==============================================================================================


    //==============================================================================================

    override fun onBrushSizeChanged(size: Float) {
        val ph = memeEditorView.paintHandler
        ph.paintProperty = ph.paintProperty.copy(brushSize = size)
    }

    override fun onBrushColorChanged(color: Int) {
        val ph = memeEditorView.paintHandler
        ph.paintProperty = ph.paintProperty.copy(color = color)
    }

    override fun onShapeChanged(paintMode: PaintHandler.PaintMode) {
        memeEditorView.paintHandler.paintMode = paintMode
    }

    override fun onPaintUndo() {
        memeEditorView.paintHandler.actionManager.undo()
    }

    override fun hasUndo(): Boolean {
        return memeEditorView.paintHandler.actionManager.head != null
    }

}

