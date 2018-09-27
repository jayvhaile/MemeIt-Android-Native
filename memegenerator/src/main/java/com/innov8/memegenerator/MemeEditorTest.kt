package com.innov8.memegenerator

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import androidx.viewpager.widget.PagerAdapter
import com.innov8.memegenerator.adapters.StickersAdapter
import com.innov8.memegenerator.adapters.TextPresetsAdapter
import com.innov8.memegenerator.customViews.ColorChooser
import com.innov8.memegenerator.customViews.FontChooser
import com.innov8.memegenerator.memeEngine.TextEditListener
import com.innov8.memegenerator.models.MemeTemplate
import com.innov8.memegenerator.models.MyTypeFace
import com.innov8.memegenerator.models.TextPreset
import com.innov8.memegenerator.models.TextStyleProperty
import com.innov8.memegenerator.utils.AsyncLoader
import com.innov8.memegenerator.utils.dp
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

        closeableFragments = mapOf(
                "text" to CloseableFragment(TextEditor(), 152.dp(this)),
                "sticker" to CloseableFragment(StickerFrag(), (80+56).dp(this))
        )
        text.setOnClickListener {
            open("text")
        }
        sticker.setOnClickListener {
            open("sticker")
        }

        MemeTemplate.loadLocalTemplates(this) {
            memeEditorView2.loadMemeTemplate(it[3])
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

fun Activity.makeFullScreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    } else {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
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


    class Adapter(context: Context) : ViewAdapter(context) {
        private var titles = listOf("Color", "Size", "Font", "Style", "Stroke", "Background")

        val views = MutableList(titles.size) {
            val hsv = HorizontalScrollView(context)
            hsv.addView(ColorChooser(context))
            hsv.isHorizontalScrollBarEnabled = false
            hsv as View
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
}
class TextPresetFragment : androidx.fragment.app.Fragment() {
    lateinit var textEditListener: TextEditListener
    private var asyncLoaders: AsyncLoader<List<TextPreset>>? = null
    private lateinit var textPresetsAdapter: TextPresetsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textPresetsAdapter = TextPresetsAdapter(context!!)
        textPresetsAdapter.onItemClick = {
            textEditListener.onApplyAll(it.textStyleProperty, false)
        }
        asyncLoaders = AsyncLoader {
            listOf(
                    TextPreset("Normal", TextStyleProperty(
                            20f, Color.WHITE, MyTypeFace.byName("Arial")!!,
                            false, false, false,
                            true, Color.BLACK, 10f
                    )),
                    TextPreset("Meme", TextStyleProperty(
                            20f, Color.WHITE, MyTypeFace.byName("Impact")!!,
                            false, false, true,
                            true, Color.BLACK, 10f
                    )),
                    TextPreset("Red", TextStyleProperty(
                            20f, Color.RED, MyTypeFace.byName("Pacifico")!!
                    )),
                    TextPreset("Dialog", TextStyleProperty(
                            20f, Color.YELLOW, MyTypeFace.byName("Ubuntu")!!,
                            false, false, false,
                            false, Color.BLACK, 10f
                    ))
            )
        }
    }

    private lateinit var presetList: androidx.recyclerview.widget.RecyclerView
    private lateinit var presetAdd: Button
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_text_presets, container, false)
        initViews(view)
        load()
        return view
    }

    private fun initViews(view: View) {
        presetList = view.findViewById(R.id.frag_text_preset_list)
        presetAdd = view.findViewById(R.id.frag_text_preset_add)

        presetList.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        presetList.adapter = textPresetsAdapter
    }

    fun load() {
        asyncLoaders?.load { textPresetsAdapter.addAll(it) }
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
                    val urls = context.assets.list(path)?.map { "asset:///$path/$it" } ?: listOf()
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stickersAdapter = StickersAdapter(context!!)
        StickerPack.load(context!!) {
            stickers = it
            if (load) load()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance=true
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
                    pager_tab.addTab(pager_tab.newTab().setText(it.name), index==0)
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
