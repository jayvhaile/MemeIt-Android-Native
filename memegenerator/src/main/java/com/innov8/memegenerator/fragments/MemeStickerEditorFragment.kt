package com.innov8.memegenerator.fragments


import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import com.innov8.memegenerator.R
import com.innov8.memegenerator.adapters.StickersAdapter
import com.innov8.memegenerator.customViews.MyToolbarmenu
import com.innov8.memegenerator.memeEngine.MemeEditorView
import com.innov8.memegenerator.memeEngine.MemeStickerView
import com.innov8.memegenerator.utils.AsyncLoader
import com.innov8.memegenerator.utils.initWithGrid
import kotlinx.android.synthetic.main.fragment_meme_editor_sticker.*

class MemeStickerEditorFragment : MemeEditorFragment() {
    override val menus: List<MyToolbarmenu>
        get() = listOf(

                MyToolbarmenu(R.drawable.ic_text_menu_delete) {
                    memeEditorView?.removeSelectedItem(MemeStickerView::class.java)
                }
        )
    var memeEditorView: MemeEditorView? = null//todo this should not be here

    val spanCount=6
    lateinit var adapter: StickersAdapter
    private lateinit var emoji_list: List<String>
    private lateinit var meme_face_list: List<String>

    var eloaded = false
    var mloaded = false
    var ewaiting = false
    var mwaiting = false
    var onItemClick: ((String) -> Unit)={url->
        AsyncLoader {
            val x=url.substring(9)
            BitmapFactory.decodeStream(context!!.assets.open(x))
        }.load {bitmap->
            val memeStickerView = MemeStickerView(context!!, bitmap)
            memeEditorView!!.addMemeItemView(memeStickerView)
        }
    }
    fun setOnStickerSelected( onItemClick: (String) -> Unit) {
        this.onItemClick = onItemClick
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = StickersAdapter(context!!,spanCount)
        adapter.onItemClick=this.onItemClick
        AsyncLoader {
            context!!.assets.list(path[0])?.map { "asset:///${path[0]}/$it" } ?: listOf()
        }.load {
            emoji_list = it
            if (ewaiting) adapter.setAll(it)
            eloaded = true
        }
        AsyncLoader {
            context!!.assets.list(path[1])?.map { "asset:///${path[1]}/$it" } ?: listOf()
        }.load {
            meme_face_list = it
            if (mwaiting) adapter.setAll(it)
            mloaded = true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meme_editor_sticker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sticker_list.initWithGrid(spanCount)
        sticker_list.adapter = adapter

        sticker_tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                load(p0?.position ?: 0)
            }
        })
        load()
    }

    val path = listOf("emoji_stickers", "meme_stickers")
    private fun load(pos: Int = 0) {
        if (pos == 0) {
            ewaiting = !eloaded
            mwaiting = false
            if (eloaded)
                adapter.setAll(emoji_list)

        } else {
            ewaiting = false
            mwaiting = !mloaded
            if (mloaded)
                adapter.setAll(meme_face_list)
        }
    }


}
