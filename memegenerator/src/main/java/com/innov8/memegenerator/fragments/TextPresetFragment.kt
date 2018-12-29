package com.innov8.memegenerator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memegenerator.R
import com.innov8.memegenerator.adapters.TextPresetsAdapter
import com.innov8.memegenerator.interfaces.TextEditListener
import com.innov8.memegenerator.models.TextPreset
import com.memeit.backend.models.MemeTextStyleProperty

class TextPresetFragment : Fragment() {
    var textEditListener: TextEditListener? = null
    var textStyleProperty: MemeTextStyleProperty? = null
    private lateinit var textPresetsAdapter: TextPresetsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textPresetsAdapter = TextPresetsAdapter(context!!)
        textPresetsAdapter.onItemClick = {
            textEditListener?.onApplyAll(it.textStyleProperty, false)
        }
        TextPreset.loadPresets { textPresetsAdapter.setAll(it) }

    }

    private lateinit var presetList: RecyclerView
    private lateinit var presetAdd: Button
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_text_presets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presetList = view.findViewById(R.id.frag_text_preset_list)
        presetAdd = view.findViewById(R.id.frag_text_preset_add)
        presetList.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        presetList.adapter = textPresetsAdapter
    }

}