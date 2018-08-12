package com.innov8.memegenerator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.innov8.memegenerator.custom_views.VTab
import com.innov8.memegenerator.meme_engine.*

class MemeEditorActivity : AppCompatActivity() {
    lateinit var vTab: VTab
    lateinit var vPager: ViewPager
    lateinit var memeEditorView:MemeEditorView
    var memeEditorInterfaces= mutableListOf<MemeEditorInterface>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.meme_editor_layout2)
        vTab = findViewById(R.id.vtab)
        vPager = findViewById(R.id.pager)
        memeEditorView = findViewById(R.id.imageView3)
        val adapter = MyPagerAdapter(supportFragmentManager)
        vPager.adapter = adapter
        vPager.offscreenPageLimit=1

        val onVtab: (Int) -> Unit = { index ->
            vPager.currentItem = index
            fireOnEditTypeChanged(EditType.values()[index])
        }
        vTab.items = listOf(
                vTab.item(R.drawable.ic_bottom_layout, onVtab),
                vTab.item(R.drawable.ic_image_black, onVtab),
                vTab.item(R.drawable.ic_bottom_text, onVtab),
                vTab.item(R.drawable.ic_bottom_sticker, onVtab),
                vTab.item(R.drawable.ic_format_paint, onVtab)
        )
        val memeItemView=MemeItemView(this,400,100)
        val memeTextView=MemeTextView(this,400,100)
        memeTextView.text="Hello"
        memeTextView.setOnClickListener {

        }
        memeEditorView.addMemeItemView(memeTextView)
    }

    private inner class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int = 5
        override fun getItem(pos: Int): Fragment =
                when (pos) {
                    0 -> MemeLayoutEditorFragment()
                    1 -> MemeImageEditorFragment()
                    2 ->{
                        val memeTextEditorFragment=MemeTextEditorFragment()
                        memeTextEditorFragment.textEditInterface=memeEditorView.textEditInterface
                        memeTextEditorFragment
                    }
                    3 -> MemeStickerEditorFragment()
                    4 -> MemePaintEditorFragment()
                    else -> throw IllegalArgumentException("should be 0-4")
                }
    }



    private fun fireOnEditTypeChanged(editType: EditType){
        memeEditorInterfaces.forEach { it.onEditTypeChanged(editType) }
    }

}
