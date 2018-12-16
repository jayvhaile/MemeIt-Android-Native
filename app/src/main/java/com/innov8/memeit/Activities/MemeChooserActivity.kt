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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.google.gson.Gson
import com.innov8.memegenerator.MemeEditorActivity
import com.innov8.memeit.Adapters.GifAdapter
import com.innov8.memeit.Adapters.MemeTemplatesListAdapter
import com.innov8.memeit.Adapters.MemeUploadTaskAdapter
import com.innov8.memeit.Adapters.VideoAdapter
import com.innov8.memeit.Fragments.PhotosChooserFragment
import com.innov8.memeit.R
import com.innov8.memeit.commons.models.MemeTemplate
import kotlinx.android.synthetic.main.activity_meme_chooser.*
import kotlinx.android.synthetic.main.fragment_meme_templates.*
import kotlinx.android.synthetic.main.fragment_ongoing_upload.*


class MemeChooserActivity : AppCompatActivity() {
    private lateinit var adapter: MemeChooserPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_chooser)
        adapter = MemeChooserPagerAdapter(supportFragmentManager)
        pager.adapter = adapter
        tabs.setupWithViewPager(pager)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MemeEditorActivity.REQUEST_CODE && resultCode == MemeEditorActivity.RESULT_CODE_SUCCESS) {
            startActivity(Intent(this, MemePosterActivity::class.java).apply {
                putExtras(data!!)
            })
        }
    }
}

class MemeChooserPagerAdapter(mgr: FragmentManager) : FragmentPagerAdapter(mgr) {
    private val titles = listOf("Templates", "Photos", "Gifs","Videos")
    override fun getCount(): Int = titles.size
    override fun getItem(position: Int): Fragment =
            when (position) {
                0 -> TemplateFragment()
                1 -> PhotosChooserFragment()
                2 -> GifChooserFragment()
                3 -> VideoChooserFragment()
                else -> OngoingUploadFragment()
            }

    override fun getPageTitle(position: Int): String = titles[position]
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
        memeTemplatesListAdapter.onItemClicked = {
            MemeEditorActivity.startWithMemeTemplate(activity!!, it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meme_templates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        meme_template_list.layoutManager = GridLayoutManager(context, 3)
        meme_template_list.adapter = memeTemplatesListAdapter
        meme_template_list.itemAnimator = null
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


class GifChooserFragment : Frag() {
    private lateinit var videosAdapter: GifAdapter
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        videosAdapter = GifAdapter(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meme_templates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        meme_template_list.layoutManager = GridLayoutManager(context, 3)
        meme_template_list.adapter = videosAdapter
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {

        return CursorLoader(context!!, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                "${MediaStore.Images.Media.MIME_TYPE}=?",
                arrayOf("image/gif"),
                "${MediaStore.Images.Media.DATE_ADDED} DESC")
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        videosAdapter.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        videosAdapter.swapCursor(null)
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
        meme_template_list.layoutManager = GridLayoutManager(context, 3)
        meme_template_list.adapter = videosAdapter
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {

        return CursorLoader(context!!, MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null,
                "${MediaStore.Video.Media.MIME_TYPE}=?",
                arrayOf("video/mp4"),
                "${MediaStore.Video.Media.DATE_ADDED} DESC")
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        videosAdapter.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        videosAdapter.swapCursor(null)
    }
}

class OngoingUploadFragment : Fragment() {

    val adapter by lazy {
        MemeUploadTaskAdapter(context!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProviders.of(this).get(OnGoingUploadViewmodel::class.java)
        viewModel.uploadTasks.observe(this, Observer {
            adapter.setAll(it)
            adapter.emptyActionText
            upload_toolbar.title = "${it.size} uploads running"
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ongoing_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        upload_tasks_list.layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
        upload_tasks_list.adapter = adapter
        upload_toolbar.inflateMenu(R.menu.upload_task_menu)
        upload_toolbar.setOnMenuItemClickListener {
            WorkManager.getInstance().cancelAllWorkByTag("meme_upload")
            true
        }
    }

}

class OnGoingUploadViewmodel : ViewModel() {
    val uploadTasks = WorkManager.getInstance().getWorkInfosByTagLiveData("meme_upload")
}
