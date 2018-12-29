package com.innov8.memeit.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memeit.adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.adapters.MemeAdapters.ViewHolders.MemeViewHolder
import com.innov8.memeit.customViews.MemeDraweeView
import com.innov8.memeit.loaders.GuestMemeLoader
import com.innov8.memeit.R
import com.innov8.memeit.utils.LoaderAdapterHandler
import com.innov8.memeit.commons.toast
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.models.HomeElement
import com.memeit.backend.models.Meme
import kotlinx.android.synthetic.main.activity_guest_user.*
import kotlinx.android.synthetic.main.loading_view_layout.*

class GuestUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_view_layout)
        loader_view.setContentView(R.layout.activity_guest_user)

        intent?.data?.lastPathSegment?.let {
            if (MemeItClient.myUser != null) {
                CommentsActivity.startWithMemeId(this, it)
                finish()
            } else {
                loader_view.onRetry = { init(it) }
                init(it)
            }
        } ?: let {
            toast("Meme not found.")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
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
        val handler = LoaderAdapterHandler(adapter, GuestMemeLoader())
        handler.load()
        guest_meme_list.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        guest_meme_list.adapter = adapter
        guest_sign_up.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }
    }

    class GuestMemesAdapter(context: Context) : MemeAdapter(context) {
        override fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
            if (viewType != HomeElement.MEME_TYPE)
                throw IllegalStateException("View Type must only be MEME_TYPE in GuestMemeAdapter")

            return GuestMemeViewHolder(inflater.inflate(R.layout.list_item_guest_meme, parent, false))
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
