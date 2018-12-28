package com.innov8.memegenerator.interfaces

import com.memeit.backend.models.MemeTextStyleProperty

interface ItemSelectedInterface {
    fun onTextItemSelected(textStyleProperty: MemeTextStyleProperty){}
}