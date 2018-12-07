package com.memeit.backend.models

data class Tag(val tag:String,val count:Int,val date:Long,val followed:Boolean=false)