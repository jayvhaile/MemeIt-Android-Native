package com.innov8.memegenerator.customViews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.innov8.memegenerator.memeEngine.TextEditListener
import com.innov8.memegenerator.models.TextStyleProperty
import com.innov8.memegenerator.R


class TextStyleView(val context: Context) {

    var textEditListener:TextEditListener?=null
    val styleView: View = LayoutInflater.from(context).inflate(R.layout.text_style_option,null,false)
    private val boldOpt= styleView.findViewById<ToggleImageButton>(R.id.opt_text_bold)!!
    private val italicOpt= styleView.findViewById<ToggleImageButton>(R.id.opt_text_bold)!!
    private val allCapOpt= styleView.findViewById<ToggleImageButton>(R.id.opt_text_bold)!!

    init {
        boldOpt.onCheckChanged={checked,fromUser->
            if(fromUser)textEditListener?.onTextSetBold(checked)
        }
        italicOpt.onCheckChanged={checked,fromUser->
            if(fromUser)textEditListener?.onTextSetItalic(checked)
        }
        allCapOpt.onCheckChanged={checked,fromUser->
            if(fromUser)textEditListener?.onTextSetAllCap(checked)
        }
    }
    fun setProperty(textStyleProperty: TextStyleProperty){
        boldOpt.setChecked(textStyleProperty.bold)
        italicOpt.setChecked(textStyleProperty.italic)
        allCapOpt.setChecked(textStyleProperty.allCap)
    }

}