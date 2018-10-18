package com.innov8.memegenerator.MemeEngine

import com.innov8.memeit.commons.models.TextStyleProperty

interface ItemSelectedInterface {

    fun onTextItemSelected(textStyleProperty: TextStyleProperty){}
}