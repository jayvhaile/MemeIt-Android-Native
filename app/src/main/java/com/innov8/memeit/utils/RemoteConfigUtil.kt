package com.innov8.memeit.utils

const val RCK_AD_PERIOD = "home_ad_period"
const val RCK_USER_SUG_PERIOD = "home_user_sug_period"
const val RCK_TAG_SUG_PERIOD = "home_tag_sug__period"
const val RCK_TEMPLATE_SUG_PERIOD = "home_template_sug_period"

const val RCK_AD_OFFSET = "home_ad_offset"
const val RCK_USER_SUG_OFFSET = "home_user_sug_offset"
const val RCK_TAG_SUG_OFFSET = "home_tag_sug_offset"
const val RCK_TEMPLATE_SUG_OFFSET = "home_template_sug_offset"

fun getDefaults(): MutableMap<String, Any> {
    return mutableMapOf(
            RCK_AD_PERIOD to 10L,
            RCK_USER_SUG_PERIOD to 12L,
            RCK_TAG_SUG_PERIOD to 8L,
            RCK_TEMPLATE_SUG_PERIOD to 15L,

            RCK_AD_OFFSET to 10L,
            RCK_USER_SUG_OFFSET to 3L,
            RCK_TAG_SUG_OFFSET to 8L,
            RCK_TEMPLATE_SUG_OFFSET to 5L
    )
}