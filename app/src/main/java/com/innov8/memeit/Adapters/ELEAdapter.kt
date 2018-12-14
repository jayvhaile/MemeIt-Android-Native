package com.innov8.memeit.Adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memegenerator.Adapters.MyViewHolder
import com.innov8.memeit.R

@Suppress("UNCHECKED_CAST")
abstract class ELEAdapter<T : RecyclerView.ViewHolder>(val context: Context)
    : RecyclerView.Adapter<T>() {
    protected val inflater: LayoutInflater = LayoutInflater.from(context)

    companion object {
        const val MODE_NORMAL = 0
        const val MODE_ERROR = 1
        const val MODE_EMPTY = 2


        const val TYPE_LOADING = 1001
        const val TYPE_EMPTY = 1002
        const val TYPE_ERROR = 1003
        const val TYPE_LOAD_MORE = 1004
    }

    var mode = MODE_NORMAL
        set(value) {
            if (value != field) {
                field = value
                notifyDataSetChanged()
            }
        }
    var loading = false
        set(value) {
            if (field != value) {
                field = value
                if (field) {
                    if (mode != MODE_NORMAL) mode = MODE_NORMAL
                    if (hasMore && getCount() > 0) notifyItemChanged(getCount())
                    else notifyItemInserted(getCount())
                } else {
                    if (hasMore && getCount() > 0) notifyItemChanged(getCount())
                    else notifyItemRemoved(getCount())
                }
            }
        }
    var hasMore = true
        set(value) {
            if (field != value) {
                field = value
                if (mode == MODE_NORMAL && getCount() > 0 && !loading) {
                    if (field) notifyItemInserted(getCount() - 1)
                    else notifyItemRemoved(getCount())
                }
            }
        }

    abstract var emptyDrawableId: Int
    abstract var errorDrawableId: Int
    abstract var emptyDescription: String
    abstract var errorDescription: String

    abstract var emptyActionText: String?
    abstract var errorActionText: String?

    abstract val loadingDrawable: Drawable


    var onLoadMore: (() -> Unit)? = null
    var onEmptyAction: (() -> Unit)? = null
    var onErrorAction: (() -> Unit)? = null

    //normally the error view is centered vertically this is an option to make it stick to the top
    var showErrorAtTop = false

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
        return when (viewType) {
            TYPE_ERROR -> createErrorView(parent)
            TYPE_EMPTY -> createEmptyView(parent)
            TYPE_LOADING -> createLoaderView(parent)
            TYPE_LOAD_MORE -> createLoadMoreView(parent)
            else -> onCreateHolder(parent, viewType)
        }
    }

    private fun createLoaderView(parent: ViewGroup): T {
        val view = inflater.inflate(R.layout.list_item_meme_loading, parent, false)
        val progress: ProgressBar = view.findViewById(R.id.meme_loading)
        progress.indeterminateDrawable = loadingDrawable
        return object : RecyclerView.ViewHolder(view) {} as T
    }


    private fun createLoadMoreView(parent: ViewGroup): T {
        val view = inflater.inflate(R.layout.list_item_load_more, parent, false)
        view.setOnClickListener { onLoadMore?.invoke() }
        return object : RecyclerView.ViewHolder(view) {} as T
    }

    private fun createEmptyView(parent: ViewGroup): T {
        val v = inflater.inflate(R.layout.list_item_error, parent, false) as ConstraintLayout
        if (showErrorAtTop) {
            ConstraintSet().apply {
                clone(v)
                clear(R.id.recyc_drawable, ConstraintSet.BOTTOM)
                applyTo(v)
            }
        }
        v.findViewById<ImageView>(R.id.recyc_drawable).setImageResource(emptyDrawableId)
        v.findViewById<TextView>(R.id.recyc_desc).text = emptyDescription
        val actionV = v.findViewById<TextView>(R.id.recyc_action)
        if (emptyActionText != null) {
            actionV.text = emptyActionText
            actionV.setOnClickListener { onEmptyAction?.invoke() }
        } else actionV.visibility = View.GONE
        return object : RecyclerView.ViewHolder(v) {} as T
    }

    private fun createErrorView(parent: ViewGroup): T {
        val v = inflater.inflate(R.layout.list_item_error, parent, false) as ConstraintLayout
        if (showErrorAtTop) {
            ConstraintSet().apply {
                clone(v)
                clear(R.id.recyc_drawable, ConstraintSet.BOTTOM)
                applyTo(v)
            }
        }
        v.findViewById<ImageView>(R.id.recyc_drawable).setImageResource(errorDrawableId)
        v.findViewById<TextView>(R.id.recyc_desc).text = errorDescription
        val actionV = v.findViewById<TextView>(R.id.recyc_action)
        if (errorActionText != null) {
            actionV.text = errorActionText
            actionV.setOnClickListener { onErrorAction?.invoke() }
        } else actionV.visibility = View.GONE
        return object : RecyclerView.ViewHolder(v) {} as T
    }

    final override fun getItemCount(): Int =
            when (mode) {
                MODE_NORMAL -> {
                    val c = getCount()
                    if (loading || (c > 0 && hasMore))
                        c + 1
                    else c
                }
                MODE_EMPTY, MODE_ERROR -> 1
                else -> 0
            }


    final override fun onBindViewHolder(holder: T, position: Int) {
        if (mode == MODE_NORMAL && position < getCount()) onBindHolder(holder, position)
    }

    final override fun getItemViewType(position: Int): Int {
        return when (mode) {
            MODE_NORMAL -> {
                if (position < getCount())
                    getItemType(position)
                else {
                    if (loading) TYPE_LOADING
                    else TYPE_LOAD_MORE
                }
            }
            MODE_ERROR -> TYPE_ERROR
            MODE_EMPTY -> TYPE_EMPTY
            else -> throw IllegalStateException()
        }
    }

    abstract fun onCreateHolder(parent: ViewGroup, viewType: Int): T
    abstract fun onBindHolder(holder: T, position: Int)
    /**
     * 1000-1004 is reserved, use anything else
     */
    abstract fun getItemType(position: Int): Int

    abstract fun getCount(): Int
}

abstract class ELEListAdapter<T, VH : RecyclerView.ViewHolder>(context: Context) : ELEAdapter<VH>(context) {
    val items = mutableListOf<T>()


    override fun getCount(): Int = items.size


    fun addAll(items: List<T>) {
        if (items.isEmpty()) return
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
        notifyDataSetChanged()
    }

    fun setAll(item: List<T>) {
        this.items.clear()
        this.items.addAll(item)
        if (items.size > 0) {
            if (mode == MODE_NORMAL) notifyDataSetChanged()
            else mode = MODE_NORMAL
        } else mode = MODE_EMPTY
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

abstract class ELEFilterableListAdapter<T, VH : RecyclerView.ViewHolder>(context: Context) : ELEAdapter<VH>(context) {
    protected val items: MutableSet<T> = hashSetOf()
    private val filteredItems: MutableList<T> = mutableListOf()


    abstract val filterer: (T) -> Boolean
    abstract val sorter: Comparator<in T>
    protected var filterable: Boolean = false
        set(value) {
            field = value
            filter()
        }

    protected fun filter() {
        filteredItems.clear()
        if (filterable)
            items.filter(filterer)
                    .sortedWith(sorter)
                    .toCollection(filteredItems)
        else items.sortedWith(sorter).toCollection(filteredItems)
        notifyDataSetChanged()
    }


    override fun getCount(): Int = items.size
    fun addAll(items: List<T>) {
        if (items.isEmpty()) return
        this.items.addAll(items)
        filter()
    }

    fun add(item: T) {
        items.add(item)
        filter()
    }


    fun remove(item: T) {
        if (items.contains(item)) {
            items.remove(item)
            filter()
        }
    }

    fun clear() {
        items.clear()
        filteredItems.clear()
        notifyDataSetChanged()
    }

    fun setAll(item: List<T>) {
        this.items.clear()
        this.items.addAll(item)
        filter()
        if (filteredItems.size > 0) {
            if (mode == MODE_NORMAL) notifyDataSetChanged()
            else mode = MODE_NORMAL
        } else mode = MODE_EMPTY
    }

    fun getItemAt(index: Int): T {
        return filteredItems[index]
    }

}

abstract class SimpleELEListAdapter<T>(context: Context, private val mLayoutID: Int) : ELEListAdapter<T, MyViewHolder<T>>(context) {
    var onItemClicked: ((T) -> Unit)? = null

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): MyViewHolder<T> {
        val view = inflater.inflate(mLayoutID, parent, false)
        return createViewHolder(view)
    }

    override fun onBindHolder(holder: MyViewHolder<T>, position: Int) {
        holder.position = position
        holder.bind(items[position])
    }

    override fun getItemType(position: Int): Int = 100

    abstract fun createViewHolder(view: View): MyViewHolder<T>

}
