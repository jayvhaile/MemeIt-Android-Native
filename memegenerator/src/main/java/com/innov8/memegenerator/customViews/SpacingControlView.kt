package com.innov8.memegenerator.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ScrollView
import com.innov8.memegenerator.interfaces.LayoutEditInterface
import com.innov8.memegenerator.R
import com.innov8.memegenerator.memeEngine.LinearImageLayout
import com.innov8.memegenerator.memeEngine.MemeLayout
import com.innov8.memegenerator.utils.Listener
import com.memeit.backend.models.GridImageLayoutProperty
import com.memeit.backend.models.LayoutProperty
import com.memeit.backend.models.LinearImageLayoutProperty
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


    private lateinit var horizontalSpacingSeek: IndicatorSeekBar
    private lateinit var verticalSpacingSeek4: IndicatorSeekBar
    private fun init() {
        val v = LayoutInflater.from(context).inflate(R.layout.spacing_settings, this, false)

        horizontalSpacingSeek = v.findViewById(R.id.spacing_h_seek)
        verticalSpacingSeek4 = v.findViewById(R.id.spacing_v_seek)

        horizontalSpacingSeek.onSeekChangeListener = Listener(onSeek = {
            if (it.fromUser) {
                layoutEditInterface?.onHorizontalSpacing(it.progress)
            }
        })
        verticalSpacingSeek4.onSeekChangeListener = Listener(onSeek = {
            if (it.fromUser) {
                layoutEditInterface?.onVertivalSpacing(it.progress)
            }
        })
        addView(v)
    }

    var layoutEditInterface: LayoutEditInterface? = null

    fun applySpacing(layoutProperty: LayoutProperty) {
        when (layoutProperty) {
            is LinearImageLayoutProperty -> {
                if (layoutProperty.orientation == LinearImageLayout.HORIZONTAL)
                    horizontalSpacingSeek.setProgress(layoutProperty.spacing.toFloat())
                else if (layoutProperty.orientation == LinearImageLayout.VERTICAL)
                    verticalSpacingSeek4.setProgress(layoutProperty.spacing.toFloat())
            }
            is GridImageLayoutProperty -> {
                horizontalSpacingSeek.setProgress(layoutProperty.hSpacing.toFloat())
                verticalSpacingSeek4.setProgress(layoutProperty.vSpacing.toFloat())
            }
        }
    }
}