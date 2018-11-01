package com.innov8.memeit

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.innov8.memegenerator.MemeEditorActivity
import com.innov8.memegenerator.MemeEngine.MemeLayout.LayoutInfo
import com.innov8.memeit.Adapters.PhotosAdapterList
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mcd_image_list.layoutManager = LinearLayoutManager(context!!, RecyclerView.HORIZONTAL, false)
        mcd_image_list.adapter = photoListAdapter

        val groups = listOf(group_horizontal, group_vertical, group_orientation, group_span)
        fun show(vararg group: Group, showAll: Boolean = false) {
            groups.forEach { it.visibility = if (showAll || group.contains(it)) View.VISIBLE else View.GONE }
        }
        mcd_type_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.mcd_type_horizontal -> show(group_horizontal)
                R.id.mcd_type_vertical -> show(group_vertical)
                R.id.mcd_type_grid -> show(showAll = true)
            }
        }
        mcd_finish.setOnClickListener {
            MemeEditorActivity.startWithImages(context!!, photoListAdapter.items, extractInfo())
        }
        mcd_grid_span.max = photoListAdapter.itemCount.toFloat()

    }

    /*override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog.Builder(context!!)
                .customView(view!!, false)
                .negativeText("Cancel")
                .positiveText("Finish")
                .build()
    }*/

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

        val hSpacing = mcd_spacing_horizontal.progress.dp
        val vSpacing = mcd_spacing_vertical.progress.dp

        val span = mcd_grid_span.progress

        return LayoutInfo(type, span, hSpacing, vSpacing, orientation)
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
