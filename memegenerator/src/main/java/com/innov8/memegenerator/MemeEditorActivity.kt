package com.innov8.memegenerator

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.innov8.memegenerator.customViews.MyToolBar
import com.innov8.memegenerator.customViews.MyToolbarmenu
import com.innov8.memegenerator.customViews.VTab
import com.innov8.memegenerator.fragments.*
import com.innov8.memegenerator.memeEngine.EditType
import com.innov8.memegenerator.memeEngine.ItemSelectedInterface
import com.innov8.memegenerator.memeEngine.MemeEditorInterface
import com.innov8.memegenerator.memeEngine.MemeEditorView
import com.innov8.memegenerator.models.MemeTemplate
import com.innov8.memegenerator.models.TextStyleProperty

class MemeEditorActivity : AppCompatActivity(), ItemSelectedInterface {


    lateinit var vTab: VTab
    lateinit var vPager: androidx.viewpager.widget.ViewPager
    lateinit var memeEditorView: MemeEditorView
    lateinit var myToolBar: MyToolBar
    lateinit var fragments: List<MemeEditorFragment>
    var memeEditorInterfaces = mutableListOf<MemeEditorInterface>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.meme_editor_layout2)
        memeEditorView = findViewById(R.id.imageView3)
        vTab = findViewById(R.id.vtab)
        myToolBar = findViewById(R.id.toolbar)
        vPager = findViewById(R.id.pager)


        val memeTextEditorFragment = MemeTextEditorFragment()
        memeTextEditorFragment.textEditListener = memeEditorView.textEditListener
        memeTextEditorFragment.memeEditorView=memeEditorView
        memeEditorView.itemSelectedInterface = memeTextEditorFragment

        fragments = listOf(
                MemeLayoutEditorFragment(),
                MemeImageEditorFragment(),
                memeTextEditorFragment,
                MemeStickerEditorFragment(),
                MemePaintEditorFragment()
        )




        val adapter = MyPagerAdapter(supportFragmentManager)

        vPager.adapter = adapter

        vPager.offscreenPageLimit = 1

        val onVtab: (Int) -> Unit = { index ->
            vPager.currentItem = index
            myToolBar.setRightMenus(fragments[index].menus)
            fireOnEditTypeChanged(EditType.values()[index])
        }
        vTab.items = listOf(
                vTab.item(R.drawable.ic_bottom_layout, onVtab),
                vTab.item(R.drawable.ic_image_black, onVtab),
                vTab.item(R.drawable.ic_bottom_text, onVtab),
                vTab.item(R.drawable.ic_bottom_sticker, onVtab),
                vTab.item(R.drawable.ic_format_paint, onVtab)
        )

        myToolBar.setLeftMenus(listOf(
                MyToolbarmenu(R.drawable.ic_left_menu_done){
                    val bitmap=memeEditorView.captureMeme()
                    val intent= Intent(this,MemePosterActivity::class.java)
                    intent.putExtra("texts",memeEditorView.getTexts().toTypedArray())
                    MemePosterActivity.bitmap=bitmap
                    startActivity(intent)
                },
                MyToolbarmenu(R.drawable.ic_left_menu_preview)
        ))

        val json: String? = intent.getStringExtra("string")

        if (json != null) {
            val gson = Gson()
            val memeTemplate = gson.fromJson(json, MemeTemplate::class.java)
            memeEditorView.loadMemeTemplate(memeTemplate)
        }


    }

    override fun onTextItemSelected(textStyleProperty: TextStyleProperty) {
        vPager.setCurrentItem(2, false)
    }

    private inner class MyPagerAdapter(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm) {


        override fun getCount(): Int = fragments.count()
        override fun getItem(pos: Int): androidx.fragment.app.Fragment =fragments[pos]


    }

    private fun fireOnEditTypeChanged(editType: EditType) {
        memeEditorInterfaces.forEach { it.onEditTypeChanged(editType) }
    }
}
