package com.innov8.memeit.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.innov8.memeit.Adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.Adapters.MemeAdapters.ViewHolders.MemeViewHolder
import com.innov8.memeit.CustomViews.MemeDraweeView
import com.innov8.memeit.Loaders.GuestMemeLoader
import com.innov8.memeit.MLHandler
import com.innov8.memeit.R
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.dataclasses.HomeElement
import com.memeit.backend.dataclasses.Meme
import kotlinx.android.synthetic.main.activity_guest_user.*
import kotlinx.android.synthetic.main.loading_view_layout.*

class GuestUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_view_layout)
        loader_view.setContentView(R.layout.activity_guest_user)
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener {
            val memeid = it.link.lastPathSegment
            if (MemeItClient.myUser != null) {
                if (memeid == null)
                    startActivity(Intent(this, MainActivity::class.java))
                else
                    CommentsActivity.startWithMemeId(this, memeid)
            } else {
                if (memeid == null)
                    startActivity(Intent(this, AuthActivity::class.java))
                else {
                    loader_view.onRetry = { init(memeid) }
                    init(memeid)
                }
            }
        }.addOnFailureListener {
            loader_view.setError("ff")
        }
    }

    private fun init(memeid: String) {
        loader_view.setLoading()
        MemeItMemes.getMemeByIdGuest(memeid).call({
            loader_view.setLoaded()
            guest_meme_view.loadMeme(it)
        }) {
            loader_view.setError(it)
        }
        val adapter = GuestMemesAdapter(this)
        val handler = MLHandler(adapter, GuestMemeLoader())
        handler.load()
        guest_meme_list.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        guest_meme_list.adapter = adapter
        guest_sign_up.setOnClickListener {
            startActivity(Intent(this,AuthActivity::class.java))
        }
    }

    class GuestMemesAdapter(context: Context) : MemeAdapter(context) {
        override fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
            if (viewType != HomeElement.MEME_TYPE)
                throw IllegalStateException("View Type must only be MEME_TYPE in GuestMemeAdapter")

            return GuestMemeViewHolder(inflater.inflate(R.layout.list_item_guest_meme,parent,false))
        }

        inner class GuestMemeViewHolder(itemView: View) : MemeViewHolder(itemView, this) {
            private val memeView: MemeDraweeView = itemView.findViewById(R.id.guest_meme_image)
            override fun bind(homeElement: HomeElement) {
                homeElement as Meme
                memeView.loadMeme(homeElement)
            }
        }
    }

}
