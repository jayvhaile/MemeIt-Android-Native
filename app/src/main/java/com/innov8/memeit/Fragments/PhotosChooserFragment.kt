package com.innov8.memeit.Fragments

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import com.innov8.memegenerator.MemeEditorActivity
import com.innov8.memeit.Activities.Frag
import com.innov8.memeit.Adapters.PhotosAdapter
import com.innov8.memeit.R
import com.innov8.memeit.snack
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_photo_chooser.*


class PhotosChooserFragment : Frag() {
    private lateinit var photosAdapter: PhotosAdapter

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        photosAdapter = PhotosAdapter(context!!)
        photosAdapter.onCropListener = {
            CropImage.activity(Uri.parse(it))
                    .start(context!!, this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo_chooser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        meme_template_list.layoutManager = GridLayoutManager(context, 3)
        meme_template_list.adapter = photosAdapter
        meme_template_list.itemAnimator = null

        multi_select.setOnCheckedChangeListener { _, isChecked ->
            photosAdapter.multiSelectMode = isChecked
        }
        select_done.setOnClickListener {
            val d = MultiChooserDialogFragment
                    .newInstance(photosAdapter.selectedItems.toTypedArray())
            d.setTargetFragment(this, 0)
            d.show(fragmentManager, "dd")


            /*
            MaterialDialog.Builder(context!!)
                    .customView(R.layout.list_item_thumbnail_selectable,false)
                    .items("Horizontal Stack", "Vertical Stack", "Grid")
                    .itemsCallback { _, _, position, _ ->
                        MemeEditorActivity.startWithImages(context!!, photosAdapter.selectedItems,
                                MemeLayout.LayoutInfo(if (position < 2) 1 else 2).apply {
                                    orientation = if (position < 2) position else 0
                                })
                    }.show()*/
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                MemeEditorActivity.startWithImage(context!!, result.uri.toString())
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                meme_template_list?.snack(result.error.message ?: "Could not load Image")
            }
        } else
            super.onActivityResult(requestCode, resultCode, data)

    }
}