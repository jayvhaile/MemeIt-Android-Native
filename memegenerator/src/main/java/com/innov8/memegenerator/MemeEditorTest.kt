package com.innov8.memegenerator

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.transition.TransitionManager
import androidx.viewpager.widget.PagerAdapter
import com.innov8.memegenerator.customViews.ColorChooser
import com.innov8.memegenerator.models.MemeTemplate
import com.innov8.memegenerator.utils.fromDPToPX
import kotlinx.android.synthetic.main.meme_editor.*
import kotlinx.android.synthetic.main.text_pager.*

class MemeEditorTest : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.meme_editor)

        val contraintSet1 = ConstraintSet()
        val contraintSet2 = ConstraintSet()

        contraintSet2.clone(contraint_layout)
        contraintSet2.setGuidelineBegin(R.id.guideline_top, 0)
        contraintSet2.setGuidelineEnd(R.id.guideline_bottom, 0)


        contraintSet2.constrainHeight(R.id.top_overlay, 100.fromDPToPX(this))
        contraintSet2.constrainHeight(R.id.bottom_overlay, (56 + 56+40).fromDPToPX(this))
        contraintSet2.clear(R.id.top_overlay, ConstraintSet.BOTTOM)
        contraintSet2.clear(R.id.bottom_overlay, ConstraintSet.TOP)

        contraintSet1.clone(contraint_layout)


        val x = x()

        text.setOnClickListener {
            TransitionManager.beginDelayedTransition(contraint_layout)
            contraintSet2.applyTo(contraint_layout)
            if (supportFragmentManager.findFragmentByTag("text") == null)
                supportFragmentManager.beginTransaction().add(R.id.bottom_overlay, x, "text").commit()

        }
        close.setOnClickListener {
            TransitionManager.beginDelayedTransition(contraint_layout)
            contraintSet1.applyTo(contraint_layout)
            supportFragmentManager.beginTransaction().remove(x).commit()

        }

        MemeTemplate.loadLocalTemplates(this) {
            memeEditorView2.loadMemeTemplate(it[3])
        }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) makeFullScreen()
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

class x : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.text_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text_pager.adapter = Adapter(context!!)
        pager_tab.setupWithViewPager(text_pager)
    }


    class Adapter(context: Context) : ViewAdapter(context) {
        var titles = listOf("Color", "Size", "Font", "Style", "Stroke", "Background")

        val views = List(titles.size) {
            val hsv = HorizontalScrollView(context)
            hsv.addView(ColorChooser(context))
            hsv.isHorizontalScrollBarEnabled = false
            hsv
        }

        class EmptyFrag : Fragment()

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
