package com.innov8.memeit.commons.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.amulyakhare.textdrawable.TextDrawable
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import com.innov8.memeit.commons.R
import com.innov8.memeit.commons.dp

class ProfileDraweeView : SimpleDraweeView {
    constructor(context: Context, text: String = "", color: Int = Color.RED) : super(context) {
        this.text = text
        this.color = color
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ProfileDraweeView, 0, 0)
        try {
            hasBorder = a.getBoolean(R.styleable.ProfileDraweeView_hasBorder, true)
        } finally {
            a.recycle()
        }
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }


    lateinit var textDrawable: TextDrawable
    private var text: String = ""
    private var color: Int = Color.RED

    fun setText(t: String) {
        this.text = t
        init()
    }

    var hasBorder = true

    fun init() {
        textDrawable = TextDrawable
                .builder()
                .beginConfig()
                .bold()
                .endConfig()
                .buildRound(text, color)
        val rp = RoundingParams.asCircle()
        if (hasBorder) rp.setBorder(Color.WHITE, 3f.dp(context))
        hierarchy.roundingParams = rp
        hierarchy.setPlaceholderImage(textDrawable)
    }
}