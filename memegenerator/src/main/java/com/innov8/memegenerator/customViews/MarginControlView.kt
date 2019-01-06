package com.innov8.memegenerator.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RadioGroup
import android.widget.ScrollView
import com.innov8.memegenerator.interfaces.LayoutEditInterface
import com.innov8.memegenerator.R
import com.innov8.memegenerator.utils.Listener
import com.memeit.backend.models.LayoutProperty
import com.warkiz.widget.IndicatorSeekBar

class MarginControlView : ScrollView {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private lateinit var marginLeftSeek: IndicatorSeekBar
    private lateinit var marginRightSeek: IndicatorSeekBar
    private lateinit var marginTopSeek: IndicatorSeekBar
    private lateinit var marginBottomSeek: IndicatorSeekBar

    var mode = R.id.radio_none
        set(value) {
            field = value
            val values = seekbars.map { it.progressFloat }
            when (field) {
                R.id.radio_all -> {
                    adjustAll(values.max()!!)
                    change(0, values.max()!!)
                }
                R.id.radio_left_right -> {
                    val v = values.subList(0, 2).max()!!
                    seekbars.subList(0, 2).forEach { it.setProgress(v) }
                    change(0, v)
                }
                R.id.radio_top_bottom -> {
                    val v = values.subList(2, 4).max()!!
                    seekbars.subList(2, 4).forEach { it.setProgress(v) }
                    change(2, v)
                }
            }
        }
    private lateinit var seekbars: List<IndicatorSeekBar>
    private fun adjustAll(progress: Float, except: IndicatorSeekBar? = null) =
            seekbars.filter { it != except }
                    .forEach { it.setProgress(progress) }

    private fun init() {

        val v = LayoutInflater.from(context).inflate(R.layout.margin_settings, this, false)
        val radioGroup: RadioGroup = v.findViewById(R.id.margin_radio_group)
        radioGroup.setOnCheckedChangeListener { _, id ->
            mode = id
        }

        marginLeftSeek = v.findViewById(R.id.margin_left_seek)
        marginRightSeek = v.findViewById(R.id.margin_right_seek)
        marginTopSeek = v.findViewById(R.id.margin_top_seek)
        marginBottomSeek = v.findViewById(R.id.margin_bottom_seek)
        seekbars = listOf(marginLeftSeek, marginRightSeek, marginTopSeek, marginBottomSeek)


        marginLeftSeek.onSeekChangeListener = Listener(onSeek = {
            if (it.fromUser) {
                when (mode) {
                    R.id.radio_left_right -> marginRightSeek.setProgress(it.progressFloat)
                    R.id.radio_all -> adjustAll(it.progressFloat, marginLeftSeek)

                }
                change(0, it.progressFloat)
            }
        })
        marginRightSeek.onSeekChangeListener = Listener(onSeek = {
            if (it.fromUser) {
                when (mode) {
                    R.id.radio_left_right -> marginLeftSeek.setProgress(it.progressFloat)
                    R.id.radio_all -> adjustAll(it.progressFloat, marginRightSeek)

                }
                change(1, it.progressFloat)
            }
        })
        marginTopSeek.onSeekChangeListener = Listener(onSeek = {
            if (it.fromUser) {
                when (mode) {
                    R.id.radio_top_bottom -> marginBottomSeek.setProgress(it.progressFloat)
                    R.id.radio_all -> adjustAll(it.progressFloat, marginTopSeek)
                }
                change(2, it.progressFloat)
            }
        })
        marginBottomSeek.onSeekChangeListener = Listener(onSeek = {
            if (it.fromUser) {
                when (mode) {
                    R.id.radio_top_bottom -> marginTopSeek.setProgress(it.progressFloat)
                    R.id.radio_all -> adjustAll(it.progressFloat, marginBottomSeek)

                }
                change(3, it.progressFloat)
            }
        })
        addView(v)

    }

    var layoutEditInterface: LayoutEditInterface? = null
    fun change(index: Int, progress: Float) {
        val p = progress.toInt()
        val x: () -> Unit = {
            when (index) {
                0 -> layoutEditInterface?.onLeftMargin(p)
                1 -> layoutEditInterface?.onRightMargin(p)
                2 -> layoutEditInterface?.onTopMargin(p)
                3 -> layoutEditInterface?.onBottomMargin(p)
            }
        }

        when (mode) {
            R.id.radio_none -> {
                x.invoke()
            }
            R.id.radio_all -> layoutEditInterface?.onAllMarginSet(p)
            R.id.radio_left_right -> {
                if (index < 2) {
                    layoutEditInterface?.onLeftMargin(p)
                    layoutEditInterface?.onRightMargin(p)
                } else x()

            }
            R.id.radio_top_bottom -> {
                if (index > 1) {
                    layoutEditInterface?.onTopMargin(p)
                    layoutEditInterface?.onBottomMargin(p)
                } else x()
            }

        }
    }


    fun applyMargin(layoutProperty: LayoutProperty) {
        marginLeftSeek.setProgress(layoutProperty.leftMargin.toFloat())
        marginRightSeek.setProgress(layoutProperty.rightMargin.toFloat())
        marginTopSeek.setProgress(layoutProperty.topMargin.toFloat())
        marginBottomSeek.setProgress(layoutProperty.bottomMargin.toFloat())
    }
}