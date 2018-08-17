package com.innov8.memegenerator

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import com.innov8.memegenerator.customViews.VTab
import com.innov8.memegenerator.fragments.*
import com.innov8.memegenerator.memeEngine.EditType
import com.innov8.memegenerator.memeEngine.ItemSelectedInterface
import com.innov8.memegenerator.memeEngine.MemeEditorInterface
import com.innov8.memegenerator.memeEngine.MemeEditorView
import com.innov8.memegenerator.models.MemeTemplate
import com.innov8.memegenerator.models.TextStyleProperty
import com.innov8.memegenerator.utils.log
import java.lang.IllegalArgumentException

class MemeEditorActivity : AppCompatActivity() ,ItemSelectedInterface{


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

        val json:String?=intent.getStringExtra("string")

        if(json!=null){
            val gson=Gson()
            val memeTemplate=gson.fromJson(json,MemeTemplate::class.java)
            Handler().postDelayed({
                log(memeEditorView.width,memeEditorView.height)
                memeEditorView.loadMemeTemplate(memeTemplate)
            },100)
        }
    }

    override fun onTextItemSelected(textStyleProperty: TextStyleProperty) {
        vPager.setCurrentItem(2,false)
    }

    private inner class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {


        override fun getCount(): Int = 5
        override fun getItem(pos: Int): Fragment =
                when (pos) {
                    0 -> MemeLayoutEditorFragment()
                    1 -> MemeImageEditorFragment()
                    2 ->{
                        val memeTextEditorFragment= MemeTextEditorFragment()
                        memeTextEditorFragment.textEditListener=memeEditorView.textEditListener
                        memeEditorView.itemSelectedInterface=memeTextEditorFragment
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
