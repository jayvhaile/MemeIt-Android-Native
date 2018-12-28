package com.innov8.memegenerator.Fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memegenerator.Adapters.StickersAdapter
import com.innov8.memegenerator.MemeEngine.MemeStickerView
import com.innov8.memegenerator.interfaces.StickerEditInterface
import com.innov8.memegenerator.Models.StickerPack
import com.innov8.memegenerator.R
import com.innov8.memegenerator.utils.onTabSelected
import kotlinx.android.synthetic.main.bottom_tab.*
import kotlinx.android.synthetic.main.sticker_frag.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main

class StickerChooserFragment : Fragment() {
    private lateinit var stickersAdapter: StickersAdapter
    private var stickers: List<StickerPack>? = null
    private var loaded = false
    private var load = false
    var stickerEditInterface: StickerEditInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stickersAdapter = StickersAdapter(context!!)
        StickerPack.load(context!!) {
            stickers = it
            if (load) load()
        }

        stickersAdapter.onItemClick = { url ->
            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                val bitmap = withContext(Dispatchers.Default) {
                    BitmapFactory.decodeStream(context!!.assets.open(url.substring(9)))
                }
                stickerEditInterface?.onAddSticker(MemeStickerView(context!!, bitmap, url))
            }
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
            stickersAdapter.setAll(stickers!![tab.position].urls.map { it.path })
        }
        load()
    }

    private fun load() {
        if (stickers != null) {
            if (!loaded) {
                stickers!!.forEachIndexed { index, it ->
                    pager_tab.addTab(pager_tab.newTab().setText(it.name), index == 0)
                }
                loaded = true
            }
        } else load = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loaded = false
        load = false
    }

}