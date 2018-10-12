package com.innov8.memeit.Activities

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.google.gson.Gson
import com.innov8.memegenerator.MemeEditorActivity
import com.innov8.memegenerator.Models.MemeTemplate
import com.innov8.memegenerator.utils.initWithGrid
import com.innov8.memeit.Adapters.MemeTemplatesListAdapter
import com.innov8.memeit.Adapters.PhotosAdapter
import com.innov8.memeit.Adapters.VideoAdapter
import com.innov8.memeit.R
import kotlinx.android.synthetic.main.activity_meme_chooser.*
import kotlinx.android.synthetic.main.fragment_meme_templates.*


class MemeChooser : AppCompatActivity() {
    private lateinit var adapter: MemeChooserPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_chooser)
        adapter = MemeChooserPagerAdapter(supportFragmentManager)
        pager.adapter = adapter
        tabs.setupWithViewPager(pager)
    }
}

class MemeChooserPagerAdapter(mgr: FragmentManager) : FragmentPagerAdapter(mgr) {
    private val titles = listOf("Templates", "Photos", "Videos")
    override fun getCount(): Int = 3
    override fun getItem(position: Int): Fragment =
            when (position) {
                0 -> TemplateFragment()
                1 -> PhotosChooserFragment()
                2 -> GifChooserFragment()
                else -> TemplateFragment()
            }
    override fun getPageTitle(position: Int): String? = titles[position]
}

class TemplateFragment : Fragment() {
    private lateinit var memeTemplatesListAdapter: MemeTemplatesListAdapter
    private lateinit var gson: Gson


    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        memeTemplatesListAdapter = MemeTemplatesListAdapter(context!!)
        gson = Gson()
        MemeTemplate.loadLocalTemplates(context!!) {
            memeTemplatesListAdapter.addAll(it)
        }

        memeTemplatesListAdapter.OnItemClicked = {
            val json = gson.toJson(it)
            val intent = Intent(context, MemeEditorActivity::class.java)
            intent.putExtra("string", json)
            startActivity(intent)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meme_templates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        meme_template_list.initWithGrid(3)
        meme_template_list.adapter = memeTemplatesListAdapter
    }


}

private const val REQUEST_PERMS = 120

abstract class Frag : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    private var inPermission = false
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        inPermission = state?.getBoolean("inperm", false) ?: false

        if (hasFilesPermission()) {
            load()
        } else if (!inPermission) {
            inPermission = true
            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE),
                    REQUEST_PERMS)
        }
    }

    fun load() {
        LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    private fun hasFilesPermission(): Boolean {
        return checkSelfPermission(context!!, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        inPermission = false
        if (requestCode == REQUEST_PERMS) {
            if (hasFilesPermission()) {
                load()
            } else {
                //todo show em not working without permission
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("inperm", inPermission)
    }

}

class PhotosChooserFragment : Frag() {
    private lateinit var photosAdapter: PhotosAdapter

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        photosAdapter = PhotosAdapter(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meme_templates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        meme_template_list.initWithGrid(3)
        meme_template_list.adapter = photosAdapter
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(context!!, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                "${MediaStore.Images.Media.DATE_ADDED} DESC")
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        photosAdapter.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        photosAdapter.swapCursor(null)
    }
}

class VideoChooserFragment : Frag() {
    private lateinit var videosAdapter: VideoAdapter
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        videosAdapter = VideoAdapter(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meme_templates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        meme_template_list.initWithGrid(3)
        meme_template_list.adapter = videosAdapter
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(context!!, MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                "${MediaStore.Video.Media.DATE_ADDED} DESC")
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        videosAdapter.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        videosAdapter.swapCursor(null)
    }
}
class GifChooserFragment : Frag() {
    private lateinit var videosAdapter: VideoAdapter
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        videosAdapter = VideoAdapter(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meme_templates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        meme_template_list.initWithGrid(3)
        meme_template_list.adapter = videosAdapter
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {

        return CursorLoader(context!!, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                "${MediaStore.Images.Media.MIME_TYPE}=?",
                arrayOf("image/gif"),
                "${MediaStore.Video.Media.DATE_ADDED} DESC")
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        videosAdapter.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        videosAdapter.swapCursor(null)
    }
}
