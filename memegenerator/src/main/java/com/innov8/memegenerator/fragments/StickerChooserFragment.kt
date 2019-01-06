package com.innov8.memegenerator.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memegenerator.R
import com.innov8.memegenerator.StickerEditorActivity
import com.innov8.memegenerator.adapters.StickersAdapter
import com.innov8.memegenerator.interfaces.StickerEditInterface
import com.innov8.memegenerator.loaders.PreShippedStickersLoader
import com.innov8.memegenerator.loaders.UserStickersLoader
import com.innov8.memegenerator.memeEngine.MemeStickerView
import com.innov8.memegenerator.utils.onTabSelected
import com.innov8.memeit.commons.LoaderAdapterHandler
import kotlinx.android.synthetic.main.bottom_tab.*
import kotlinx.android.synthetic.main.sticker_frag.*

class StickerChooserFragment : Fragment() {
    private val stickersAdapter by lazy {
        StickersAdapter(context!!.applicationContext)
    }
    private val preShippedStickersLoader by lazy {
        PreShippedStickersLoader(stickerPacks[0].second, context!!.applicationContext)
    }
    private val userStickerLoader by lazy {
        UserStickersLoader(context!!.applicationContext)
    }

    private val handler by lazy {
        LoaderAdapterHandler(stickersAdapter, preShippedStickersLoader)
    }

    private var loaded = false
    private var load = false
    var stickerEditInterface: StickerEditInterface? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stickersAdapter.onItemClick = {
            stickerEditInterface?.onAddSticker(MemeStickerView(context!!, it))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true
        return inflater.inflate(R.layout.sticker_frag, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sticker_list.layoutManager = LinearLayoutManager(context!!, RecyclerView.HORIZONTAL, false)
        sticker_list.adapter = stickersAdapter
        pager_tab.onTabSelected { tab ->
            if (tab.position == 3) {
                handler.loader = userStickerLoader
                handler.refresh()
                create_sticker.visibility = View.VISIBLE
            } else {
                handler.loader = preShippedStickersLoader.apply {
                    name = stickerPacks[tab.position].second
                }
                handler.refresh()
                create_sticker.visibility = View.GONE
            }
        }
        create_sticker.setOnClickListener {
            startActivityForResult(Intent(context!!,
                    Class.forName("com.innov8.memeit.activities.PhotoChooserActivity")),
                    260)
        }
        stickerPacks.forEachIndexed { index, pair ->
            pager_tab.addTab(pager_tab.newTab().setText(pair.first), index == 0)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 260 && resultCode == 265 && data != null) {
            StickerEditorActivity.startWithBitmapUri(this, data.getStringExtra("url"))
        } else if (requestCode == StickerEditorActivity.REQUEST_CODE &&
                resultCode == StickerEditorActivity.RESULT_CODE) {
            if (pager_tab.selectedTabPosition == 3) {
                handler.refresh()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loaded = false
        load = false
    }

    companion object {
        private val stickerPacks = listOf("Emojis" to "emoji_stickers",
                "Meme Faces" to "meme_stickers",
                "Chat Bubbles" to "bubbles",
                "My Stickers" to "")
    }

}