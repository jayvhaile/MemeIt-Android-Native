package com.innov8.memegenerator.Models

import android.graphics.Color
import com.memeit.backend.models.MemeTextStyleProperty
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main

data class TextPreset(val name: String, val textStyleProperty: MemeTextStyleProperty) {
    companion object {
        fun loadPresets(onload: (List<TextPreset>) -> Unit) {
            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {

                onload(withContext(Dispatchers.Default) {
                    listOf(
                            TextPreset("Normal", MemeTextStyleProperty(
                                    20f, Color.WHITE, "Arial",
                                    false, false, false,
                                    true, Color.BLACK, 10f
                            )),
                            TextPreset("Meme", MemeTextStyleProperty(
                                    20f, Color.WHITE, "Impact",
                                    false, false, true,
                                    true, Color.BLACK, 10f
                            )),
                            TextPreset("Red", MemeTextStyleProperty(
                                    20f, Color.RED, "Pacifico"
                            )),
                            TextPreset("Dialog", MemeTextStyleProperty(
                                    20f, Color.YELLOW, "Ubuntu",
                                    false, false, false,
                                    false, Color.BLACK, 10f
                            ))
                    )
                })
            }
        }
    }
}
