package com.innov8.memeit.Activities

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.database.Cursor
import android.os.Bundle
import android.os.FileObserver
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
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
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import androidx.work.WorkManager
import com.innov8.memegenerator.MemeEditorActivity
import com.innov8.memeit.Adapters.DraftsAdapter
import com.innov8.memeit.Adapters.GifAdapter
import com.innov8.memeit.Adapters.MemeUploadTaskAdapter
import com.innov8.memeit.Adapters.VideoAdapter
import com.innov8.memeit.CustomViews.DrawableBadge
import com.innov8.memeit.Fragments.MemeTemplateHolderFragment
import com.innov8.memeit.Fragments.PhotosChooserFragment
import com.innov8.memeit.Loaders.DraftLoader
import com.innov8.memeit.R
import com.innov8.memeit.Utils.LoaderAdapterHandler
import com.innov8.memeit.Utils.makeLinear
import com.innov8.memeit.commons.dp
import com.memeit.backend.models.MemeTemplate
import kotlinx.android.synthetic.main.activity_meme_chooser.*
import kotlinx.android.synthetic.main.fragment_meme_templates.*
import kotlinx.android.synthetic.main.fragment_ongoing_upload.*

class MemeChooserActivity : AppCompatActivity() {
    private lateinit var pagerAdapter: MemeChooserPagerAdapter
    private lateinit var fileObserver: FileObserver
    private lateinit var closedSet: ConstraintSet
    var opened = false
    private val openedSet by lazy {
        ConstraintSet().apply {
            clone(closedSet)
            setVisibility(R.id.overlay, View.VISIBLE)
            constrainHeight(R.id.draft_list, 128.dp(this@MemeChooserActivity))
        }
    }

    private val loader by lazy {
        DraftLoader()
    }
    private val adapter by lazy {
        DraftsAdapter(this)
    }
    private val handler by lazy {
        LoaderAdapterHandler(adapter, loader).apply {
            onLoaded = {
                onDraftChanged(MemeTemplate.getDraftsJsonDir(this@MemeChooserActivity).listFiles().size)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_chooser)
        pagerAdapter = MemeChooserPagerAdapter(supportFragmentManager)
        pager.adapter = pagerAdapter
        tabs.setupWithViewPager(pager)
        closedSet = ConstraintSet().apply {
            clone(root)
        }
        draft_list.apply {
            makeLinear(RecyclerView.HORIZONTAL)
            adapter = this@MemeChooserActivity.adapter
        }


        fileObserver = object : FileObserver(MemeTemplate.getDraftsJsonDir(this).absolutePath) {
            override fun onEvent(event: Int, path: String) {
                when (event) {
                    FileObserver.CREATE,
                    FileObserver.MODIFY,
                    FileObserver.DELETE,
                    FileObserver.DELETE_SELF -> {
                        handler.refresh()
                    }
                }

            }
        }
        draft_btn.setOnClickListener {
            toggle()
        }
        handler.load()
        fileObserver.startWatching()
    }

    private fun makeTransitioon(): TransitionSet {
        return TransitionSet().apply {
            addTransition(Fade(Fade.IN).apply {
                addTarget(R.id.overlay)
            })
            addTransition(Fade(Fade.OUT).apply {
                addTarget(R.id.overlay)
            })
            addTransition(ChangeBounds().apply {
                addTarget(R.id.draft_list)
                interpolator = AccelerateDecelerateInterpolator()
            })
        }
    }

    private fun toggle() {
        if (opened) close()
        else open()
    }


    private fun open() {
        if (!opened) {
            TransitionManager.beginDelayedTransition(root, makeTransitioon())
            openedSet.applyTo(root)
            opened = true
        }
    }

    private fun close() {
        if (opened) {
            TransitionManager.beginDelayedTransition(root, makeTransitioon())
            closedSet.applyTo(root)
            opened = false
        }
    }

    override fun onBackPressed() {
        if (opened) {
            close()
        } else super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
//        fileObserver.startWatching()
    }

    override fun onStop() {
//        fileObserver.stopWatching()
        super.onStop()
    }

    private fun onDraftChanged(count: Int) {
        if (count > 0) {
            draft_btn.apply {
                visibility = View.VISIBLE
                setImageDrawable(DrawableBadge.Builder(this@MemeChooserActivity)
                        .drawableResId(R.drawable.ic_mode_edit_black_24dp)
                        .maximumCounter(99)
                        .build()
                        .get(count)
                )
            }
        } else draft_btn.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MemeEditorActivity.REQUEST_CODE) {
            if (resultCode == MemeEditorActivity.RESULT_CODE_SUCCESS_MEME)
                startActivity(Intent(this, MemePosterActivity::class.java).apply {
                    putExtras(data!!)
                })
            else if (resultCode == MemeEditorActivity.RESULT_CODE_SUCCESS_TEMPLATE)
                startActivity(Intent(this, MemeTemplatePosterActivity::class.java).apply {
                    putExtras(data!!)
                })
        }
    }

    class MemeChooserPagerAdapter(mgr: FragmentManager) : FragmentPagerAdapter(mgr) {
        private val titles = listOf("Templates", "Photos", "Gifs", "Videos")
        override fun getCount(): Int = titles.size
        override fun getItem(position: Int): Fragment =
                when (position) {
                    0 -> MemeTemplateHolderFragment()
                    1 -> PhotosChooserFragment()
                    2 -> GifChooserFragment()
                    3 -> VideoChooserFragment()
                    else -> OngoingUploadFragment()
                }

        override fun getPageTitle(position: Int): String = titles[position]
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
            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), REQUEST_PERMS)
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
