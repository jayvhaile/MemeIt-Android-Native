package com.innov8.memegenerator.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.*

/**
 * Created by Jv on 7/21/2018.
 */

abstract class ListAdapter<T>(protected var mContext: Context, private val mLayoutID: Int) : androidx.recyclerview.widget.RecyclerView.Adapter<MyViewHolder<T>>() {
    private val items: MutableList<T>
    var OnItemClicked:((T)->Unit)?=null
    init {
        items = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder<T> {
        val view = LayoutInflater.from(mContext).inflate(mLayoutID, parent, false)
        return createViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder<T>, position: Int) {
        holder.setPosition(position)
        holder.bind(items[position])
    }

    abstract fun createViewHolder(view: View): MyViewHolder<T>

    override fun getItemCount(): Int {
        return items.size
    }


    fun addAll(items: List<T>) {
        if (items.size == 0) return
        val start = this.items.size
        this.items.addAll(items)
        notifyItemRangeInserted(start, items.size)
    }

    fun add(item: T) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }


    fun remove(item: T) {
        if (items.contains(item)) {
            val index = items.indexOf(item)
            items.remove(item)
            notifyItemRemoved(index)
        }
    }

    fun clear() {
        items.clear()
        notifyItemRangeRemoved(0, items.size)
    }

    fun setAll(items: List<T>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun updateItem(item: T, index: Int) {
        if (index < -1 || index >= items.size) return
        items[index] = item
        notifyItemChanged(index)
    }

    fun updateItem(item: T) {
        val index = items.indexOf(item)
        if (index != -1)
            updateItem(item, index)
    }

    fun getItemAt(index: Int): T {
        return items[index]
    }

}
