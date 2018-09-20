package com.innov8.memeit.CustomViews

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memegenerator.utils.fromDPToPX
import com.innov8.memegenerator.utils.log
import com.innov8.memegenerator.utils.toast
import com.innov8.memeit.Adapters.TagSuggestionAdapter
import com.innov8.memeit.R
import com.memeit.backend.MemeItMemes
import com.memeit.backend.dataclasses.Tag
import com.memeit.backend.utilis.OnCompleteListener

class ChipSearchToolbar : LinearLayout,MenuItem.OnActionExpandListener{
    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        editText.requestFocusFromTouch()
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        editText.setText("")
        return true
    }

    lateinit var editText: EditText
    lateinit var clearButton: ImageButton
    lateinit var popupWindow: PopupWindow
    var OnSearch: ((String, Array<String>) -> Unit)? =null
    constructor(context: Context) : super(context) {
        init()

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    lateinit var adapter: TagSuggestionAdapter


    private fun init() {
        val inflator = LayoutInflater.from(context);
        editText = inflator.inflate(R.layout.search_edit, null) as EditText

        //editText.inputType = InputType.TYPE_CLASS_TEXT
        editText.maxLines = 1
        editText.hint = "Search Memes"
        adapter = TagSuggestionAdapter(context)

        val v: View = inflator.inflate(R.layout.tag_suggestion, null)
        val list: RecyclerView = v.findViewById(R.id.sug_list)
        popupWindow = PopupWindow(v, 280.fromDPToPX(context), 180.fromDPToPX(context))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 2.fromDPToPX(context).toFloat()
        }
        list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        list.adapter = adapter

        adapter.OnDataChange = {
            if (it.isEmpty() && popupWindow.isShowing)
                popupWindow.dismiss()
            else if (it.isNotEmpty() ){
                popupWindow.showAsDropDown(editText, (-32).fromDPToPX(context), 0)
            }

        }
        adapter.OnItemClicked={
            val s=editText.text

            val i=s.lastIndexOf('#')

            s.replace(i+1,s.length,it.tag.toLowerCase()+" ")
        }




        clearButton = ImageButton(context)
        clearButton.setBackgroundColor(Color.TRANSPARENT)
        clearButton.setImageResource(R.drawable.ic_clear_black_24dp)

        clearButton.setOnClickListener {
            editText.setText("")
        }
        gravity=Gravity.CENTER_VERTICAL

        val lp = LayoutParams(200.fromDPToPX(context), LayoutParams.MATCH_PARENT)
        val lp2 = LayoutParams(56.fromDPToPX(context), LayoutParams.MATCH_PARENT)
        // layoutParams.width= ViewGroup.LayoutParams.MATCH_PARENT

        addView(editText, lp)
        addView(clearButton, lp2)

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                var hash = false
                var hashSI = -1
                if(!s.contains('#')){
                    x("")
                }else {

                    for (i in 0 until s.length) {
                        if (hash) {
                            if (s[i] == ' ') {/*
                                val span =  TextAppearanceSpan(context,android.R.style.TextAppearance_Small,ColorStateList.valueOf(0).)

                            editText.text.setSpan(span, hashSI, i, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)*/
                                hash = false
                                hashTagDone()

                            } else {
                                x(s.subSequence(hashSI + 1, i + 1).toString())
                            }
                        } else if (s[i] == '#') {
                            hash = true
                            hashSI = i
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
        editText.imeOptions=EditorInfo.IME_ACTION_SEARCH
        editText.inputType=InputType.TYPE_CLASS_TEXT

        editText.setOnEditorActionListener { _, actionId, _ ->
           if(actionId==EditorInfo.IME_ACTION_SEARCH){
               val text=getSearchText()
               val tags=getSearchTags()
               context.toast(text+" "+tags.joinToString(" "))
               OnSearch?.invoke(text,tags)
               true
           }
            false
        }

    }

    private val listener = object : OnCompleteListener<List<Tag>> {
        override fun onSuccess(t: List<Tag>) {
            log("SUCCESS")
            t.forEach { log(it) }
            adapter.addAll(t)
        }

        override fun onFailure(error: OnCompleteListener.Error?) {
            log("tag errored", error?.message ?: "")
        }

    }

    fun x(s: String) {
        log(s)
        adapter.updateFilter(s)
        MemeItMemes.getInstance().getPopularTags(s, 0, 5, listener)
    }

    fun hashTagDone() {
        adapter.clear()
    }

    fun getSearchText():String{
        return editText.text.split(" ")
                .filter { !it.startsWith('#') }
                .joinToString (" ")
    }
    fun getSearchTags():Array<String>{
        return editText.text.split(" ")
                .filter { it.startsWith('#')&&it.length>1 }
                .map { it.substring(1) }
                .toTypedArray()
    }



}