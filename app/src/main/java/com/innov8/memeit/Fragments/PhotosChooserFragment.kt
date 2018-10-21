package com.innov8.memeit.Fragments

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.afollestad.materialdialogs.MaterialDialog
import com.innov8.memegenerator.MemeEditorActivity
import com.innov8.memegenerator.MemeEngine.MemeLayout
import com.innov8.memegenerator.utils.initWithGrid
import com.innov8.memeit.Activities.Frag
import com.innov8.memeit.Adapters.PhotosAdapter
import com.innov8.memeit.R
import kotlinx.android.synthetic.main.fragment_photo_chooser.*


class PhotosChooserFragment : Frag() {
    private lateinit var photosAdapter: PhotosAdapter

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        photosAdapter = PhotosAdapter(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo_chooser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        meme_template_list.initWithGrid(3)
        meme_template_list.adapter = photosAdapter
        meme_template_list.itemAnimator = null

        multi_select.setOnCheckedChangeListener { _, isChecked ->
            photosAdapter.multiSelectMode = isChecked
        }
        select_done.setOnClickListener {
            MaterialDialog.Builder(context!!)
                    .items("Horizontal Stack", "Vertical Stack", "Grid")
                    .itemsCallback { _, _, position, _ ->
                        MemeEditorActivity.startWithImages(context!!, photosAdapter.selectedItems,
                                MemeLayout.LayoutInfo(if(position<2)1 else 2).apply {
                                    orientation=if(position<2)position else 0
                                })
                    }.show()
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(context!!, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                "${MediaStore.Images.Media.MIME_TYPE}<>?",
                arrayOf("image/gif"),
                "${MediaStore.Images.Media.DATE_ADDED} DESC")
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        photosAdapter.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        photosAdapter.swapCursor(null)
    }
}