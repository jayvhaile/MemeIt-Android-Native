package com.innov8.memegenerator.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ScrollView
import com.innov8.memegenerator.interfaces.LayoutEditInterface
import com.innov8.memegenerator.R
import com.innov8.memegenerator.utils.Listener
import com.warkiz.widget.IndicatorSeekBar

class SpacingControlView : ScrollView {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }




    private fun init() {
        val v = LayoutInflater.from(context).inflate(R.layout.spacing_settings, this, false)

        val spacing_h_seek = v.findViewById<IndicatorSeekBar>(R.id.spacing_h_seek)
        val spacing_v_seek = v.findViewById<IndicatorSeekBar>(R.id.spacing_v_seek)

        spacing_h_seek.onSeekChangeListener = Listener(onSeek = {
            if (it.fromUser) {
                layoutEditInterface?.onHorizontalSpacing(it.progress)
            }
        })
        spacing_v_seek.onSeekChangeListener = Listener(onSeek = {
            if (it.fromUser) {
                layoutEditInterface?.onVertivalSpacing(it.progress)
            }
        })
        addView(v)
    }
    var layoutEditInterface: LayoutEditInterface? = null
}