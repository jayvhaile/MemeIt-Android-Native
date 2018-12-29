package com.innov8.memeit.fragments

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
import com.innov8.memeit.activities.Frag
import com.innov8.memeit.adapters.PhotosAdapter
import com.innov8.memeit.R
import com.innov8.memeit.utils.snack
import com.innov8.memeit.commons.toast
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

        photosAdapter.onSelectedItemChanged = {
            if (multi_select.isChecked) {
                val s = it.size
                select_done.visibility = View.VISIBLE
                toolbar_text.text = "$s Photo${if (s > 0) "s" else ""} Selected"
            } else {
                toolbar_text.text = "Select Multiple"
                select_done.visibility = View.GONE
            }
        }
        multi_select.setOnCheckedChangeListener { _, isChecked ->
            photosAdapter.multiSelectMode = isChecked

        }
        select_done.setOnClickListener {
            val items = photosAdapter.selectedItems
            if (items.size < 2) context!!.toast("Select at least 2 photos to continue")
            else
                MultiChooserDialogFragment
                        .newInstance(items.toTypedArray())
                        .apply {
                            setTargetFragment(this@PhotosChooserFragment, 0)
                        }.show(fragmentManager, "d")

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
                MemeEditorActivity.startWithImage(activity!!, result.uri.toString())
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                meme_template_list?.snack(result.error.message ?: "Could not load Image")
            }
        } else
            super.onActivityResult(requestCode, resultCode, data)

    }
}