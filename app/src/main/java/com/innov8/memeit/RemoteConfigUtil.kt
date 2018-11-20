package com.innov8.memeit

const val RCK_AD_FREQ = "home_ad_ofreq"
const val RCK_USER_SUG_FREQ = "home_user_sug_freq"
const val RCK_TAG_SUG_FREQ = "home_tag_sug__freq"
const val RCK_TEMPLATE_SUG_FREQ = "home_template_sug_freq"

const val RCK_AD_OFFSET = "home_ad_offset"
const val RCK_USER_SUG_OFFSET = "home_user_sug_offset"
const val RCK_TAG_SUG_OFFSET = "home_tag_sug_offset"
const val RCK_TEMPLATE_SUG_OFFSET = "home_template_sug_offset"

fun getDefaults(): MutableMap<String, Any> {
    return mutableMapOf(
            RCK_AD_FREQ to 10L,
            RCK_USER_SUG_FREQ to 12L,
            RCK_TAG_SUG_FREQ to 8L,
            RCK_TEMPLATE_SUG_FREQ to 15L,

            RCK_AD_OFFSET to 10L,
            RCK_USER_SUG_OFFSET to 3L,
            RCK_TAG_SUG_OFFSET to 8L,
            RCK_TEMPLATE_SUG_OFFSET to 5L
    )

}