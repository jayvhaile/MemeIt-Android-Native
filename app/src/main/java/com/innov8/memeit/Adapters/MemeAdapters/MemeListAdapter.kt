package com.innov8.memeit.Adapters.MemeAdapters

import android.content.Context
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import com.innov8.memeit.Adapters.MemeAdapters.ViewHolders.MemeListViewHolder
import com.innov8.memeit.Adapters.MemeAdapters.ViewHolders.MemeViewHolder
import com.innov8.memeit.CustomViews.MemeView
import com.innov8.memeit.R
import com.memeit.backend.dataclasses.HomeElement

class MemeListAdapter(context: Context) : MemeAdapter(context) {
    companion object {
        val activeRID = intArrayOf(R.drawable.laughing, R.drawable.rofl, R.drawable.neutral, R.drawable.angry)
    }

    val constraintSetReaction = ConstraintSet().apply {
        clone(context, R.layout.list_item_meme)
    }
    val constraintSetDefault = ConstraintSet().apply {
        clone(context, R.layout.list_item_meme2)
    }
    override fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        if (viewType != HomeElement.MEME_TYPE)
            throw IllegalStateException("View Type must only be MEME_TYPE in MemeListAdapter")
        return MemeListViewHolder(MemeView(context, constraintSetReaction, constraintSetDefault), this)
    }
}