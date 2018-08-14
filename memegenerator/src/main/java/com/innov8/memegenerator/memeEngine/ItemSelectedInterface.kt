package com.innov8.memegenerator.memeEngine

import com.innov8.memegenerator.models.TextProperty

interface ItemSelectedInterface {

    fun onTextItemSelected(textProperty: TextProperty){}
}