package com.innov8.memegenerator.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memegenerator.R
import com.innov8.memegenerator.adapters.TextPresetsAdapter
import com.innov8.memegenerator.interfaces.TextEditListener
import com.innov8.memeit.commons.mapTo
import com.memeit.backend.models.MemeTextStyleProperty
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main

class TextPresetFragment : Fragment() {
    var textEditListener: TextEditListener? = null
    private val textPresetsAdapter by lazy {
        TextPresetsAdapter(context!!).apply {
            onItemClick = {
                textEditListener?.onApplyAll(it, false)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadPresets { textPresetsAdapter.setAll(it) }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_text_presets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.frag_text_preset_list).apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = textPresetsAdapter
        }
    }

    companion object {
        fun loadPresets(onLoad: (List<Pair<MemeTextStyleProperty, MemeTextStyleProperty>>) -> Unit) {
            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                onLoad(withContext(Dispatchers.Default) {
                    listOf(
                            MemeTextStyleProperty(
                                    20f, Color.BLACK, "Aileron",
                                    false, false, false,
                                    false, Color.BLACK, 0f,
                                    Color.TRANSPARENT, Layout.Alignment.ALIGN_NORMAL
                            ).mapTo { this to this.copy(bgColor = Color.WHITE) },
                            MemeTextStyleProperty(
                                    20f, Color.WHITE, "Arial",
                                    false, false, false,
                                    true, Color.BLACK, 10f
                            ).mapTo { this to this },
                            MemeTextStyleProperty(
                                    20f, Color.WHITE, "Impact",
                                    false, false, true,
                                    true, Color.BLACK, 10f
                            ).mapTo { this to this },
                            MemeTextStyleProperty(
                                    20f, Color.BLACK, "Montserrat",
                                    false, false, false,
                                    false
                            ).mapTo { this to this.copy(bgColor = Color.WHITE) },
                            MemeTextStyleProperty(
                                    20f, Color.RED, "Pacifico"
                            ).mapTo { this to this },
                            MemeTextStyleProperty(
                                    20f, Color.YELLOW, "Ubuntu",
                                    false, false, false,
                                    false, Color.BLACK, 10f
                            ).mapTo { this to this }
                    )
                })
            }
        }

    }

}