package com.innov8.memeit.Adapters

import android.content.Context
import android.view.View
import android.widget.TextView
import com.facebook.common.util.UriUtil
import com.facebook.drawee.view.SimpleDraweeView
import com.innov8.memegenerator.Adapters.ListAdapter
import com.innov8.memegenerator.Adapters.MyViewHolder
import com.innov8.memegenerator.MemeEditorActivity
import com.innov8.memegenerator.loading_button_lib.customViews.CircularProgressButton
import com.innov8.memeit.MemeItApp
import com.innov8.memeit.R
import com.innov8.memeit.commons.getDrawableIdByName
import com.innov8.memeit.commons.models.MemeTemplate
import com.innov8.memeit.commons.toast
import com.innov8.memeit.commons.views.ProfileDraweeView
import com.innov8.memeit.prefix
import com.memeit.backend.dataclasses.User

class UserSugAdapter(context: Context) : ListAdapter<User>(context, R.layout.list_item_user_sug) {
    override fun createViewHolder(view: View): MyViewHolder<User> = UserSugViewHolder(view)


    inner class UserSugViewHolder(itemView: View) : MyViewHolder<User>(itemView) {
        private val profileV: ProfileDraweeView = itemView.findViewById(R.id.user_sug_pp)
        private val nameV: TextView = itemView.findViewById(R.id.user_sug_name)
        private val followBtn: CircularProgressButton = itemView.findViewById(R.id.follower_follow_btn)

        init {
            followBtn.setOnClickListener {
                it.context.toast("Follow ${getItemAt(item_position).name}")
            }
        }

        override fun bind(t: User) {
            profileV.setText(t.name.prefix())
            nameV.text = t.name
        }
    }
}

class TemplateSugAdapter(context: Context) : ListAdapter<MemeTemplate>(context, R.layout.list_item_template_sug) {
    override fun createViewHolder(view: View): MyViewHolder<MemeTemplate> = TemplateSugViewHolder(view)


    inner class TemplateSugViewHolder(itemView: View) : MyViewHolder<MemeTemplate>(itemView) {
        private val templateV: SimpleDraweeView = itemView.findViewById(R.id.template_sug_pp)
        private val editBtn: TextView = itemView.findViewById(R.id.edit_btn)

        init {
            editBtn.setOnClickListener {
                MemeEditorActivity.startWithMemeTemplate(context,getItemAt(item_position))
            }
        }

        override fun bind(t: MemeTemplate) {
            val u = UriUtil.getUriForResourceId(MemeItApp.instance.getDrawableIdByName(t.imageURL)).toString()
            templateV.setImageURI(u)
        }
    }
}