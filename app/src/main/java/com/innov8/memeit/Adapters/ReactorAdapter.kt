package com.innov8.memeit.Adapters

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.innov8.memegenerator.adapters.ListAdapter
import com.innov8.memegenerator.adapters.MyViewHolder
import com.innov8.memeit.Activities.ProfileActivity
import com.innov8.memeit.CustomViews.ProfileDraweeView
import com.innov8.memeit.R
import com.innov8.memeit.getDrawableID
import com.innov8.memeit.loadImage
import com.innov8.memeit.prefix
import com.memeit.backend.dataclasses.Reaction

class ReactorAdapter(context:Context): ListAdapter<Reaction>(context, R.layout.list_item_reactors) {
    val size=context.resources.getDimension(R.dimen.profile_mini_size)
    override fun createViewHolder(view: View): MyViewHolder<Reaction> {
       return ReactorsViewHolder(view)
    }



    inner class ReactorsViewHolder(itemView:View):MyViewHolder<Reaction>(itemView){
        private val reactorPPV:ProfileDraweeView=itemView.findViewById(R.id.reactor_pp)
        private val reactorNameV:TextView=itemView.findViewById(R.id.reactor_name)
        private val reactorReactionV:ImageView=itemView.findViewById(R.id.reactor_reaction)


        init {
            reactorPPV.setOnClickListener {
                val i = Intent(mContext, ProfileActivity::class.java)
                i.putExtra("user", getItemAt(item_position).poster.toUser())
                mContext.startActivity(i)
            }
        }
        override fun bind(t: Reaction) {
            reactorPPV.loadImage(t.poster.profileUrl,size,size)
            reactorPPV.text=t.poster.name.prefix()
            reactorNameV.text=t.poster.name
            reactorReactionV.setImageResource(t.getDrawableID())

        }
    }
}