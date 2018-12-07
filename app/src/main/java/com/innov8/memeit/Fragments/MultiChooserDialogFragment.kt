package com.innov8.memeit.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.innov8.memegenerator.MemeEditorActivity
import com.innov8.memegenerator.MemeEngine.MemeLayout.LayoutInfo
import com.innov8.memeit.Adapters.PhotosAdapterList
import com.innov8.memeit.R
import com.innov8.memeit.Utils.snack
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_multi_chooser_dialog.*
import kotlinx.android.synthetic.main.fragment_photo_chooser.*

class MultiChooserDialogFragment : BottomSheetDialogFragment() {

    lateinit var photoListAdapter: PhotosAdapterList
    private var currentCrop: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoListAdapter = PhotosAdapterList(context!!)
        photoListAdapter.onCropListener = {
            currentCrop = it
            CropImage.activity(Uri.parse(it))
                    .start(context!!, this)
        }
        arguments?.getStringArray("photos")?.let {
            photoListAdapter.addAll(it.toList())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_multi_chooser_dialog, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mcd_image_list.layoutManager = LinearLayoutManager(context!!, RecyclerView.HORIZONTAL, false)
        mcd_image_list.adapter = photoListAdapter


        mcd_type_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.mcd_type_horizontal -> option_card.visibility = View.GONE
                R.id.mcd_type_vertical -> option_card.visibility = View.GONE
                R.id.mcd_type_grid -> option_card.visibility = View.VISIBLE
            }
        }
        mcd_finish.setOnClickListener {
            MemeEditorActivity.startWithImages(context!!, photoListAdapter.items, extractInfo())
        }
        mcd_grid_span.max = photoListAdapter.itemCount.toFloat()

    }


    fun extractInfo(): LayoutInfo {
        val type = if (mcd_type_group.checkedRadioButtonId == R.id.mcd_type_grid) LayoutInfo.TYPE_GRID
        else LayoutInfo.TYPE_LINEAR

        val orientation = if (mcd_type_group.checkedRadioButtonId == R.id.mcd_type_grid) {
            when (mcd_orientation_group.checkedRadioButtonId) {
                R.id.mcd_orientation_vertical -> 1
                else -> 0
            }
        } else {
            when (mcd_type_group.checkedRadioButtonId) {
                R.id.mcd_type_vertical -> 1
                else -> 0
            }
        }
        val span = mcd_grid_span.progress

        return LayoutInfo(type, span, orientation)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val index = photoListAdapter.items.indexOf(currentCrop!!)
                photoListAdapter.items[index] = result.uri.toString()
                photoListAdapter.notifyItemChanged(index)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                meme_template_list?.snack(result.error.message ?: "Could not load Image")
            }
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        fun newInstance(items: Array<String>): MultiChooserDialogFragment {
            val df = MultiChooserDialogFragment()
            df.arguments = Bundle().apply {
                putStringArray("photos", items)
            }
            return df
        }

    }

}
