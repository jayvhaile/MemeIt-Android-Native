package com.innov8.memeit.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memegenerator.Adapters.MyViewHolder
import com.innov8.memeit.Activities.ProfileActivity
import com.innov8.memeit.commons.views.ProfileDraweeView
import com.innov8.memeit.R
import com.innov8.memeit.getDrawableID
import com.innov8.memeit.loadImage
import com.innov8.memeit.prefix
import com.memeit.backend.dataclasses.Reaction

class ReactorAdapter(context: Context) : SimpleELEListAdapter<Reaction>(context, R.layout.list_item_reactors) {
    override var emptyDrawableId: Int = R.drawable.ic_add
    override var errorDrawableId: Int = R.drawable.ic_no_internet
    override var emptyDescription: String = "No Memes"
    override var errorDescription: String = "Couldn't load Memes"
    override var emptyActionText: String? = ""
    override var errorActionText: String? = "Try Again"
    override val loadingDrawable = CubeGrid().apply {
        color = Color.rgb(255, 100, 0)
    }

    override fun createViewHolder(view: View): MyViewHolder<Reaction> {
        return ReactorsViewHolder(view)
    }


    inner class ReactorsViewHolder(itemView: View) : MyViewHolder<Reaction>(itemView) {
        private val reactorPPV: ProfileDraweeView = itemView.findViewById(R.id.reactor_pp)
        private val reactorNameV: TextView = itemView.findViewById(R.id.reactor_name)
        private val reactorReactionV: ImageView = itemView.findViewById(R.id.reactor_reaction)


        init {
            reactorPPV.setOnClickListener {
                val i = Intent(context, ProfileActivity::class.java)
                i.putExtra("user", getItemAt(item_position).poster!!.toUser())
                context.startActivity(i)
            }
        }

        override fun bind(t: Reaction) {
            reactorPPV.loadImage(t.poster!!.profileUrl)
            reactorPPV.text = t.poster!!.name.prefix()
            reactorNameV.text = t.poster!!.name
            reactorReactionV.setImageResource(t.getDrawableID())
        }
    }
}