package com.innov8.memegenerator

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.innov8.memegenerator.customViews.MyToolbarmenu
import com.innov8.memegenerator.fragments.*
import com.innov8.memegenerator.memeEngine.EditType
import com.innov8.memegenerator.memeEngine.ItemSelectedInterface
import com.innov8.memegenerator.memeEngine.MemeEditorInterface
import com.innov8.memegenerator.memeEngine.MemeStickerView
import com.innov8.memegenerator.models.MemeTemplate
import com.innov8.memegenerator.models.TextStyleProperty
import com.innov8.memegenerator.utils.AsyncLoader
import kotlinx.android.synthetic.main.meme_editor_layout2.*

class MemeEditorActivity : AppCompatActivity(), ItemSelectedInterface {


    lateinit var fragments: List<MemeEditorFragment>
    private var memeEditorInterfaces = mutableListOf<MemeEditorInterface>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.meme_editor_layout2)
        val memeLayoutEditorFragment=MemeLayoutEditorFragment()
        memeLayoutEditorFragment.layoutEditInterface=memeEditorView.layoutEditInterface

        val memeTextEditorFragment = MemeTextEditorFragment()
        memeTextEditorFragment.textEditListener = memeEditorView.textEditListener
        memeTextEditorFragment.memeEditorView = memeEditorView
        memeEditorView.itemSelectedInterface = memeTextEditorFragment

        val memeStickerEditorFragment = MemeStickerEditorFragment()
        memeStickerEditorFragment.memeEditorView = memeEditorView


        memeStickerEditorFragment.setOnStickerSelected { url ->
            AsyncLoader {
                val x=url.substring(9)
                BitmapFactory.decodeStream(assets.open(x))
            }.load {
                val memeStickerView = MemeStickerView(this, it)
                memeEditorView.addMemeItemView(memeStickerView)
            }
        }
        fragments = listOf(
                memeLayoutEditorFragment,
                MemeImageEditorFragment(),
                memeTextEditorFragment,
                memeStickerEditorFragment,
                MemePaintEditorFragment()
        )
        vtab.select(2)


        val adapter = MyPagerAdapter(supportFragmentManager)

        pager.adapter = adapter

        pager.offscreenPageLimit = 1

        val onSelect: (Int) -> Unit = { index ->
            pager.currentItem = index
            toolbar.setRightMenus(fragments[index].menus)
            fireOnEditTypeChanged(EditType.values()[index])
        }
        vtab.items = listOf(
                vtab.item(R.drawable.ic_bottom_layout, onSelect),
                vtab.item(R.drawable.ic_image_black, onSelect),
                vtab.item(R.drawable.ic_bottom_text, onSelect),
                vtab.item(R.drawable.ic_bottom_sticker, onSelect),
                vtab.item(R.drawable.ic_format_paint, onSelect)
        )

        toolbar.setLeftMenus(listOf(
                MyToolbarmenu(R.drawable.ic_left_menu_done) {
                    val bitmap = memeEditorView.captureMeme()
                    val intent = Intent(this, MemePosterActivity::class.java)
                    intent.putExtra("texts", memeEditorView.getTexts().toTypedArray())
                    MemePosterActivity.bitmap = bitmap
                    startActivity(intent)
                },
                MyToolbarmenu(R.drawable.ic_left_menu_preview) {
                    memeEditorView.scaleX = memeEditorView.scaleX * 1.2f
                    memeEditorView.scaleY = memeEditorView.scaleY * 1.2f
                }
        ))

        val json: String? = intent.getStringExtra("string")
        val uri: String? = intent.getStringExtra("uri")

        if (json != null) {
            val gson = Gson()
            val memeTemplate = gson.fromJson(json, MemeTemplate::class.java)
            memeEditorView.loadMemeTemplate(memeTemplate)
        } else if (uri != null) {
            AsyncLoader<Bitmap> {
                val stream = contentResolver.openInputStream(Uri.parse(uri))
                BitmapFactory.decodeStream(stream)
            }.load {
                memeEditorView.loadBitmab(it)
            }

        }


    }

    override fun onTextItemSelected(textStyleProperty: TextStyleProperty) {
        pager.setCurrentItem(2, false)
    }

    private inner class MyPagerAdapter(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm) {


        override fun getCount(): Int = fragments.count()
        override fun getItem(pos: Int): androidx.fragment.app.Fragment = fragments[pos]


    }

    private fun fireOnEditTypeChanged(editType: EditType) {
        memeEditorInterfaces.forEach { it.onEditTypeChanged(editType) }
    }
}
