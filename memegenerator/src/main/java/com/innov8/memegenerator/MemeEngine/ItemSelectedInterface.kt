package com.innov8.memegenerator.MemeEngine

import com.innov8.memegenerator.Models.TextStyleProperty

interface ItemSelectedInterface {

    fun onTextItemSelected(textStyleProperty: TextStyleProperty){}
}