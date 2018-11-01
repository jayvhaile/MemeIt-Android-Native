package com.innov8.memegenerator.Models

import android.graphics.Color
import com.innov8.memeit.commons.models.MyTypeFace
import com.innov8.memeit.commons.models.TextStyleProperty
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

data class TextPreset(val name: String, val textStyleProperty: TextStyleProperty) {
    companion object {
        fun loadPresets(onload: (List<TextPreset>) -> Unit) {
            launch(UI) {
                onload(withContext(CommonPool) {
                    listOf(
                            TextPreset("Normal", TextStyleProperty(
                                    20f, Color.WHITE, MyTypeFace.byName("Arial")!!,
                                    false, false, false,
                                    true, Color.BLACK, 10f
                            )),
                            TextPreset("Meme", TextStyleProperty(
                                    20f, Color.WHITE, MyTypeFace.byName("Impact")!!,
                                    false, false, true,
                                    true, Color.BLACK, 10f
                            )),
                            TextPreset("Red", TextStyleProperty(
                                    20f, Color.RED, MyTypeFace.byName("Pacifico")!!
                            )),
                            TextPreset("Dialog", TextStyleProperty(
                                    20f, Color.YELLOW, MyTypeFace.byName("Ubuntu")!!,
                                    false, false, false,
                                    false, Color.BLACK, 10f
                            ))
                    )
                })
            }
        }
    }
}
