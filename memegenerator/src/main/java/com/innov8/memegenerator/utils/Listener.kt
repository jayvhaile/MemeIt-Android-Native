package com.innov8.memegenerator.utils

import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams

class Listener(val onSeek: ((SeekParams) -> Unit)? = null,
               val onStart: ((IndicatorSeekBar) -> Unit)? = null,
               val onStop: ((IndicatorSeekBar) -> Unit)? = null) : OnSeekChangeListener {
    override fun onSeeking(seekParams: SeekParams) {
        onSeek?.invoke(seekParams)
    }

    override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {
        onStart?.invoke(seekBar)
    }

    override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {
        onStop?.invoke(seekBar)
    }

}