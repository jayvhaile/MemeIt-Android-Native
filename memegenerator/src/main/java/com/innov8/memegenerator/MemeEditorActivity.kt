package com.innov8.memegenerator

import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.innov8.memegenerator.Fragments.*
import com.innov8.memegenerator.MemeEngine.*
import com.innov8.memegenerator.Models.MemeTemplate
import com.innov8.memegenerator.Models.MyTypeFace
import com.innov8.memegenerator.Models.TextStyleProperty
import com.innov8.memegenerator.utils.*
import com.memeit.backend.dataclasses.Meme
import com.waynejo.androidndkgif.GifDecoder
import kotlinx.android.synthetic.main.meme_editor.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import java.io.File
import java.util.*

class MemeEditorActivity : AppCompatActivity(), TextEditListener, LayoutEditInterface, StickerEditInterface, ItemSelectedInterface, PaintEditInterface {
    override fun onBrushSizeChanged(size: Float) {
      val ph= memeEditorView.paintHandler
       ph.paintProperty=ph.paintProperty.copy(brushSize= size)
    }

    override fun onBrushColorChanged(color: Int) {
        val ph= memeEditorView.paintHandler

        ph.paintProperty=ph.paintProperty.copy(color=color)
    }

    override fun onShapeChanged(paintMode: PaintHandler.PaintMode) {
        memeEditorView.paintHandler.paintMode=paintMode
    }

    private val constraintSet1 = ConstraintSet()
    private val opened
        get() = closeableFragments[current] != null

    private lateinit var closeableFragments: Map<String, CloseableFragment>
    var current = "none"

    var type: Meme.MemeType = Meme.MemeType.IMAGE
    var path: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.meme_editor)

        memeEditorView.itemSelectedInterface = this
        constraintSet1.clone(contraint_layout)

        closeableFragments = mapOf(
                "layout" to CloseableFragment(LayoutEditorFragment().apply { layoutEditListener = this@MemeEditorActivity }
                        , 152.dp(this)),
                "text" to CloseableFragment(TextEditorFragment().apply { textEditListener = this@MemeEditorActivity }, 130.dp(this), TextPresetFragment(), 80.dp(this)),
                "sticker" to CloseableFragment(StickerChooserFragment().apply { stickerEditInterface = this@MemeEditorActivity }
                        , (80 + 56).dp(this)),
                "paint" to CloseableFragment(PaintOptionsFragment().apply {
                    paintEditInterface = this@MemeEditorActivity
                }, 152.dp(this))
        )
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

        done.setOnClickListener {
            if (type == Meme.MemeType.IMAGE) {
                val bitmap = memeEditorView.captureMeme()
                val intent = Intent(this, MemePosterActivity::class.java)
                intent.putExtra("texts", memeEditorView.getTexts().toTypedArray())
                MemePosterActivity.bitmap = bitmap
                startActivity(intent)
            } else {
                val memeLayout = (memeEditorView.memeLayout as? SingleImageLayout)!!
                val gifi = GifInfo(path!!, memeLayout.getDrawingRectRelAt(0))

                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).mkdirs()
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                        "meme${Random().nextInt(100) + 100}.gif")
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
                }
            }
        }

        val json: String? = intent.getStringExtra("string")

        path = intent.getStringExtra("uri")

        if (json != null) {
            val gson = Gson()
            val memeTemplate = gson.fromJson(json, MemeTemplate::class.java)
            memeEditorView.loadMemeTemplate(memeTemplate)
        } else if (path != null) {
            val t = intent.getStringExtra("type") ?: "IMAGE"
            type = Meme.MemeType.of(t)

            if (type == Meme.MemeType.GIF)
                launch(UI) {
                    val b = withContext(CommonPool) {
                        val x = GifDecoder()
                        val i = x.loadUsingIterator(path)
                        if (i.hasNext())
                            i.next().bitmap
                        else null
                    }
                    b?.let {
                        memeEditorView.loadBitmab(it)
                    }
                }
            else {
                launch(UI) {
                    withContext(CommonPool) {
                        val stream = contentResolver.openInputStream(Uri.parse(path))
                        BitmapFactory.decodeStream(stream)
                    }?.let {
                        memeEditorView.loadBitmab(it)
                    }
                }
            }

        }

    }

    companion object {
        private val actions = mutableListOf<Action>()
    }


    class CloseableFragment(val bottomFragment: Fragment? = null, val bottomSize: Int = 0,
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

    //==========================================================================
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

    override fun onBackgroundColorChanged(color: Int) {
        memeEditorView.memeLayout?.backgroudColor = color
    }

    //============================================================================
    override fun onAddSticker(memeStickerView: MemeStickerView) {
        memeEditorView.addMemeItemView(memeStickerView)
    }

    override fun onTextItemSelected(textStyleProperty: TextStyleProperty) {
        log(textStyleProperty)
        (closeableFragments["text"]?.bottomFragment as? TextEditorFragment)?.textStyleProperty = textStyleProperty
    }

}

