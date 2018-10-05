package com.memeit.backend.dataclasses


interface HomeElement{
    companion object {
        const val MEME_TYPE=0
        const val USER_SUGGESTION_TYPE=1
        const val TAG_SUGGESTION_TYPE=2
        const val MEME_TEMPLATE_SUGGESTION_TYPE=3
        const val AD_TYPE=4
    }
    val itemType:Int
}


class UserSuggestion(val users:List<User>): HomeElement {
    override val itemType: Int= HomeElement.USER_SUGGESTION_TYPE
}
class TagSuggestion(val tags:List<Tag>): HomeElement {
    override val itemType: Int= HomeElement.TAG_SUGGESTION_TYPE
}
class MemeTemplateSuggestion(val templates:List<String>): HomeElement {
    override val itemType: Int= HomeElement.MEME_TEMPLATE_SUGGESTION_TYPE
}
class AdElement : HomeElement {
    override val itemType: Int= HomeElement.AD_TYPE
}


