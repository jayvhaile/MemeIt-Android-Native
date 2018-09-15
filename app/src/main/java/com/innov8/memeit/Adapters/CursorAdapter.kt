package com.innov8.memeit.Adapters

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memegenerator.adapters.MyViewHolder

abstract class CursorAdapter<T>(val context: Context, val layoutID: Int) : RecyclerView.Adapter<MyViewHolder<T>>() {
    var cursor: Cursor? = null
    var OnItemClicked: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder<T> {
        val view = LayoutInflater.from(context).inflate(layoutID, parent, false)
        return createViewHolder(view)
    }

    abstract fun createViewHolder(view: View): MyViewHolder<T>

    override fun getItemCount(): Int = cursor?.count ?: 0


    override fun onBindViewHolder(holder: MyViewHolder<T>, position: Int) {
        holder.item_position = position
        holder.bind(getItem(position))
    }

    fun getItem(position: Int): T {
        cursor ?: throw IllegalStateException()
        cursor!!.moveToPosition(position)
        return getItem(cursor!!)
    }

    abstract fun getItem(cursor: Cursor): T

    fun swapCursor(cursor: Cursor?) {
        this.cursor = cursor
        notifyDataSetChanged()
    }

}