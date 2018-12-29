package com.innov8.memegenerator.adapters

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.innov8.memegenerator.memeEngine.PaintHandler.PaintMode
import com.innov8.memegenerator.R

data class PaintShapeInfo(val imageID: Int, val name: String, val paintMode: PaintMode) {

    companion object {
        fun getList() = listOf(
                PaintShapeInfo(R.drawable.paint_shape_doodle, "Doodle", PaintMode.DOODLE),
                PaintShapeInfo(R.drawable.paint_shape_arrow, "Arrow", PaintMode.ARROW),
                PaintShapeInfo(R.drawable.paint_shape_line, "Line", PaintMode.LINE),
                PaintShapeInfo(R.drawable.paint_shape_rect, "Rectangle", PaintMode.RECT),
                PaintShapeInfo(R.drawable.paint_shape_circle, "Circle", PaintMode.CIRCLE)
        )
    }

}

class PaintShapesListAdapter(context: Context) : ListAdapter<PaintShapeInfo>(context, R.layout.list_item_paintshape) {

    var selectedIndex: Int = 0
        set(value) {
            notifyItemChanged(field)
            field = value
            notifyItemChanged(field)
            onSelectionChangedListener?.invoke(getItemAt(field).paintMode)
        }

    var onSelectionChangedListener: ((PaintMode) -> Unit)? = null
    override fun createViewHolder(view: View): MyViewHolder<PaintShapeInfo> {
        return PaintShapeViewHolder(view)
    }

    val selectedColor = Color.parseColor("#220000ff")
    val defaultColor = Color.TRANSPARENT

    internal inner class PaintShapeViewHolder(itemView: View) : MyViewHolder<PaintShapeInfo>(itemView) {
        val imageV: ImageView = itemView.findViewById(R.id.paint_shape_image)
        val textV: TextView = itemView.findViewById(R.id.paint_shape_text)

        init {
            itemView.setOnClickListener {
                selectedIndex = item_position
            }
        }

        override fun bind(t: PaintShapeInfo) {
            imageV.setBackgroundColor(if (selectedIndex == item_position) selectedColor else defaultColor)
            imageV.setImageResource(t.imageID)
            textV.text = t.name
        }
    }
}