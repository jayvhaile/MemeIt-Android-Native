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


    var lists= mutableListOf<List<String>>(listOf(), listOf(), listOf())

    var onItemClick: ((String) -> Unit)={url->
        AsyncLoader {
            val x=url.substring(9)
            //todo downsample the bitmaps
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
        adapter = StickersAdapter(context!!)
        adapter.onItemClick=this.onItemClick
        path.forEachIndexed { i, s ->
            AsyncLoader {
                context!!.assets.list(s)?.map { "asset:///$s/$it" } ?: listOf()
            }.load {
                lists[i]= it
                if (waiting[i]) adapter.setAll(it)
                loaded[i] = true
            }
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

    val loaded= mutableListOf(false,false,false)
    val waiting= mutableListOf(false,false,false)

    val path = listOf("emoji_stickers", "meme_stickers","bubbles")
    private fun load(pos: Int = 0) {
        waiting[pos]=!loaded[pos]
        waiting.forEachIndexed {i, b->
            if(i!=pos)waiting[i]=false
        }
        if(loaded[pos])
            adapter.setAll(lists[pos])
    }


}
