package com.innov8.memegenerator.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
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
import com.innov8.memegenerator.memeEngine.MemeStickerView
import com.innov8.memegenerator.models.StickerPack
import com.innov8.memegenerator.utils.onTabSelected
import kotlinx.android.synthetic.main.bottom_tab.*
import kotlinx.android.synthetic.main.sticker_frag.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import java.io.File
import java.io.FileInputStream

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
                    if (pager_tab.selectedTabPosition == 3)
                        BitmapFactory.decodeStream(FileInputStream(File(url.substring(5))))
                    else
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
            if (tab.position == 3) {
                stickersAdapter.clear()
                loadMyStickers()
            }
            stickersAdapter.setAll(stickers!![tab.position].urls.map { it.path })
            create_sticker.visibility = if (tab.position == 3) View.VISIBLE else View.GONE
        }
        create_sticker.setOnClickListener {
            startActivityForResult(Intent(context!!,
                    Class.forName("com.innov8.memeit.activities.PhotoChooserActivity")),
                    260)
        }
        load()
    }

    private fun loadMyStickers() {
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val result = withContext(Dispatchers.Default) {
                StickerPack.myStickersDir(context!!).listFiles()
                        .map { Uri.fromFile(it).toString() }
                        .reversed()
            }
            stickersAdapter.setAll(result)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 260 && resultCode == 265 && data != null) {
            StickerEditorActivity.startWithBitmapUri(this, data.getStringExtra("url"))
        } else if (requestCode == StickerEditorActivity.REQUEST_CODE &&
                resultCode == StickerEditorActivity.RESULT_CODE) {
            if (pager_tab.selectedTabPosition == 3) {
                loadMyStickers()
            }
        }
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