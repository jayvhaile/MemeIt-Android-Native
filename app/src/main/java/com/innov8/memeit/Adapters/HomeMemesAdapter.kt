package com.innov8.memeit.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.innov8.memegenerator.adapters.MyViewHolder
import com.innov8.memegenerator.loading_button_lib.customViews.CircularProgressButton
import com.innov8.memeit.Models.*
import com.innov8.memeit.R
import com.memeit.backend.MemeItUsers

class HomeMemesAdapter(val context: Context) : RecyclerView.Adapter<MyViewHolder<out HomeElement>>() {
    private var elements = mutableListOf<HomeElement>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder<out HomeElement> {
        when (viewType) {
            MEME_TYPE -> {
                //todo change this to memeviewholder
                val inflater=LayoutInflater.from(context)
                val v=inflater.inflate(R.layout.list_item_user_suggestion,parent,false)
                return UserSuggestionHolder(v)
            }
            USER_SUGGESTION_TYPE -> {
                val inflater=LayoutInflater.from(context)
                val v=inflater.inflate(R.layout.list_item_user_suggestion,parent,false)
                return UserSuggestionHolder(v)
            }
            MEME_TEMPLATE_SUGGESTION_TYPE -> {
                val inflater=LayoutInflater.from(context)
                val v=inflater.inflate(R.layout.list_item_user_suggestion,parent,false)
                return MemeTemplateSuggestionHolder(v)
            }
            AD_TYPE -> {
                val inflater=LayoutInflater.from(context)
                val v=inflater.inflate(R.layout.list_item_user_suggestion,parent,false)
                return AdHolder(v)
            }
            else->{
                throw IllegalArgumentException("ViewType must be one of the four")
            }
        }
    }

    override fun getItemCount(): Int = elements.size


    override fun onBindViewHolder(holder: MyViewHolder<out HomeElement>, position: Int) {
        when (getItemViewType(position)) {
            MEME_TYPE -> {

            }
            USER_SUGGESTION_TYPE -> {
                (holder as UserSuggestionHolder).bind(elements[position] as UserSuggestion)
            }
            MEME_TEMPLATE_SUGGESTION_TYPE -> {
                (holder as MemeTemplateSuggestionHolder).bind(elements[position] as MemeTemplateSuggestion)
            }
            AD_TYPE -> {
                (holder as AdHolder).bind(elements[position] as AdElement)
            }
        }
        holder.position = position
    }

    override fun getItemViewType(position: Int): Int = elements[position].itemType


    private inner class UserSuggestionHolder(itemView: View) : MyViewHolder<UserSuggestion>(itemView) {
        private val imgs: List<SimpleDraweeView> = listOf(
                itemView.findViewById(R.id.suggestion_pp1),
                itemView.findViewById(R.id.suggestion_pp2),
                itemView.findViewById(R.id.suggestion_pp3),
                itemView.findViewById(R.id.suggestion_pp4)
        )
        private val btns: List<CircularProgressButton> = listOf(
                itemView.findViewById(R.id.suggestion_follow_btn1),
                itemView.findViewById(R.id.suggestion_follow_btn2),
                itemView.findViewById(R.id.suggestion_follow_btn3),
                itemView.findViewById(R.id.suggestion_follow_btn4)
        )
        init {
            btns.forEachIndexed { i, btn ->
                btn.setOnClickListener {
                   val user=(elements[item_position] as UserSuggestion).users[i]
                    //todo provide an oncompletelistenr here
                  MemeItUsers.getInstance().followUser(user.userID,null)
                }
            }
        }


        override fun bind(t:UserSuggestion) {
            for(i in 0..4){
                if(i<t.users.size){
                    imgs[i].visibility=View.VISIBLE
                    btns[i].visibility=View.VISIBLE
                    imgs[i].setImageURI(t.users[i].imageUrl)//todo replace with loadimageto
                }else{
                    imgs[i].visibility=View.GONE
                    btns[i].visibility=View.GONE
                }
            }
        }
    }

    inner class MemeTemplateSuggestionHolder(itemView: View) : MyViewHolder<MemeTemplateSuggestion>(itemView) {
        private val imgs: List<SimpleDraweeView> = listOf(
                itemView.findViewById(R.id.suggestion_pp1),
                itemView.findViewById(R.id.suggestion_pp2),
                itemView.findViewById(R.id.suggestion_pp3),
                itemView.findViewById(R.id.suggestion_pp4)
        )
        private val btns: List<CircularProgressButton> = listOf(
                itemView.findViewById(R.id.suggestion_follow_btn1),
                itemView.findViewById(R.id.suggestion_follow_btn2),
                itemView.findViewById(R.id.suggestion_follow_btn3),
                itemView.findViewById(R.id.suggestion_follow_btn4)
        )

        override fun bind(t: MemeTemplateSuggestion) {
           for(i in 0..4){
                if(i<t.templates.size){
                    imgs[i].visibility=View.VISIBLE
                    btns[i].visibility=View.VISIBLE
                    imgs[i].setImageURI(t.templates[i])
                }else{
                    imgs[i].visibility=View.GONE
                    btns[i].visibility=View.GONE
                }
            }
        }
    }

    inner class AdHolder(itemView: View) : MyViewHolder<AdElement>(itemView) {
        override fun bind(t: AdElement) {

        }
    }


}