package com.innov8.memegenerator.models

data class MemeTemplate(val label:String, val imageURL:String, val dataSource: Byte= LOCAL_DATA_SOURCE, val textProperties:List<TextProperty>){
    companion object {
        val LOCAL_DATA_SOURCE:Byte=0
        val SERVER_DATA_SOURCE:Byte=1
    }

}