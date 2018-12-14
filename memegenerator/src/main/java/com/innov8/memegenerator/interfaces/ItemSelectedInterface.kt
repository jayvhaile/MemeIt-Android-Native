package com.innov8.memegenerator.interfaces

import com.innov8.memeit.commons.models.TextStyleProperty

interface ItemSelectedInterface {

    fun onTextItemSelected(textStyleProperty: TextStyleProperty){}
}