package com.innov8.memegenerator.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.generic.RoundingParams
import com.innov8.memegenerator.adapters.StickersAdapter
import com.innov8.memegenerator.memeEngine.MemeStickerView
import com.innov8.memegenerator.interfaces.StickerEditInterface
import com.innov8.memegenerator.models.StickerPack
import com.innov8.memegenerator.R
import com.innov8.memegenerator.StickerEditorActivity
import com.innov8.memegenerator.utils.onTabSelected
import com.innov8.memeit.commons.toast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
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
            create_sticker.visibility = if (tab.position == 3) View.VISIBLE else View.GONE
        }
        create_sticker.setOnClickListener {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(1, 1)
//                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(context!!, this)
        }
        load()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                StickerEditorActivity.startWithBitmapUri(context!!, result.uri.toFile().absolutePath)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                context?.toast("Cant read Image File")
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