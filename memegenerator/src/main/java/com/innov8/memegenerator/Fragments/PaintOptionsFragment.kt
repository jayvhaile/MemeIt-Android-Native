package com.innov8.memegenerator.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.innov8.memegenerator.interfaces.PaintEditInterface
import com.innov8.memegenerator.MemeEngine.PaintHandler
import com.innov8.memegenerator.R
import kotlinx.android.synthetic.main.paint_options_view.*

class PaintOptionsFragment : Fragment() {


    private val views = mutableListOf<View>()

    var paintEditInterface: PaintEditInterface? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.paint_options_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        color_seek_bar.colorChangeListener = {
            paintEditInterface?.onBrushColorChanged(it)
//            brush_size.setColorForAll(it)
        }
        brush_size.onBrushSizeSelected = {
            paintEditInterface?.onBrushSizeChanged(it)
        }

        val popup = PopupMenu(context, paint_shape_chooser)
        popup.inflate(R.menu.paint_shape_menu)

        paint_shape_chooser.setOnClickListener {
            popup.show()
        }
        popup.setOnMenuItemClickListener {
            paintEditInterface?.onShapeChanged(when (it.itemId) {
                R.id.menu_shape_rect -> (PaintHandler.PaintMode.RECT)
                R.id.menu_shape_circle -> (PaintHandler.PaintMode.CIRCLE)
                R.id.menu_shape_oval -> (PaintHandler.PaintMode.OVAL)
                R.id.menu_shape_line -> (PaintHandler.PaintMode.LINE)
                R.id.menu_shape_arrow -> (PaintHandler.PaintMode.ARROW)
                else -> (PaintHandler.PaintMode.DOODLE)
            })
            true
        }
        paint_undo.setOnClickListener {
            paintEditInterface?.onPaintUndo()
            updateUndoState()
        }
        updateUndoState()
    }

    fun updateUndoState() {
        val hasUndo = paintEditInterface?.hasUndo() ?: true
        paint_undo?.isEnabled = hasUndo
        paint_undo?.alpha = if (hasUndo) 1f else 0.5f
    }


}