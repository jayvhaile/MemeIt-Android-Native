package com.innov8.memeit.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.set
import androidx.core.text.toSpannable
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memeit.commons.MyViewHolder
import com.innov8.memeit.R
import com.innov8.memeit.commons.ELEWordFilterableListAdapter
import com.innov8.memeit.utils.color
import com.innov8.memeit.utils.formatNumber
import com.innov8.memeit.utils.loadImage
import com.innov8.memeit.utils.prefix
import com.innov8.memeit.commons.views.ProfileDraweeView
import com.memeit.backend.models.User
import kotlin.Comparator

class UserSearchAdapter(context: Context) : ELEWordFilterableListAdapter<User, UserSearchAdapter.UserViewHolder>(context) {
    override val filterer: (User) -> Boolean = {
        if (filterWord.startsWith("@"))
            it.username?.toLowerCase()?.contains(filterWord.toLowerCase().substring(1)) ?: false
        else
            it.name?.toLowerCase()?.contains(filterWord.toLowerCase()) ?: false
    }
    override val sorter: Comparator<in User> = Comparator { user1: User, user2: User -> user1.compareTo(user2) }
    override var emptyDrawableId: Int = R.drawable.tag2
    override var errorDrawableId: Int = R.drawable.ic_no_internet
    override var emptyDescription: String = "No User Found"
    override var errorDescription: String = "Failed to load users"
    override var errorActionText: String? = "Try Again"
    override var emptyActionText: String? = null
    override val loadingDrawable = CubeGrid().apply {
        color = Color.rgb(255, 100, 0)
    }

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(inflater.inflate(R.layout.list_item_user_inline, parent, false))
    }

    override fun onBindHolder(holder: UserViewHolder, position: Int) {
        holder.item_position=position
        holder.bind(getItemAt(position))
    }

    override fun getItemType(position: Int): Int = 0

    fun updateFilter(value: String) {
        filterWord = value
        filter()
    }

    var onItemClicked: ((User) -> Unit)? = null

    inner class UserViewHolder(itemView: View) : MyViewHolder<User>(itemView) {
        private val profileV: ProfileDraweeView = itemView.findViewById(R.id.profile_view)
        private val nameV: TextView = itemView.findViewById(R.id.profile_name)
        private val usernameV: TextView = itemView.findViewById(R.id.profile_username)
        private val postCountV: TextView = itemView.findViewById(R.id.post_count)

        init {
            itemView.setOnClickListener { onItemClicked?.invoke(getItemAt(item_position)) }
        }

        @SuppressLint("SetTextI18n")
        override fun bind(t: User) {
            profileV.setText(t.name!!.prefix())
            profileV.loadImage(t.imageUrl)
            t.name?.let {
                val span = it.toSpannable()
                val i = it.toLowerCase().indexOf(filterWord.toLowerCase())
                if (i >= 0) {
                    span[i..i + filterWord.length] = ForegroundColorSpan(R.color.colorAccent.color())
                    span[i..i + filterWord.length] = StyleSpan(Typeface.BOLD)
                }
                nameV.text = span
            }
            t.username?.let {
                val span = "@$it".toSpannable()
                val i = "@$it".toLowerCase().indexOf(filterWord.toLowerCase())
                if (i >= 0) {
                    span[i..i + filterWord.length] = ForegroundColorSpan(R.color.colorAccent.color())
                    span[i..i + filterWord.length] = StyleSpan(Typeface.BOLD)
                }
                usernameV.text = span
            }
            postCountV.text = "${t.postCount.formatNumber()} posts"
        }
    }

}