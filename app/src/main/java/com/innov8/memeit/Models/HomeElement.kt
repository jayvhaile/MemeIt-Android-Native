package com.innov8.memeit.Models

import com.memeit.backend.dataclasses.User

val MEME_TYPE=0
val USER_SUGGESTION_TYPE=1
val MEME_TEMPLATE_SUGGESTION_TYPE=2
val AD_TYPE=3
interface HomeElement{
    var itemType:Int
}


class UserSuggestion(val users:List<User>):HomeElement{
    override var itemType: Int= USER_SUGGESTION_TYPE
}
class MemeTemplateSuggestion(val templates:List<String>):HomeElement{
    override var itemType: Int= MEME_TEMPLATE_SUGGESTION_TYPE
}
class AdElement :HomeElement {
    override var itemType: Int= AD_TYPE

}


