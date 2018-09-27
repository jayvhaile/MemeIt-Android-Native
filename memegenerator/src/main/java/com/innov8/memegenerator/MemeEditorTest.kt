package com.innov8.memegenerator

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import androidx.viewpager.widget.PagerAdapter
import com.google.gson.Gson
import com.innov8.memegenerator.adapters.StickersAdapter
import com.innov8.memegenerator.adapters.TextPresetsAdapter
import com.innov8.memegenerator.customViews.ColorChooser
import com.innov8.memegenerator.customViews.FontChooser
import com.innov8.memegenerator.customViews.MarginControl
import com.innov8.memegenerator.memeEngine.LayoutEditInterface
import com.innov8.memegenerator.memeEngine.MemeStickerView
import com.innov8.memegenerator.memeEngine.StickerEditInterface
import com.innov8.memegenerator.memeEngine.TextEditListener
import com.innov8.memegenerator.models.MemeTemplate
import com.innov8.memegenerator.models.TextPreset
import com.innov8.memegenerator.utils.AsyncLoader
import com.innov8.memegenerator.utils.dp
import com.innov8.memegenerator.utils.makeFullScreen
import com.innov8.memegenerator.utils.onTabSelected
import com.warkiz.widget.IndicatorSeekBar
import kotlinx.android.synthetic.main.bottom_tab.*
import kotlinx.android.synthetic.main.meme_editor.*
import kotlinx.android.synthetic.main.sticker_frag.*
import kotlinx.android.synthetic.main.text_pager.*

class MemeEditorTest : AppCompatActivity() {
    val contraintSet1 = ConstraintSet()
    val opened
        get() = closeableFragments[current] != null

    lateinit var closeableFragments: Map<String, CloseableFragment>
    var current = "none"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.meme_editor)

        contraintSet1.clone(contraint_layout)

        val lf = LayoutFrag()
        lf.layoutEditListener = memeEditorView2.layoutEditInterface
        val sf = StickerFrag()
        sf.stickerEditInterface = memeEditorView2.stickerEditInterface
        closeableFragments = mapOf(
                "layout" to CloseableFragment(lf, 152.dp(this)),
                "text" to CloseableFragment(TextEditor(), 152.dp(this), TextPresetFragment(), 80.dp(this)),
                "sticker" to CloseableFragment(sf, (80 + 56).dp(this))
        )
        layout.setOnClickListener {
            open("layout")
        }
        text.setOnClickListener {
            open("text")
        }
        sticker.setOnClickListener {
            open("sticker")
        }

        done.setOnClickListener {
            val bitmap = memeEditorView2.captureMeme()
            val intent = Intent(this, MemePosterActivity::class.java)
            intent.putExtra("texts", memeEditorView2.getTexts().toTypedArray())
            MemePosterActivity.bitmap = bitmap
            startActivity(intent)
        }

        val json: String? = intent.getStringExtra("string")
        val uri: String? = intent.getStringExtra("uri")

        if (json != null) {
            val gson = Gson()
            val memeTemplate = gson.fromJson(json, MemeTemplate::class.java)
            memeEditorView2.loadMemeTemplate(memeTemplate)
        } else if (uri != null) {
            AsyncLoader<Bitmap> {
                val stream = contentResolver.openInputStream(Uri.parse(uri))
                BitmapFactory.decodeStream(stream)
            }.load {
                memeEditorView2.loadBitmab(it)
            }

        }


    }

    class CloseableFragment(val bottomFragment: Fragment? = null, val bottomSize: Int = 0,
                            val topFragment: Fragment? = null, val topSize: Int = 0)

    private fun open(tag: String) {
        val cf = closeableFragments[tag]
        if (cf == null) {
            current = "none"
            return
        }
        val constraintSetOpened = ConstraintSet()
        constraintSetOpened.clone(contraint_layout)
        val transaction = supportFragmentManager.beginTransaction()

        if (cf.topFragment != null) {
            constraintSetOpened.setGuidelineBegin(R.id.guideline_top, 0)
            constraintSetOpened.constrainHeight(R.id.top_overlay, cf.topSize)
            constraintSetOpened.clear(R.id.top_overlay, ConstraintSet.BOTTOM)
            transaction.replace(R.id.top_overlay, cf.topFragment)
        }
        if (cf.bottomFragment != null) {
            constraintSetOpened.setGuidelineEnd(R.id.guideline_bottom, 0)
            constraintSetOpened.constrainHeight(R.id.bottom_overlay, cf.bottomSize)
            constraintSetOpened.clear(R.id.bottom_overlay, ConstraintSet.TOP)
            transaction.replace(R.id.bottom_overlay, cf.bottomFragment)
        }
        if (!opened) {
            TransitionManager.beginDelayedTransition(contraint_layout)
            constraintSetOpened.applyTo(contraint_layout)

        }
        transaction.commit()
        current = tag
    }

    private fun close() {
        TransitionManager.beginDelayedTransition(contraint_layout)
        contraintSet1.applyTo(contraint_layout)
        current = "none"
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) makeFullScreen()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        makeFullScreen()
    }

    override fun onBackPressed() {
        if (opened) close()
        else
            super.onBackPressed()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        open(savedInstanceState?.getString("current") ?: "none")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("current", current)
    }

}

class LayoutFrag : Fragment() {
    private val views = mutableListOf<View>()

    var layoutEditListener: LayoutEditInterface? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mc = MarginControl(context!!)
        mc.layoutEditInterface = layoutEditListener
        views.add(mc)
        val cc = ColorChooser(context!!)
        cc.onColorChoosed = {
            layoutEditListener?.onBackgroundColorChanged(it)
        }
        views.add(cc)
        text_pager.adapter = Adapter(context!!)
        pager_tab.setupWithViewPager(text_pager)
    }


    inner class Adapter(context: Context) : ViewAdapter(context) {
        private var titles = listOf("Margin", "Background")


        init {

        }

        override fun getItem(position: Int): View = views[position]


        override fun getCount(): Int = titles.size

        override fun getPageTitle(position: Int): CharSequence? = titles[position]

    }
}

class TextEditor : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.text_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text_pager.adapter = Adapter(context!!)
        pager_tab.setupWithViewPager(text_pager)
    }


    inner class Adapter(context: Context) : ViewAdapter(context) {
        private var titles = listOf("Color", "Size", "Font", "Style", "Stroke", "Background")

        val views = MutableList(titles.size) {
            ColorChooser(context) as View
        }

        init {
            views[1] = IndicatorSeekBar.with(context!!)
                    .min(5f)
                    .max(100f)
                    .tickCount(20)
                    .build()
            views[2] = FontChooser(context)
        }

        override fun getItem(position: Int): View = views[position]


        override fun getCount(): Int = titles.size

        override fun getPageTitle(position: Int): CharSequence? = titles[position]


    }


}

class TextPresetFragment : Fragment() {
    var textEditListener: TextEditListener? = null
    private lateinit var textPresetsAdapter: TextPresetsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textPresetsAdapter = TextPresetsAdapter(context!!)
        textPresetsAdapter.onItemClick = {
            textEditListener?.onApplyAll(it.textStyleProperty, false)
        }
        TextPreset.loadPresets { textPresetsAdapter.setAll(it) }

    }

    private lateinit var presetList: androidx.recyclerview.widget.RecyclerView
    private lateinit var presetAdd: Button
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_text_presets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presetList = view.findViewById(R.id.frag_text_preset_list)
        presetAdd = view.findViewById(R.id.frag_text_preset_add)
        presetList.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        presetList.adapter = textPresetsAdapter
    }
}

class StickerPack(val name: String, val urls: List<String>) {
    companion object {
        private val stickers = mapOf("Emojis" to "emoji_stickers",
                "Meme Faces" to "meme_stickers",
                "Chat Bubbles" to "bubbles")

        fun load(context: Context, onLoaded: (List<StickerPack>) -> Unit) {
            AsyncLoader {
                val list = MutableList(stickers.size) { index: Int ->
                    val (name, path) = stickers.toList()[index]
                    val urls = context.assets.list(path)?.map { "asset:///$path/$it" }
                            ?: listOf()
                    StickerPack(name, urls)
                }
                list.add(StickerPack("My Stickers", listOf()))//todo load my stickers
                list
            }.load(onLoaded)
        }
    }


}

class StickerFrag : Fragment() {
    lateinit var stickersAdapter: StickersAdapter
    var stickers: List<StickerPack>? = null
    var loaded = false
    var load = false
    var stickerEditInterface: StickerEditInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stickersAdapter = StickersAdapter(context!!)
        StickerPack.load(context!!) {
            stickers = it
            if (load) load()
        }

        stickersAdapter.onItemClick = { url ->
            AsyncLoader {
                val x = url.substring(9)
                BitmapFactory.decodeStream(context!!.assets.open(x))
            }.load { bitmap ->
                val memeStickerView = MemeStickerView(context!!, bitmap)
                stickerEditInterface?.onAddSticker(memeStickerView)
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
        pager_tab.onTabSelected {
            stickersAdapter.setAll(stickers!![it.position].urls)
        }
        load()
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

abstract class ViewAdapter(val context: Context) : PagerAdapter() {


    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`
    final override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = getItem(position);
        container.addView(view)
        return view
    }

    abstract fun getItem(position: Int): View
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

}
