package com.innov8.memegenerator

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import com.innov8.memegenerator.Fragments.*
import com.innov8.memegenerator.MemeEngine.*
import com.innov8.memegenerator.utils.origin
import com.innov8.memeit.commons.dp
import com.innov8.memeit.commons.loadBitmapfromStream
import com.innov8.memeit.commons.makeFullScreen
import com.innov8.memeit.commons.models.MemeTemplate
import com.innov8.memeit.commons.models.MyTypeFace
import com.innov8.memeit.commons.models.TextStyleProperty
import com.memeit.backend.dataclasses.Meme
import com.waynejo.androidndkgif.GifDecoder
import kotlinx.android.synthetic.main.meme_editor.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import java.io.File
import java.util.*

class MemeEditorActivity : AppCompatActivity(), ItemSelectedInterface {


    private val constraintSet1 = ConstraintSet()
    private val opened
        get() = closeableFragments[current] != null

    private lateinit var closeableFragments: Map<String, CloseableFragment>
    var current = "none"

    var type: Meme.MemeType = Meme.MemeType.IMAGE
    var path: String? = null

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

    private inline fun <T : Fragment> T.make(title: String, size: Int, block: T.() -> Unit) =
            title to CloseableFragment(this.apply(block), size.dp(this@MemeEditorActivity))

    private fun initListeners() {
        memeEditorView.itemSelectedInterface = this
        memeEditorView.paintHandler.actionManager.onActionListChanged = {
            (closeableFragments["paint"]?.bottomFragment as? PaintOptionsFragment)?.updateUndoState()
        }
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
            if (type == Meme.MemeType.IMAGE) {
                val bitmap = memeEditorView.captureMeme()
                val intent = Intent(this, MemePosterActivity::class.java)
                intent.putExtra("texts", memeEditorView.getTexts().toTypedArray())
                MemePosterActivity.bitmap = bitmap
                startActivity(intent)
            } else {
                val memeLayout = (memeEditorView.memeLayout as? SingleImageLayout)!!
                val gifi = GifInfo(intent.getStringExtra(GIF_PATH), memeLayout.getDrawingRectRelAt(0))

                val da = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MemeIt/")
                da.mkdirs()
                val file = File(da, "meme${Random().nextInt(100) + 100}.gif")
                file.createNewFile()
                launch(UI) {
                    val pd = MaterialDialog.Builder(this@MemeEditorActivity)
                            .title("Please wait a while")
                            .content("Processing Gif")
                            .progress(true, 0).build()
                    pd.show()
                    val overlay = memeEditorView.captureItems()
                    withContext(CommonPool) {
                        compileGifMeme(gifi, overlay,
                                memeLayout.drawingRect.origin(),
                                memeEditorView.paint,
                                file.absolutePath)
                    }
                    pd.dismiss()
                    startActivity(Intent(this@MemeEditorActivity, MemePosterActivity::class.java).apply {
                        putExtra("gif", file.absolutePath)
                    })
                }
            }
        }

    }

    private fun handleIntent() {
        val mode = intent.getIntExtra(MODE, -1)
        val result = when (mode) {
            MODE_TEMPLATE -> handleTemplateIntent()
            MODE_SINGLE_IMAGE -> handleImageIntent()
            MODE_MULTI_IMAGE -> handleImagesIntent()
            MODE_GIF_IMAGE -> handleGifIntent()
            else -> false
        }
        if (!result) {
            setResult(-1, null)
            finish()
        }
    }

    private fun handleTemplateIntent(): Boolean {
        val template = intent.getParcelableExtra<MemeTemplate?>(TEMPLATE)
        return if (template != null) {
            memeEditorView.loadMemeTemplate(template)
            true
        } else false
    }

    private fun handleImageIntent(): Boolean {
        val path = intent.getStringExtra(SINGLE_PATH) ?: null
        return if (path != null) {
            launch(UI) {
                withContext(CommonPool) {
                    contentResolver.openInputStream(Uri.parse(path))?.let {
                        this@MemeEditorActivity.loadBitmapfromStream(it, 400, 800)
                    }
                }?.let {
                    memeEditorView.loadBitmab(it)
                }
            }
            true
        } else false
    }

    private fun handleImagesIntent(): Boolean {
        val paths = intent.getStringArrayExtra(MULTI_PATH) ?: null
        val lInfo: MemeLayout.LayoutInfo? = intent.getParcelableExtra(MULTI_LAYOUT)


        return if (paths != null && lInfo != null) {
            val s = 400
            val sh = 800
            val ps = paths.size

            val reqW = when (lInfo.type) {
                MemeLayout.LayoutInfo.TYPE_LINEAR -> if (lInfo.orientation == 0) s / ps else s / 2
                MemeLayout.LayoutInfo.TYPE_GRID -> if (lInfo.orientation == 0) s / lInfo.span else s / (ps / lInfo.span)
                else -> s
            }
            val reqH = when (lInfo.type) {
                MemeLayout.LayoutInfo.TYPE_LINEAR -> if (lInfo.orientation == 0) sh / 2 else sh / ps
                MemeLayout.LayoutInfo.TYPE_GRID -> if (lInfo.orientation == 0) sh / (ps / lInfo.span) else sh / lInfo.span
                else -> sh
            }
            launch(UI) {
                withContext(CommonPool) {
                    try {
                        paths.map { url ->
                            contentResolver.openInputStream(Uri.parse(url))?.let {
                                this@MemeEditorActivity.loadBitmapfromStream(it, reqW, reqH)
                            }
                        }
                    } catch (e: Exception) {
                        null
                    }
                }?.let { bitmaps ->
                    val x = mutableListOf<Bitmap>()
                    bitmaps.filter { it != null }.forEach { x.add(it!!) }
                    memeEditorView.setLayout(lInfo.create(
                            memeEditorView.width,
                            memeEditorView.height,
                            x
                    ))

                }
            }
            true
        } else false
    }

    private fun handleGifIntent(): Boolean {
        val path = intent.getStringExtra(GIF_PATH) ?: null
        return if (path != null) {
            launch(UI) {
                val b = withContext(CommonPool) {
                    val x = GifDecoder()
                    val i = x.loadUsingIterator(path)
                    if (i.hasNext())
                        i.next().bitmap
                    else null
                }
                b?.let {
                    type = Meme.MemeType.GIF
                    memeEditorView.loadBitmab(it)
                }
            }
            true
        } else false
    }

    //==============================================================================================
    private class CloseableFragment(val bottomFragment: Fragment? = null, val bottomSize: Int = 0,
                                    val topFragment: Fragment? = null, val topSize: Int = 0)

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
    }

    private fun close() {
        TransitionManager.beginDelayedTransition(contraint_layout)
        constraintSet1.applyTo(contraint_layout)
        current = "none"
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
            super.onBackPressed()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        open(savedInstanceState?.getString("current") ?: "none")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("current", current)
    }

    override fun onTextItemSelected(textStyleProperty: TextStyleProperty) {
        (closeableFragments["text"]?.bottomFragment as? TextEditorFragment)?.textStyleProperty = textStyleProperty
    }

    companion object {
        private const val MODE_TEMPLATE = 0
        private const val MODE_SINGLE_IMAGE = 1
        private const val MODE_MULTI_IMAGE = 2
        private const val MODE_GIF_IMAGE = 3

        private const val MODE = "mode"
        private const val TEMPLATE = "template"
        private const val SINGLE_PATH = "single path"
        private const val MULTI_PATH = "multi path"
        private const val MULTI_LAYOUT = "multi layout"
        private const val GIF_PATH = "gif path"
        fun startWithMemeTemplate(context: Context, template: MemeTemplate) {
            startThis(context) {
                putExtra(MODE, MODE_TEMPLATE)
                putExtra(TEMPLATE, template)
            }
        }

        fun startWithGif(context: Context, path: String) {
            startThis(context) {
                putExtra(MODE, MODE_GIF_IMAGE)
                putExtra(GIF_PATH, path)
            }
        }

        fun startWithImage(context: Context, path: String) {
            startThis(context) {
                putExtra(MODE, MODE_SINGLE_IMAGE)
                putExtra(SINGLE_PATH, path)
            }
        }

        fun startWithImages(context: Context, paths: List<String>, layoutInfo: MemeLayout.LayoutInfo) {
            startThis(context) {
                putExtra(MODE, MODE_MULTI_IMAGE)
                putExtra(MULTI_PATH, paths.toTypedArray())
                putExtra(MULTI_LAYOUT, layoutInfo)
            }
        }

        private inline fun startThis(context: Context, applyToIntent: Intent.() -> Unit) =
                context.startActivity(Intent(context, MemeEditorActivity::class.java).apply(applyToIntent))

    }
}

class EditorHandler(val memeEditorView: MemeEditorView) :
        TextEditListener,
        LayoutEditInterface,
        StickerEditInterface,
        PaintEditInterface {


    override fun onAddText(memeTextView: MemeTextView) {
        memeEditorView.addMemeItemView(memeTextView)
    }

    override fun onApplyAll(textStyleProperty: TextStyleProperty, applySize: Boolean) {
        (memeEditorView.focusedItem as MemeTextView?)?.applyTextStyleProperty(textStyleProperty, applySize)
    }

    override fun onTextColorChanged(color: Int) {
        (memeEditorView.focusedItem as MemeTextView?)?.setTextColor(color)
    }

    override fun onTextFontChanged(typeface: MyTypeFace) {
        (memeEditorView.focusedItem as MemeTextView?)?.setTypeface(typeface)
    }

    override fun onTextSetBold(bold: Boolean) {

    }

    override fun onTextSetItalic(italic: Boolean) {

    }

    override fun onTextSetAllCap(allCap: Boolean) {
        (memeEditorView.focusedItem as? MemeTextView)?.setAllCaps(allCap)
    }

    override fun onTextSetStroked(stroked: Boolean) {
        (memeEditorView.focusedItem as? MemeTextView)?.setStroke(stroked)
    }

    override fun onTextStrokeChanged(strokeSize: Float) {
        (memeEditorView.focusedItem as? MemeTextView)?.setStrokeWidth(strokeSize)
    }

    override fun onTextStrokrColorChanged(strokeColor: Int) {
        (memeEditorView.focusedItem as? MemeTextView)?.setStrokeColor(strokeColor)
    }

    override fun onTextSizeChanged(size: Float) {
        (memeEditorView.focusedItem as? MemeTextView)?.setTextSize(size)
    }

    //==============================================================================================

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

