package com.innov8.memegenerator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.innov8.memegenerator.fragments.MemeTextEditorFragment
import com.innov8.memegenerator.memeEngine.MemeEditorView
import com.innov8.memegenerator.memeEngine.MemeTextView
import com.innov8.memegenerator.utils.*

class MemeTemplateMaker : AppCompatActivity() {
    lateinit var memeEditorView:MemeEditorView
    lateinit var memeTextEditorFragment: MemeTextEditorFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_template_maker)
        memeEditorView=findViewById(R.id.memeEditorView)
        memeTextEditorFragment= MemeTextEditorFragment()
        memeTextEditorFragment.textEditListener=memeEditorView.textEditListener
        supportFragmentManager.replace(R.id.holder,memeTextEditorFragment)

        findViewById<Button>(R.id.addtext).setOnClickListener {
            val memeTextView= MemeTextView(this,400,100)
            memeTextView.text="Text"
            memeEditorView.addMemeItemView(memeTextView)
        }
        findViewById<Button>(R.id.removetext).setOnClickListener {
            memeEditorView.removeSelectedItem()
        }
        findViewById<Button>(R.id.next).setOnClickListener {
           loadNext()
        }
        loadNext()
    }

    val meme_template_count=2
    var meme_template_index=1
    fun loadNext(){
        if(meme_template_index<=meme_template_count) {
            var bitmap = loadBitmap(getDrawableIdByName("got$meme_template_index"), .3f)
            memeEditorView.generateAllTextProperty().forEach {
                val str=it.toString()
                log("TextProperty",str)
            }


            memeEditorView.clearMemeItems()
            memeEditorView.image = bitmap
            meme_template_index++

        }else{
            this.toast("Finished")
        }
    }
}
