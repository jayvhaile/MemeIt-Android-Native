package com.innov8.memeit.CustomViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memeit.R

class MyRecyclerView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val recyclerView = RecyclerView(context)
    val infoView: View = LayoutInflater.from(context).inflate(R.layout.list_item_error, this, false)

    private val infoDrawable: ImageView = infoView.findViewById(R.id.recyc_drawable)
    private val infoDescription: TextView = infoView.findViewById(R.id.recyc_desc)
    private val infoAction: TextView = infoView.findViewById(R.id.recyc_action)

    init {
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        addView(infoView, lp)
        addView(recyclerView, lp)
        recyclerView.visibility = View.GONE
        recyclerView.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {
                if (recyclerView.layoutManager?.childCount == 0) {
                    recyclerView.visibility = View.GONE
                    infoView.visibility = View.VISIBLE
                }
            }

            override fun onChildViewAttachedToWindow(view: View) {
                if (recyclerView.layoutManager?.childCount ?: 0 > 0) {
                    recyclerView.visibility = View.VISIBLE
                    infoView.visibility = View.GONE
                }
            }
        })
        infoAction.setOnClickListener { currentMode?.onAction?.invoke() }
    }

    var emptyModeInfo: InfoMode? = null
    var errorModeInfo: InfoMode? = null
    var currentMode: InfoMode? = null
        set(value) {
            field = value
            currentMode?.drawableId?.let { infoDrawable.setImageResource(it) }
                    ?: infoDrawable.setImageDrawable(null)
            infoDescription.text = currentMode?.description ?: ""
            infoAction.text = currentMode?.actionName ?: ""
        }

    fun setEmptyMode() {
        currentMode = emptyModeInfo
    }

    fun setErrorMode() {
        currentMode = errorModeInfo
    }

}

data class InfoMode(val drawableId: Int?,
                    val description: String?,
                    val actionName: String?,
                    val onAction: (() -> Unit)?)