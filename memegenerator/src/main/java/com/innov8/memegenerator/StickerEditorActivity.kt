package com.innov8.memegenerator

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.innov8.memegenerator.customViews.CheckerBoardDrawable
import com.innov8.memegenerator.memeEngine.PaintHandler
import com.innov8.memegenerator.models.StickerPack
import com.innov8.memeit.commons.dp
import com.innov8.memeit.commons.loadBitmapfromStream
import com.innov8.memeit.commons.makeFullScreen
import com.memeit.backend.MemeItClient.context
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.activity_sticker_editor.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

class StickerEditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticker_editor)
        val url = intent?.getStringExtra("url") ?: throw IllegalStateException("url is required")
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            withContext(Dispatchers.Default) {
                contentResolver.openInputStream(Uri.parse(url))?.let {
                    this@StickerEditorActivity.loadBitmapfromStream(it, 400, 800)
                }
            }?.let {
                bitmapEraserView.bitmap = it
            } ?: finish()
        }
        background.setImageDrawable(CheckerBoardDrawable(12f.dp(this), Color.LTGRAY, Color.GRAY))
        color_seek_bar.colorChangeListener = {
            bitmapEraserView.paintHandler.apply {
                paintProperty = paintProperty.copy(color = it)
            }
        }
        softness_seekbar.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) {

            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {
                bitmapEraserView.paintHandler.apply {
                    seekBar.progressFloat.let {
                        paintProperty = paintProperty.copy(maskFilter = if (it < 1) null else BlurMaskFilter(it, BlurMaskFilter.Blur.NORMAL))
                    }
                }
            }
        }
        brush_size.onBrushSizeSelected = {
            bitmapEraserView.paintHandler.apply {
                paintProperty = paintProperty.copy(brushSize = it)
            }
        }

        val popup = PopupMenu(context, paint_shape_chooser)
        popup.inflate(R.menu.paint_shape_menu)

        paint_shape_chooser.setOnClickListener {
            popup.show()
        }
        popup.setOnMenuItemClickListener {
            bitmapEraserView.paintHandler.apply {
                paintMode = when (it.itemId) {
                    R.id.menu_shape_rect -> (PaintHandler.PaintMode.RECT)
                    R.id.menu_shape_circle -> (PaintHandler.PaintMode.CIRCLE)
                    R.id.menu_shape_oval -> (PaintHandler.PaintMode.OVAL)
                    R.id.menu_shape_line -> (PaintHandler.PaintMode.LINE)
                    R.id.menu_shape_arrow -> (PaintHandler.PaintMode.ARROW)
                    else -> (PaintHandler.PaintMode.DOODLE)
                }
                switchEraseMode(false)
            }
            true
        }
        paint_eraser.setOnClickListener {
            switchEraseMode()
        }
        val undo = { _: View ->
            bitmapEraserView.paintHandler.actionManager.undo()
            updateUndoState()
        }

        val redo = { _: View ->
            bitmapEraserView.paintHandler.actionManager.redo()
            updateRedoState()
        }
        paint_undo.setOnClickListener(undo)
        paint_undo_text.setOnClickListener(undo)
        paint_redo.setOnClickListener(redo)
        paint_redo_text.setOnClickListener(redo)
        updateUndoState()
        switchEraseMode(true)

        back.setOnClickListener {
            onBackPressed()
        }
        done.setOnClickListener {
            val bitmap = bitmapEraserView.getEditedBitmap()
            val file = File(StickerPack.myStickersDir(this), "${UUID.randomUUID()}_sticker.png")
            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                withContext(Dispatchers.Default) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, FileOutputStream(file))
                }
                setResult(RESULT_CODE)
                finish()
            }
        }
        bitmapEraserView.paintHandler.actionManager.onActionListChanged = {
            updateUndoState()
            updateRedoState()
        }
    }

    private fun switchEraseMode(erase: Boolean = color_seek_bar.isEnabled) {
        paint_eraser.setColorFilter(if (erase) Color.RED else Color.WHITE)
        color_seek_bar.isEnabled = !erase
        color_seek_bar.alpha = if (erase) 0.5f else 1f
        bitmapEraserView.paintHandler.apply {
            if (erase) paintMode = PaintHandler.PaintMode.DOODLE
            paintProperty = if (erase)
                paintProperty.copy(color = Color.TRANSPARENT, xferMode = PorterDuffXfermode(PorterDuff.Mode.CLEAR))
            else
                paintProperty.copy(color = color_seek_bar.getColor(), xferMode = null)
        }
    }

    private fun updateUndoState() {
        val hasUndo = bitmapEraserView.paintHandler.actionManager.head != null
        paint_undo?.apply {
            isEnabled = hasUndo
            alpha = if (hasUndo) 1f else 0.5f
        }
        paint_undo_text?.apply {
            isEnabled = hasUndo
            alpha = if (hasUndo) 1f else 0.5f
        }
    }

    private fun updateRedoState() {
        val hasRedo = bitmapEraserView.paintHandler.actionManager.hasNext()
        paint_redo?.apply {
            isEnabled = hasRedo
            alpha = if (hasRedo) 1f else 0.5f
        }
        paint_redo_text?.apply {
            isEnabled = hasRedo
            alpha = if (hasRedo) 1f else 0.5f
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) makeFullScreen()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        makeFullScreen()
    }

    companion object {
        const val REQUEST_CODE = 173
        const val RESULT_CODE = 178
        fun startWithBitmapUri(context: Context, url: String) {
            context.startActivity(Intent(context, StickerEditorActivity::class.java).apply {
                putExtra("url", url)
            })
        }

        fun startWithBitmapUri(fragment: Fragment, url: String) {
            fragment.startActivityForResult(Intent(context, StickerEditorActivity::class.java).apply {
                putExtra("url", url)
            }, REQUEST_CODE)
        }
    }
}
