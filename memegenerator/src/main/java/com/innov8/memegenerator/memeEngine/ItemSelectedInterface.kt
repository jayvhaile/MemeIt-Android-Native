package com.innov8.memegenerator.memeEngine

import com.innov8.memegenerator.models.TextStyleProperty

interface ItemSelectedInterface {

    fun onTextItemSelected(textStyleProperty: TextStyleProperty){}
}